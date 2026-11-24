package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AgencyCatalog
import com.example.data.PreferencesRepository
import com.example.model.AdminAiAddStatus
import com.example.model.AdminAnnouncement
import com.example.model.AdminCustomTool
import com.example.model.AiMasterMessage
import com.example.model.AiMasterStatus
import com.example.model.AppLanguage
import com.example.model.GeneratedWebsiteResult
import com.example.model.SavedBlueprint
import com.example.model.SavedInquiry
import com.example.model.WebsiteToolType
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "WebShaktiViewModel"

data class WebShaktiUiState(
    val language: AppLanguage = AppLanguage.EN,
    val whatsAppNumber: String = PreferencesRepository.DEFAULT_WHATSAPP_NUMBER,
    val webhookUrl: String = "",
    val savedInquiries: List<SavedInquiry> = emptyList(),
    val savedBlueprints: List<SavedBlueprint> = emptyList(),
    val selectedTool: WebsiteToolType = WebsiteToolType.BUSINESS_WEBSITE,
    val lastGeneratedSite: GeneratedWebsiteResult? = null,
    val prefilledWebsiteType: WebsiteToolType? = null,
    val prefilledRequirements: String? = null,
    val contactInitialTab: Int = 1, // 0 = Contact Form, 1 = Custom Website Request
    val aiMasterMessages: List<AiMasterMessage> = emptyList(),
    val aiMasterStatus: AiMasterStatus = AiMasterStatus.Idle,
    val isAdminUnlocked: Boolean = false,
    val adminUnlockError: String? = null,
    val adminCustomTools: List<AdminCustomTool> = emptyList(),
    val adminAnnouncements: List<AdminAnnouncement> = emptyList(),
    val adminAiAddStatus: AdminAiAddStatus = AdminAiAddStatus.Idle
)

class WebShaktiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application.applicationContext)

    private val aiMasterModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are AI Master inside WEBSHAKTI AI, an expert bilingual (English & Hindi) " +
                        "website mentor, web developer (HTML5, CSS3, SEO, Schema, Hosting/DNS), and " +
                        "digital work assistant. Users come to you to clear doubts about their work, " +
                        "websites, coding bugs, business setup online, or how to customize/host exported HTML sites. " +
                        "Reply clearly, practically, and step-by-step in the same language the user uses " +
                        "(Hindi, Hinglish, or English). Keep formatting clean and easy to read on mobile."
                )
            }
        )
    }

    private val adminToolGeneratorModel by lazy {
        val toolSchema = Schema.obj(
            properties = mapOf(
                "titleEn" to Schema.string(description = "Short English title of the new website generator tool"),
                "titleHi" to Schema.string(description = "Short Hindi title of the new website generator tool"),
                "subtitleEn" to Schema.string(description = "One-sentence English description of what this website tool builds"),
                "subtitleHi" to Schema.string(description = "One-sentence Hindi description of what this website tool builds"),
                "defaultBrandHint" to Schema.string(description = "Sample website brand name for this niche"),
                "sections" to Schema.array(
                    items = Schema.string(),
                    description = "4 to 5 key website layout section titles"
                ),
                "customHtmlTemplate" to Schema.string(
                    description = "Complete clean responsive HTML5+CSS single-page website using white (#FFFFFF) background, black (#090D16) text, and #155EEF accent color"
                )
            )
        )
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = toolSchema
            },
            systemInstruction = content {
                text(
                    "You are the Admin Studio Architect for WEBSHAKTI AI. Given an admin instruction, " +
                        "design a practical new Website Generator Tool with bilingual English & Hindi labels, " +
                        "4-5 clear page sections, and a complete clean responsive HTML5+CSS template."
                )
            }
        )
    }

    private val adminAnnouncementGeneratorModel by lazy {
        val bannerSchema = Schema.obj(
            properties = mapOf(
                "title" to Schema.string(description = "Crisp headline for the studio update or announcement"),
                "message" to Schema.string(description = "Helpful 1-2 sentence message for website users"),
                "badge" to Schema.string(description = "Short 1-2 word badge label such as NEW TOOL or UPDATE")
            )
        )
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = bannerSchema
            },
            systemInstruction = content {
                text(
                    "You write concise, honest, user-friendly studio updates for WEBSHAKTI AI users. " +
                        "Match the language (Hindi or English) of the admin's request."
                )
            }
        )
    }

    private val selectedToolFlow = MutableStateFlow(WebsiteToolType.BUSINESS_WEBSITE)
    private val lastGeneratedSiteFlow = MutableStateFlow<GeneratedWebsiteResult?>(null)
    private val prefilledTypeFlow = MutableStateFlow<WebsiteToolType?>(null)
    private val prefilledRequirementsFlow = MutableStateFlow<String?>(null)
    private val contactTabFlow = MutableStateFlow(1)
    private val aiMasterMessagesFlow = MutableStateFlow<List<AiMasterMessage>>(emptyList())
    private val aiMasterStatusFlow = MutableStateFlow<AiMasterStatus>(AiMasterStatus.Idle)
    private val isAdminUnlockedFlow = MutableStateFlow(false)
    private val adminUnlockErrorFlow = MutableStateFlow<String?>(null)
    private val adminAiAddStatusFlow = MutableStateFlow<AdminAiAddStatus>(AdminAiAddStatus.Idle)

    private val configStateFlow = combine(
        repository.languageFlow,
        repository.whatsAppNumberFlow,
        repository.webhookUrlFlow
    ) { lang, wa, webhook ->
        Triple(lang, wa, webhook)
    }

    private val localDataFlow = combine(
        repository.savedInquiriesFlow,
        repository.savedBlueprintsFlow,
        repository.adminCustomToolsFlow,
        repository.adminAnnouncementsFlow
    ) { inquiries, blueprints, adminTools, adminNotices ->
        DataBundle(inquiries, blueprints, adminTools, adminNotices)
    }

    private val formPrefillFlow = combine(
        prefilledTypeFlow,
        prefilledRequirementsFlow,
        contactTabFlow
    ) { type, req, tab ->
        Triple(type, req, tab)
    }

    private val aiMasterFlow = combine(
        aiMasterMessagesFlow,
        aiMasterStatusFlow
    ) { msgs, status ->
        msgs to status
    }

    private val adminSessionFlow = combine(
        isAdminUnlockedFlow,
        adminUnlockErrorFlow,
        adminAiAddStatusFlow
    ) { unlocked, err, aiStatus ->
        Triple(unlocked, err, aiStatus)
    }

    private val studioStateFlow = combine(
        selectedToolFlow,
        lastGeneratedSiteFlow,
        formPrefillFlow
    ) { tool, siteResult, prefill ->
        Triple(tool, siteResult, prefill)
    }

    private val combinedAiFlow = combine(
        aiMasterFlow,
        adminSessionFlow
    ) { master, admin ->
        master to admin
    }

    val uiState: StateFlow<WebShaktiUiState> = combine(
        configStateFlow,
        localDataFlow,
        studioStateFlow,
        combinedAiFlow
    ) { cfg, data, studio, aiCombo ->
        val master = aiCombo.first
        val admin = aiCombo.second
        WebShaktiUiState(
            language = cfg.first,
            whatsAppNumber = cfg.second,
            webhookUrl = cfg.third,
            savedInquiries = data.inquiries,
            savedBlueprints = data.blueprints,
            selectedTool = studio.first,
            lastGeneratedSite = studio.second,
            prefilledWebsiteType = studio.third.first,
            prefilledRequirements = studio.third.second,
            contactInitialTab = studio.third.third,
            aiMasterMessages = master.first,
            aiMasterStatus = master.second,
            isAdminUnlocked = admin.first,
            adminUnlockError = admin.second,
            adminCustomTools = data.adminTools,
            adminAnnouncements = data.adminAnnouncements,
            adminAiAddStatus = admin.third
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WebShaktiUiState()
    )

    private data class DataBundle(
        val inquiries: List<SavedInquiry>,
        val blueprints: List<SavedBlueprint>,
        val adminTools: List<AdminCustomTool>,
        val adminAnnouncements: List<AdminAnnouncement>
    )

    fun toggleLanguage() {
        val next = if (uiState.value.language == AppLanguage.EN) AppLanguage.HI else AppLanguage.EN
        viewModelScope.launch { repository.setLanguage(next) }
    }

    fun askAiMaster(question: String) {
        val trimmed = question.trim()
        if (trimmed.isBlank()) return
        val status = aiMasterStatusFlow.value
        if (status is AiMasterStatus.Loading || status is AiMasterStatus.Streaming) return

        val isHi = uiState.value.language == AppLanguage.HI
        val previousMessages = aiMasterMessagesFlow.value
        val userEntry = AiMasterMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = trimmed,
            timestamp = currentTimestamp()
        )
        aiMasterMessagesFlow.value = previousMessages + userEntry
        aiMasterStatusFlow.value = AiMasterStatus.Loading

        viewModelScope.launch {
            val accumulatedText = StringBuilder()
            try {
                val historyContents = previousMessages.takeLast(8).map { msg ->
                    content(if (msg.isUser) "user" else "model") {
                        text(msg.text)
                    }
                }
                val conversationPrompt = historyContents + content("user") {
                    text(trimmed)
                }

                withTimeout(60_000L) {
                    aiMasterModel.generateContentStream(conversationPrompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulatedText.append(chunkText)
                            aiMasterStatusFlow.value = AiMasterStatus.Streaming(accumulatedText.toString())
                        }
                    }
                }

                val finalAnswer = accumulatedText.toString().trim()
                if (finalAnswer.isNotBlank()) {
                    val botEntry = AiMasterMessage(
                        id = UUID.randomUUID().toString(),
                        isUser = false,
                        text = finalAnswer,
                        timestamp = currentTimestamp()
                    )
                    aiMasterMessagesFlow.value = aiMasterMessagesFlow.value + botEntry
                    aiMasterStatusFlow.value = AiMasterStatus.Idle
                } else {
                    aiMasterStatusFlow.value = AiMasterStatus.Error(
                        if (isHi) "AI Master से कोई उत्तर प्राप्त नहीं हुआ। कृपया पुनः प्रयास करें।"
                        else "AI Master returned an empty response. Please try again."
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Master request timed out", e)
                aiMasterStatusFlow.value = AiMasterStatus.Error(
                    if (isHi) "अनुरोध 60 सेकंड में टाइम आउट हो गया। कृपया पुनः प्रयास करें।"
                    else "Request timed out after 60 seconds. Please try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Master generation failed", e)
                aiMasterStatusFlow.value = AiMasterStatus.Error(
                    e.localizedMessage
                        ?: if (isHi) "AI Master से कनेक्ट करने में त्रुटि हुई।"
                        else "Failed to get a response from AI Master."
                )
            }
        }
    }

    fun clearAiMasterChat() {
        aiMasterMessagesFlow.value = emptyList()
        aiMasterStatusFlow.value = AiMasterStatus.Idle
    }

    fun dismissAiMasterError() {
        if (aiMasterStatusFlow.value is AiMasterStatus.Error) {
            aiMasterStatusFlow.value = AiMasterStatus.Idle
        }
    }

    fun unlockAdminPanel(passcode: String): Boolean {
        val isHi = uiState.value.language == AppLanguage.HI
        return if (passcode.trim() == PreferencesRepository.ADMIN_PASSCODE) {
            isAdminUnlockedFlow.value = true
            adminUnlockErrorFlow.value = null
            true
        } else {
            isAdminUnlockedFlow.value = false
            adminUnlockErrorFlow.value = if (isHi) {
                "गलत पासवर्ड! एडमिन पैनल खोलने के लिए सही पासवर्ड दर्ज करें।"
            } else {
                "Incorrect passcode! Enter the valid Admin passcode to unlock."
            }
            false
        }
    }

    fun lockAdminPanel() {
        isAdminUnlockedFlow.value = false
        adminUnlockErrorFlow.value = null
        adminAiAddStatusFlow.value = AdminAiAddStatus.Idle
    }

    fun clearAdminUnlockError() {
        adminUnlockErrorFlow.value = null
    }

    fun clearAdminAiAddStatus() {
        adminAiAddStatusFlow.value = AdminAiAddStatus.Idle
    }

    fun addAdminToolViaAi(prompt: String) {
        if (!isAdminUnlockedFlow.value) return
        val trimmed = prompt.trim()
        if (trimmed.isBlank()) return
        if (adminAiAddStatusFlow.value is AdminAiAddStatus.Loading) return

        val isHi = uiState.value.language == AppLanguage.HI
        adminAiAddStatusFlow.value = AdminAiAddStatus.Loading

        viewModelScope.launch {
            try {
                val response = withTimeout(60_000L) {
                    adminToolGeneratorModel.generateContent(trimmed)
                }
                val rawJson = response.text
                if (rawJson.isNullOrBlank()) {
                    adminAiAddStatusFlow.value = AdminAiAddStatus.Error(
                        if (isHi) "AI से खाली उत्तर मिला। कृपया पुनः प्रयास करें।"
                        else "AI returned an empty response. Please try again."
                    )
                    return@launch
                }

                val json = JSONObject(rawJson)
                val titleEn = json.optString("titleEn").ifBlank { "Custom Website Tool" }
                val titleHi = json.optString("titleHi").ifBlank { titleEn }
                val subtitleEn = json.optString("subtitleEn").ifBlank { trimmed }
                val subtitleHi = json.optString("subtitleHi").ifBlank { subtitleEn }
                val brandHint = json.optString("defaultBrandHint").ifBlank { titleEn }
                val secArr = json.optJSONArray("sections")
                val sections = buildList {
                    if (secArr != null) {
                        for (i in 0 until secArr.length()) {
                            val s = secArr.optString(i).trim()
                            if (s.isNotBlank()) add(s)
                        }
                    }
                }.ifEmpty { listOf("Hero Banner", "Key Highlights", "Services Grid", "Contact & Footer") }
                val customHtml = json.optString("customHtmlTemplate").ifBlank {
                    buildHtmlCode(
                        WebsiteToolType.BUSINESS_WEBSITE,
                        brandHint,
                        subtitleEn,
                        "Get Started",
                        "#155EEF",
                        isHi
                    )
                }

                val newTool = AdminCustomTool(
                    id = UUID.randomUUID().toString(),
                    titleEn = titleEn,
                    titleHi = titleHi,
                    subtitleEn = subtitleEn,
                    subtitleHi = subtitleHi,
                    baseType = WebsiteToolType.BUSINESS_WEBSITE,
                    defaultBrandHint = brandHint,
                    sections = sections,
                    customHtmlTemplate = customHtml,
                    addedByAi = true,
                    timestamp = currentTimestamp()
                )
                repository.addAdminCustomTool(newTool)
                adminAiAddStatusFlow.value = AdminAiAddStatus.Success(
                    if (isHi) "AI द्वारा नया वेबसाइट टूल जोड़ा गया: $titleHi"
                    else "Added new website tool via AI: $titleEn"
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Admin AI tool generation timed out", e)
                adminAiAddStatusFlow.value = AdminAiAddStatus.Error(
                    if (isHi) "अनुरोध 60 सेकंड में टाइम आउट हो गया।"
                    else "Request timed out after 60 seconds."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Admin AI tool generation failed", e)
                adminAiAddStatusFlow.value = AdminAiAddStatus.Error(
                    e.localizedMessage
                        ?: if (isHi) "AI द्वारा टूल जोड़ने में त्रुटि हुई।"
                        else "Failed to generate and add tool via AI."
                )
            }
        }
    }

    fun addAdminAnnouncementViaAi(prompt: String) {
        if (!isAdminUnlockedFlow.value) return
        val trimmed = prompt.trim()
        if (trimmed.isBlank()) return
        if (adminAiAddStatusFlow.value is AdminAiAddStatus.Loading) return

        val isHi = uiState.value.language == AppLanguage.HI
        adminAiAddStatusFlow.value = AdminAiAddStatus.Loading

        viewModelScope.launch {
            try {
                val response = withTimeout(60_000L) {
                    adminAnnouncementGeneratorModel.generateContent(trimmed)
                }
                val rawJson = response.text
                if (rawJson.isNullOrBlank()) {
                    adminAiAddStatusFlow.value = AdminAiAddStatus.Error(
                        if (isHi) "AI से खाली उत्तर मिला।" else "AI returned an empty response."
                    )
                    return@launch
                }

                val json = JSONObject(rawJson)
                val title = json.optString("title").ifBlank { "Studio Update" }
                val message = json.optString("message").ifBlank { trimmed }
                val badge = json.optString("badge").ifBlank { "AI UPDATE" }

                val item = AdminAnnouncement(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    message = message,
                    badge = badge,
                    addedByAi = true,
                    timestamp = currentTimestamp()
                )
                repository.addAdminAnnouncement(item)
                adminAiAddStatusFlow.value = AdminAiAddStatus.Success(
                    if (isHi) "AI द्वारा अपडेट जोड़ा गया: $title"
                    else "Added studio update via AI: $title"
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Admin AI announcement timed out", e)
                adminAiAddStatusFlow.value = AdminAiAddStatus.Error(
                    if (isHi) "अनुरोध 60 सेकंड में टाइम आउट हो गया।"
                    else "Request timed out after 60 seconds."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Admin AI announcement failed", e)
                adminAiAddStatusFlow.value = AdminAiAddStatus.Error(
                    e.localizedMessage
                        ?: if (isHi) "AI द्वारा अपडेट जोड़ने में त्रुटि हुई।"
                        else "Failed to create studio update via AI."
                )
            }
        }
    }

    fun addAdminToolManually(
        title: String,
        subtitle: String,
        sectionsCsv: String,
        baseType: WebsiteToolType
    ) {
        if (!isAdminUnlockedFlow.value) return
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) return
        val cleanSubtitle = subtitle.trim().ifBlank { "Custom Admin Website Template" }
        val sections = sectionsCsv.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .ifEmpty { listOf("Hero Header", "Features Grid", "About Section", "Contact Footer") }
        val isHi = uiState.value.language == AppLanguage.HI
        val html = buildHtmlCode(baseType, cleanTitle, cleanSubtitle, "Get Started", "#155EEF", isHi)
        val newTool = AdminCustomTool(
            id = UUID.randomUUID().toString(),
            titleEn = cleanTitle,
            titleHi = cleanTitle,
            subtitleEn = cleanSubtitle,
            subtitleHi = cleanSubtitle,
            baseType = baseType,
            defaultBrandHint = cleanTitle,
            sections = sections,
            customHtmlTemplate = html,
            addedByAi = false,
            timestamp = currentTimestamp()
        )
        viewModelScope.launch { repository.addAdminCustomTool(newTool) }
    }

    fun addAdminAnnouncementManually(title: String, message: String, badge: String) {
        if (!isAdminUnlockedFlow.value) return
        val cleanTitle = title.trim()
        val cleanMessage = message.trim()
        if (cleanTitle.isBlank() || cleanMessage.isBlank()) return
        val item = AdminAnnouncement(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            message = cleanMessage,
            badge = badge.trim().ifBlank { "ADMIN" },
            addedByAi = false,
            timestamp = currentTimestamp()
        )
        viewModelScope.launch { repository.addAdminAnnouncement(item) }
    }

    fun deleteAdminCustomTool(id: String) {
        if (!isAdminUnlockedFlow.value) return
        viewModelScope.launch { repository.deleteAdminCustomTool(id) }
    }

    fun deleteAdminAnnouncement(id: String) {
        if (!isAdminUnlockedFlow.value) return
        viewModelScope.launch { repository.deleteAdminAnnouncement(id) }
    }

    fun openAdminCustomTool(tool: AdminCustomTool) {
        selectedToolFlow.value = tool.baseType
        val isHi = uiState.value.language == AppLanguage.HI
        val title = if (isHi) tool.titleHi else tool.titleEn
        val subtitle = if (isHi) tool.subtitleHi else tool.subtitleEn
        val wireframe = tool.sections.mapIndexed { idx, sec ->
            "${idx + 1}. $sec" to "$title — $subtitle"
        }
        val payload = buildApiPayload(title, tool.defaultBrandHint, subtitle, "Get Started", "#155EEF", isHi)
        lastGeneratedSiteFlow.value = GeneratedWebsiteResult(
            toolType = tool.baseType,
            brandName = tool.defaultBrandHint.ifBlank { title },
            niche = subtitle,
            accentHex = "#155EEF",
            wireframeSections = wireframe,
            htmlCode = tool.customHtmlTemplate,
            apiPromptPayload = payload,
            createdAtLabel = tool.timestamp
        )
    }

    fun updateWhatsAppNumber(number: String) {
        viewModelScope.launch { repository.setWhatsAppNumber(number) }
    }

    fun updateWebhookUrl(url: String) {
        viewModelScope.launch { repository.setWebhookUrl(url) }
    }

    fun selectWebsiteTool(toolType: WebsiteToolType) {
        selectedToolFlow.value = toolType
        lastGeneratedSiteFlow.value = null
    }

    fun openSavedWebsite(blueprint: SavedBlueprint) {
        val matched = AgencyCatalog.websiteTools.firstOrNull {
            it.titleEn == blueprint.toolName || it.titleHi == blueprint.toolName
        } ?: AgencyCatalog.websiteTools.first()
        selectedToolFlow.value = matched.type
        val isHi = uiState.value.language == AppLanguage.HI
        val sections = (if (isHi) matched.defaultSectionsHi else matched.defaultSectionsEn)
            .map { it to blueprint.topic }
        lastGeneratedSiteFlow.value = GeneratedWebsiteResult(
            toolType = matched.type,
            brandName = blueprint.topic,
            niche = matched.titleEn,
            accentHex = "#155EEF",
            wireframeSections = sections,
            htmlCode = blueprint.structuredDraft,
            apiPromptPayload = blueprint.apiPromptPayload,
            createdAtLabel = blueprint.timestamp
        )
    }

    fun importUploadedWebsite(fileName: String, rawContent: String): String {
        val isHi = uiState.value.language == AppLanguage.HI
        val titleMatch = Regex("<title>(.*?)</title>", RegexOption.IGNORE_CASE).find(rawContent)
        val cleanName = titleMatch?.groupValues?.getOrNull(1)?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: fileName.substringBeforeLast('.').replace('_', ' ').replace('-', ' ')
                .ifBlank { "Uploaded Website" }

        val activeTool = selectedToolFlow.value
        val timestamp = currentTimestamp()
        val lineCount = rawContent.lines().size
        val sections = listOf(
            (if (isHi) "1. अपलोड की गई फ़ाइल" else "1. Uploaded Website File") to fileName,
            (if (isHi) "2. पहचान किया गया ब्रांड" else "2. Detected Title") to cleanName,
            (if (isHi) "3. कोड साइज" else "3. Code Metrics") to "$lineCount lines • ${rawContent.length} chars",
            (if (isHi) "4. अगला कदम" else "4. Ready Actions") to (if (isHi) "डाउनलोड, शेयर या होस्टिंग पर अपलोड करें" else "Download, Share, or Deploy to Hosting API")
        )
        val payload = buildApiPayload("Uploaded HTML Import", cleanName, fileName, "Deploy", "#155EEF", isHi)

        lastGeneratedSiteFlow.value = GeneratedWebsiteResult(
            toolType = activeTool,
            brandName = cleanName,
            niche = "Uploaded HTML Package ($fileName)",
            accentHex = "#155EEF",
            wireframeSections = sections,
            htmlCode = rawContent,
            apiPromptPayload = payload,
            createdAtLabel = timestamp
        )

        val blueprint = SavedBlueprint(
            id = UUID.randomUUID().toString(),
            toolName = if (isHi) "अपलोड की गई वेबसाइट" else "Uploaded Website",
            topic = cleanName,
            structuredDraft = rawContent,
            apiPromptPayload = payload,
            timestamp = timestamp
        )
        viewModelScope.launch { repository.addBlueprint(blueprint) }
        return cleanName
    }

    fun deployWebsiteToConfiguredEndpoint(
        siteName: String,
        htmlCode: String,
        endpointUrl: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val cleanUrl = endpointUrl.trim()
        val isHi = uiState.value.language == AppLanguage.HI
        if (!cleanUrl.startsWith("https://")) {
            onComplete(
                false,
                if (isHi) "अपलोड करने के लिए कृपया मान्य https:// होस्टिंग API URL दर्ज करें।"
                else "Please enter a valid https:// hosting or deploy API URL first."
            )
            return
        }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val conn = (URL(cleanUrl).openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        connectTimeout = 8000
                        readTimeout = 8000
                        doOutput = true
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    }
                    val payload = JSONObject().apply {
                        put("source", "WEBSHAKTI AI")
                        put("siteName", siteName)
                        put("html", htmlCode)
                    }.toString()
                    OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                        writer.write(payload)
                        writer.flush()
                    }
                    val code = conn.responseCode
                    conn.disconnect()
                    code
                }
            }
            result.onSuccess { statusCode ->
                if (statusCode in 200..299) {
                    onComplete(
                        true,
                        if (isHi) "वेबसाइट अपलोड सफल (HTTP $statusCode)" else "Website uploaded to endpoint (HTTP $statusCode)"
                    )
                } else {
                    onComplete(
                        false,
                        if (isHi) "सर्वर ने HTTP $statusCode लौटाया" else "Server returned HTTP $statusCode"
                    )
                }
            }.onFailure { err ->
                onComplete(
                    false,
                    if (isHi) "अपलोड त्रुटि: ${err.localizedMessage ?: "कनेक्शन विफल"}"
                    else "Upload failed: ${err.localizedMessage ?: "Unable to reach endpoint"}"
                )
            }
        }
    }

    fun prepareCustomWebsiteRequest(
        websiteType: WebsiteToolType? = null,
        requirementsSummary: String? = null
    ) {
        prefilledTypeFlow.value = websiteType
        prefilledRequirementsFlow.value = requirementsSummary
        contactTabFlow.value = 1
    }

    fun setContactTab(tabIndex: Int) {
        contactTabFlow.value = tabIndex
    }

    fun submitContactInquiry(
        fullName: String,
        email: String,
        subject: String,
        message: String
    ) {
        val entry = SavedInquiry(
            id = UUID.randomUUID().toString(),
            type = "contact",
            fullName = fullName.trim(),
            email = email.trim(),
            companyOrSubject = subject.trim(),
            serviceOrTopic = "Website Consultation",
            planTier = "General",
            timeline = "-",
            message = message.trim(),
            timestamp = currentTimestamp()
        )
        viewModelScope.launch { repository.addInquiry(entry) }
    }

    fun submitWebsiteRequest(
        fullName: String,
        email: String,
        company: String,
        websiteTypeName: String,
        timeline: String,
        requirements: String
    ) {
        val entry = SavedInquiry(
            id = UUID.randomUUID().toString(),
            type = "website_build",
            fullName = fullName.trim(),
            email = email.trim(),
            companyOrSubject = company.trim().ifBlank { "Independent" },
            serviceOrTopic = websiteTypeName,
            planTier = "Custom Website",
            timeline = timeline,
            message = requirements.trim(),
            timestamp = currentTimestamp()
        )
        viewModelScope.launch { repository.addInquiry(entry) }
    }

    fun deleteInquiry(id: String) {
        viewModelScope.launch { repository.deleteInquiry(id) }
    }

    fun generateWebsitePackage(
        toolType: WebsiteToolType,
        brandName: String,
        niche: String,
        ctaText: String,
        accentHex: String
    ) {
        val cleanBrand = brandName.trim()
        val cleanNiche = niche.trim().ifBlank { "Modern Digital Business" }
        val cleanCta = ctaText.trim().ifBlank {
            if (uiState.value.language == AppLanguage.HI) "शुरू करें" else "Get Started"
        }
        val isHi = uiState.value.language == AppLanguage.HI
        val meta = AgencyCatalog.websiteTools.first { it.type == toolType }

        val wireframe = buildWireframeSections(toolType, cleanBrand, cleanNiche, cleanCta, isHi)
        val html = buildHtmlCode(toolType, cleanBrand, cleanNiche, cleanCta, accentHex, isHi)
        val payload = buildApiPayload(meta.titleEn, cleanBrand, cleanNiche, cleanCta, accentHex, isHi)

        lastGeneratedSiteFlow.value = GeneratedWebsiteResult(
            toolType = toolType,
            brandName = cleanBrand,
            niche = cleanNiche,
            accentHex = accentHex,
            wireframeSections = wireframe,
            htmlCode = html,
            apiPromptPayload = payload,
            createdAtLabel = currentTimestamp()
        )
    }

    fun saveCurrentWebsiteToWorkspace() {
        val site = lastGeneratedSiteFlow.value ?: return
        val meta = AgencyCatalog.websiteTools.first { it.type == site.toolType }
        val toolTitle = if (uiState.value.language == AppLanguage.HI) meta.titleHi else meta.titleEn
        val blueprint = SavedBlueprint(
            id = UUID.randomUUID().toString(),
            toolName = toolTitle,
            topic = site.brandName,
            structuredDraft = site.htmlCode,
            apiPromptPayload = site.apiPromptPayload,
            timestamp = site.createdAtLabel
        )
        viewModelScope.launch { repository.addBlueprint(blueprint) }
    }

    fun deleteBlueprint(id: String) {
        viewModelScope.launch { repository.deleteBlueprint(id) }
    }

    private fun currentTimestamp(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        return formatter.format(Date())
    }

    private fun buildWireframeSections(
        toolType: WebsiteToolType,
        brand: String,
        niche: String,
        cta: String,
        isHi: Boolean
    ): List<Pair<String, String>> = when (toolType) {
        WebsiteToolType.BUSINESS_WEBSITE -> listOf(
            (if (isHi) "1. नेविगेशन बार" else "1. Top Navigation") to "$brand  |  Home • Services • Team • Contact",
            (if (isHi) "2. हीरो सेक्शन" else "2. Hero Banner") to "$brand — $niche  [$cta]",
            (if (isHi) "3. मुख्य समाधान ग्रिड" else "3. Capabilities Grid") to "Architecture • Consulting • Support",
            (if (isHi) "4. वेबसाइट विशेषज्ञ टीम" else "4. Web Creators Team") to "Dedicated Architects & Engineers",
            (if (isHi) "5. संपर्क और फ़ुटर" else "5. Inquiry & Footer") to "Contact Form + Direct WhatsApp CTA"
        )
        WebsiteToolType.ECOMMERCE_WEBSITE -> listOf(
            (if (isHi) "1. स्टोर हेडर" else "1. Store Header") to "$brand  |  Shop • Categories • Cart (0)",
            (if (isHi) "2. कलेक्शन हीरो" else "2. Featured Collection") to "Curated $niche  [$cta]",
            (if (isHi) "3. प्रोडक्ट कैटलॉग ग्रिड" else "3. Product Grid") to "Product Cards • Filter Bar • Quick View",
            (if (isHi) "4. ऑर्डर और सपोर्ट" else "4. Trust & Checkout Ready") to "Shipping Policy • Returns • WhatsApp Support"
        )
        WebsiteToolType.PORTFOLIO_WEBSITE -> listOf(
            (if (isHi) "1. परिचय हेडर" else "1. Personal Intro Hero") to "$brand — $niche  [$cta]",
            (if (isHi) "2. प्रोजेक्ट शोकेस" else "2. Featured Case Studies") to "3-Column Visual Project Cards",
            (if (isHi) "3. कौशल और टूलस्टैक" else "3. Tech & Design Stack") to "UI Systems • Frontend • SEO",
            (if (isHi) "4. हायर / संपर्क ब्लॉक" else "4. Direct Booking Footer") to "Email + WhatsApp Availability"
        )
        WebsiteToolType.SAAS_LANDING_PAGE -> listOf(
            (if (isHi) "1. प्रोडक्ट हीरो" else "1. SaaS Product Hero") to "$brand: $niche  [$cta]",
            (if (isHi) "2. फ़ीचर मैट्रिक्स" else "2. Core Feature Blocks") to "Workflow Automation • Analytics • Security",
            (if (isHi) "3. प्लान और प्राइसिंग" else "3. Transparent Plans") to "Starter • Growth • Enterprise",
            (if (isHi) "4. FAQ और डेमो CTA" else "4. FAQ & Demo Form") to "Technical Q&A + Trial Request"
        )
        WebsiteToolType.BLOG_NEWS_WEBSITE -> listOf(
            (if (isHi) "1. संपादकीय हेडर" else "1. Masthead & Topics") to "$brand  |  Latest • Guides • Insights",
            (if (isHi) "2. फ़ीचर्ड लेख" else "2. Lead Editorial Hero") to "Deep Dive into $niche  [$cta]",
            (if (isHi) "3. लेख ग्रिड" else "3. Article Cards Grid") to "Reading Time • Category Tags • Search",
            (if (isHi) "4. न्यूज़लेटर फ़ुटर" else "4. Reader Subscription") to "Email Digest Form + Archive Links"
        )
        WebsiteToolType.RESTAURANT_BOOKING_SITE -> listOf(
            (if (isHi) "1. ब्रांड हीरो" else "1. Dining Showcase Hero") to "$brand — $niche  [$cta]",
            (if (isHi) "2. सिग्नेचर मेनू" else "2. Signature Menu Grid") to "Chef Specials • Beverages • Dietary Tags",
            (if (isHi) "3. टेबल आरक्षण" else "3. Reservation & Hours") to "Date/Guest Selector + WhatsApp Booking",
            (if (isHi) "4. लोकेशन फ़ुटर" else "4. Map & Opening Hours") to "Address • Daily Schedule • Contact"
        )
        WebsiteToolType.SEO_META_SCHEMA_TOOL -> listOf(
            (if (isHi) "1. टाइटल और मेटा विवरण" else "1. Search Title & Description") to "$brand | $niche",
            (if (isHi) "2. Open Graph कार्ड" else "2. Social Open Graph Tags") to "og:title • og:description • og:type",
            (if (isHi) "3. JSON-LD स्कीमा" else "3. JSON-LD Structured Data") to "Schema.org Organization / WebSite"
        )
        WebsiteToolType.LEGAL_PAGES_GENERATOR -> listOf(
            (if (isHi) "1. गोपनीयता नीति खंड" else "1. Privacy Policy Section") to "Data handling & cookies for $brand",
            (if (isHi) "2. नियम और शर्तें खंड" else "2. Terms & Conditions Section") to "Usage terms for $niche",
            (if (isHi) "3. संपर्क और अनुपालन" else "3. Compliance Contact Block") to "User rights & support email"
        )
    }

    private fun buildHtmlCode(
        toolType: WebsiteToolType,
        brand: String,
        niche: String,
        cta: String,
        accentHex: String,
        isHi: Boolean
    ): String {
        val langAttr = if (isHi) "hi" else "en"
        if (toolType == WebsiteToolType.SEO_META_SCHEMA_TOOL) {
            return """
                <!-- SEO & Structured Data Bundle for $brand -->
                <title>$brand — $niche</title>
                <meta name="description" content="$brand delivers $niche with modern responsive architecture." />
                <meta name="robots" content="index, follow" />
                <meta property="og:type" content="website" />
                <meta property="og:title" content="$brand — $niche" />
                <meta property="og:description" content="Explore $brand: $niche." />
                <meta name="twitter:card" content="summary_large_image" />
                <script type="application/ld+json">
                {
                  "@context": "https://schema.org",
                  "@type": "WebSite",
                  "name": "$brand",
                  "description": "$niche"
                }
                </script>
            """.trimIndent()
        }

        if (toolType == WebsiteToolType.LEGAL_PAGES_GENERATOR) {
            return """
                <!DOCTYPE html>
                <html lang="$langAttr">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>$brand — Privacy Policy & Terms</title>
                  <style>
                    body { font-family: system-ui, sans-serif; background:#FFFFFF; color:#090D16; max-width:860px; margin:auto; padding:40px 20px; line-height:1.7; }
                    h1, h2 { color:$accentHex; }
                    section { background:#F7F9FC; padding:20px; border-radius:14px; margin-bottom:20px; border:1px solid #CBD5E1; }
                  </style>
                </head>
                <body>
                  <h1>$brand — Legal Documentation</h1>
                  <section>
                    <h2>Privacy Policy</h2>
                    <p>$brand ($niche) collects only information voluntarily submitted through contact or inquiry forms.</p>
                  </section>
                  <section>
                    <h2>Terms & Conditions</h2>
                    <p>By accessing $brand, you agree to use published content and services in compliance with applicable laws.</p>
                  </section>
                </body>
                </html>
            """.trimIndent()
        }

        val sections = buildWireframeSections(toolType, brand, niche, cta, isHi)
        val cardsHtml = sections.drop(2).joinToString("\n      ") { (title, desc) ->
            "<div class=\"card\"><h3>$title</h3><p>$desc</p></div>"
        }

        return """
            <!DOCTYPE html>
            <html lang="$langAttr">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>$brand — $niche</title>
              <style>
                * { box-sizing: border-box; margin: 0; padding: 0; }
                body { font-family: system-ui, sans-serif; background: #FFFFFF; color: #090D16; line-height: 1.6; }
                header { display: flex; justify-content: space-between; align-items: center; padding: 20px 8%; background: #F7F9FC; border-bottom: 1px solid #CBD5E1; }
                .brand { font-weight: 700; font-size: 1.25rem; color: $accentHex; }
                .hero { padding: 72px 8%; background: radial-gradient(circle at top right, ${accentHex}18, #FFFFFF); }
                .hero h1 { font-size: clamp(2rem, 4vw, 3.2rem); margin-bottom: 12px; color: #090D16; }
                .hero p { color: #334155; max-width: 640px; margin-bottom: 24px; }
                .btn { display: inline-block; background: $accentHex; color: #FFFFFF; padding: 12px 24px; border-radius: 10px; text-decoration: none; font-weight: 600; }
                .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 18px; padding: 48px 8%; }
                .card { background: #F7F9FC; border: 1px solid #CBD5E1; border-radius: 16px; padding: 22px; }
                .card h3 { color: $accentHex; margin-bottom: 8px; }
                .card p { color: #334155; }
                footer { text-align: center; padding: 28px 8%; color: #475569; border-top: 1px solid #CBD5E1; background: #F7F9FC; }
              </style>
            </head>
            <body>
              <header>
                <div class="brand">$brand</div>
                <nav><a class="btn" href="#contact">$cta</a></nav>
              </header>
              <section class="hero">
                <h1>$brand</h1>
                <p>$niche</p>
                <a class="btn" href="#contact">$cta</a>
              </section>
              <section class="grid">
                  $cardsHtml
              </section>
              <footer id="contact">© $brand — Generated by WEBSHAKTI AI Website Studio</footer>
            </body>
            </html>
        """.trimIndent()
    }

    private fun buildApiPayload(
        toolTitle: String,
        brand: String,
        niche: String,
        cta: String,
        accentHex: String,
        isHi: Boolean
    ): String {
        val langLabel = if (isHi) "Hindi (hi)" else "English (en)"
        return """
            {
              "generator_tool": "$toolTitle",
              "status": "READY_FOR_LLM_WEBSITE_API",
              "parameters": {
                "brand_name": "${brand.replace("\"", "\\\"")}",
                "niche": "${niche.replace("\"", "\\\"")}",
                "primary_cta": "${cta.replace("\"", "\\\"")}",
                "accent_color": "$accentHex",
                "output_language": "$langLabel"
              }
            }
        """.trimIndent()
    }
}
