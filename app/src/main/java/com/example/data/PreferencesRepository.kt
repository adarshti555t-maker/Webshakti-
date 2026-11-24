package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AdminAnnouncement
import com.example.model.AdminCustomTool
import com.example.model.AppLanguage
import com.example.model.SavedBlueprint
import com.example.model.SavedInquiry
import com.example.model.WebsiteToolType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.webShaktiDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "webshakti_prefs"
)

class PreferencesRepository(private val context: Context) {

    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
        private val KEY_WHATSAPP_NUMBER = stringPreferencesKey("whatsapp_number")
        private val KEY_WEBHOOK_URL = stringPreferencesKey("webhook_url")
        private val KEY_INQUIRIES_JSON = stringPreferencesKey("saved_inquiries_json")
        private val KEY_BLUEPRINTS_JSON = stringPreferencesKey("saved_blueprints_json")
        private val KEY_ADMIN_TOOLS_JSON = stringPreferencesKey("admin_custom_tools_json")
        private val KEY_ADMIN_ANNOUNCEMENTS_JSON = stringPreferencesKey("admin_announcements_json")
        const val DEFAULT_WHATSAPP_NUMBER = "+919569221396"
        const val ADMIN_PASSCODE = "1234AT"
    }

    val languageFlow: Flow<AppLanguage> = context.webShaktiDataStore.data.map { prefs ->
        when (prefs[KEY_LANGUAGE]) {
            AppLanguage.HI.code -> AppLanguage.HI
            else -> AppLanguage.EN
        }
    }

    val whatsAppNumberFlow: Flow<String> = context.webShaktiDataStore.data.map { prefs ->
        prefs[KEY_WHATSAPP_NUMBER]?.takeIf { it.isNotBlank() } ?: DEFAULT_WHATSAPP_NUMBER
    }

    val webhookUrlFlow: Flow<String> = context.webShaktiDataStore.data.map { prefs ->
        prefs[KEY_WEBHOOK_URL] ?: ""
    }

    val savedInquiriesFlow: Flow<List<SavedInquiry>> = context.webShaktiDataStore.data.map { prefs ->
        parseInquiries(prefs[KEY_INQUIRIES_JSON])
    }

    val savedBlueprintsFlow: Flow<List<SavedBlueprint>> = context.webShaktiDataStore.data.map { prefs ->
        parseBlueprints(prefs[KEY_BLUEPRINTS_JSON])
    }

    val adminCustomToolsFlow: Flow<List<AdminCustomTool>> = context.webShaktiDataStore.data.map { prefs ->
        parseAdminTools(prefs[KEY_ADMIN_TOOLS_JSON])
    }

    val adminAnnouncementsFlow: Flow<List<AdminAnnouncement>> = context.webShaktiDataStore.data.map { prefs ->
        parseAdminAnnouncements(prefs[KEY_ADMIN_ANNOUNCEMENTS_JSON])
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.webShaktiDataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.code
        }
    }

    suspend fun setWhatsAppNumber(number: String) {
        val cleaned = number.trim().ifBlank { DEFAULT_WHATSAPP_NUMBER }
        context.webShaktiDataStore.edit { prefs ->
            prefs[KEY_WHATSAPP_NUMBER] = cleaned
        }
    }

    suspend fun setWebhookUrl(url: String) {
        context.webShaktiDataStore.edit { prefs ->
            prefs[KEY_WEBHOOK_URL] = url.trim()
        }
    }

    suspend fun addInquiry(inquiry: SavedInquiry) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseInquiries(prefs[KEY_INQUIRIES_JSON]).toMutableList()
            current.add(0, inquiry)
            prefs[KEY_INQUIRIES_JSON] = encodeInquiries(current.take(25))
        }
    }

    suspend fun deleteInquiry(id: String) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseInquiries(prefs[KEY_INQUIRIES_JSON]).filterNot { it.id == id }
            prefs[KEY_INQUIRIES_JSON] = encodeInquiries(current)
        }
    }

    suspend fun addBlueprint(blueprint: SavedBlueprint) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseBlueprints(prefs[KEY_BLUEPRINTS_JSON]).toMutableList()
            current.add(0, blueprint)
            prefs[KEY_BLUEPRINTS_JSON] = encodeBlueprints(current.take(25))
        }
    }

    suspend fun deleteBlueprint(id: String) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseBlueprints(prefs[KEY_BLUEPRINTS_JSON]).filterNot { it.id == id }
            prefs[KEY_BLUEPRINTS_JSON] = encodeBlueprints(current)
        }
    }

    suspend fun addAdminCustomTool(tool: AdminCustomTool) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseAdminTools(prefs[KEY_ADMIN_TOOLS_JSON]).toMutableList()
            current.add(0, tool)
            prefs[KEY_ADMIN_TOOLS_JSON] = encodeAdminTools(current.take(25))
        }
    }

    suspend fun deleteAdminCustomTool(id: String) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseAdminTools(prefs[KEY_ADMIN_TOOLS_JSON]).filterNot { it.id == id }
            prefs[KEY_ADMIN_TOOLS_JSON] = encodeAdminTools(current)
        }
    }

    suspend fun addAdminAnnouncement(item: AdminAnnouncement) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseAdminAnnouncements(prefs[KEY_ADMIN_ANNOUNCEMENTS_JSON]).toMutableList()
            current.add(0, item)
            prefs[KEY_ADMIN_ANNOUNCEMENTS_JSON] = encodeAdminAnnouncements(current.take(20))
        }
    }

    suspend fun deleteAdminAnnouncement(id: String) {
        context.webShaktiDataStore.edit { prefs ->
            val current = parseAdminAnnouncements(prefs[KEY_ADMIN_ANNOUNCEMENTS_JSON]).filterNot { it.id == id }
            prefs[KEY_ADMIN_ANNOUNCEMENTS_JSON] = encodeAdminAnnouncements(current)
        }
    }

    private fun parseInquiries(raw: String?): List<SavedInquiry> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        SavedInquiry(
                            id = obj.optString("id"),
                            type = obj.optString("type"),
                            fullName = obj.optString("fullName"),
                            email = obj.optString("email"),
                            companyOrSubject = obj.optString("companyOrSubject"),
                            serviceOrTopic = obj.optString("serviceOrTopic"),
                            planTier = obj.optString("planTier"),
                            timeline = obj.optString("timeline"),
                            message = obj.optString("message"),
                            timestamp = obj.optString("timestamp")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeInquiries(list: List<SavedInquiry>): String {
        val array = JSONArray()
        list.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("type", item.type)
                    put("fullName", item.fullName)
                    put("email", item.email)
                    put("companyOrSubject", item.companyOrSubject)
                    put("serviceOrTopic", item.serviceOrTopic)
                    put("planTier", item.planTier)
                    put("timeline", item.timeline)
                    put("message", item.message)
                    put("timestamp", item.timestamp)
                }
            )
        }
        return array.toString()
    }

    private fun parseBlueprints(raw: String?): List<SavedBlueprint> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        SavedBlueprint(
                            id = obj.optString("id"),
                            toolName = obj.optString("toolName"),
                            topic = obj.optString("topic"),
                            structuredDraft = obj.optString("structuredDraft"),
                            apiPromptPayload = obj.optString("apiPromptPayload"),
                            timestamp = obj.optString("timestamp")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeBlueprints(list: List<SavedBlueprint>): String {
        val array = JSONArray()
        list.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("toolName", item.toolName)
                    put("topic", item.topic)
                    put("structuredDraft", item.structuredDraft)
                    put("apiPromptPayload", item.apiPromptPayload)
                    put("timestamp", item.timestamp)
                }
            )
        }
        return array.toString()
    }

    private fun parseAdminTools(raw: String?): List<AdminCustomTool> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val secArr = obj.optJSONArray("sections")
                    val sections = buildList {
                        if (secArr != null) {
                            for (j in 0 until secArr.length()) {
                                add(secArr.optString(j))
                            }
                        }
                    }
                    val baseTypeName = obj.optString("baseType", WebsiteToolType.BUSINESS_WEBSITE.name)
                    val baseType = WebsiteToolType.entries.firstOrNull { it.name == baseTypeName }
                        ?: WebsiteToolType.BUSINESS_WEBSITE
                    add(
                        AdminCustomTool(
                            id = obj.optString("id"),
                            titleEn = obj.optString("titleEn"),
                            titleHi = obj.optString("titleHi"),
                            subtitleEn = obj.optString("subtitleEn"),
                            subtitleHi = obj.optString("subtitleHi"),
                            baseType = baseType,
                            defaultBrandHint = obj.optString("defaultBrandHint"),
                            sections = sections,
                            customHtmlTemplate = obj.optString("customHtmlTemplate"),
                            addedByAi = obj.optBoolean("addedByAi", false),
                            timestamp = obj.optString("timestamp")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeAdminTools(list: List<AdminCustomTool>): String {
        val array = JSONArray()
        list.forEach { item ->
            val secArr = JSONArray().apply { item.sections.forEach { put(it) } }
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("titleEn", item.titleEn)
                    put("titleHi", item.titleHi)
                    put("subtitleEn", item.subtitleEn)
                    put("subtitleHi", item.subtitleHi)
                    put("baseType", item.baseType.name)
                    put("defaultBrandHint", item.defaultBrandHint)
                    put("sections", secArr)
                    put("customHtmlTemplate", item.customHtmlTemplate)
                    put("addedByAi", item.addedByAi)
                    put("timestamp", item.timestamp)
                }
            )
        }
        return array.toString()
    }

    private fun parseAdminAnnouncements(raw: String?): List<AdminAnnouncement> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        AdminAnnouncement(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            message = obj.optString("message"),
                            badge = obj.optString("badge"),
                            addedByAi = obj.optBoolean("addedByAi", false),
                            timestamp = obj.optString("timestamp")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeAdminAnnouncements(list: List<AdminAnnouncement>): String {
        val array = JSONArray()
        list.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("message", item.message)
                    put("badge", item.badge)
                    put("addedByAi", item.addedByAi)
                    put("timestamp", item.timestamp)
                }
            )
        }
        return array.toString()
    }
}
