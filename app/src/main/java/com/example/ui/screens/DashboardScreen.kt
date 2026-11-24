package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.data.AgencyCatalog
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.SavedBlueprint
import com.example.model.SavedInquiry
import com.example.ui.components.AgencyFooter
import com.example.ui.components.ApiConnectionBadge
import com.example.ui.components.ApiConnectionBanner
import com.example.ui.components.SectionHeader
import com.example.ui.components.launchWhatsApp
import com.example.ui.components.neuralGridBackground
import com.example.ui.components.shareTextContent
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun SavedSitesWorkspaceScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    webhookUrl: String,
    savedInquiries: List<SavedInquiry>,
    savedBlueprints: List<SavedBlueprint>,
    onSaveWebhookUrl: (String) -> Unit,
    onReopenWebsite: (SavedBlueprint) -> Unit,
    onImportHtmlWebsite: (String, String) -> String,
    onDeploySiteToEndpoint: (String, String, String, (Boolean, String) -> Unit) -> Unit,
    onDeleteInquiry: (String) -> Unit,
    onDeleteBlueprint: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var webhookDraft by remember(webhookUrl) { mutableStateOf(webhookUrl) }
    var pendingDownloadSite by remember { mutableStateOf<SavedBlueprint?>(null) }
    var deployTargetSite by remember { mutableStateOf<SavedBlueprint?>(null) }

    val downloadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri ->
        val html = pendingDownloadSite?.structuredDraft
        if (uri != null && html != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(html.toByteArray(Charsets.UTF_8))
                }
            }.onSuccess {
                onShowMessage(
                    if (isHi) "वेबसाइट HTML फ़ाइल डाउनलोड हो गई!" else "Website HTML file downloaded!"
                )
            }.onFailure {
                onShowMessage(if (isHi) "डाउनलोड विफल रहा।" else "Download failed.")
            }
        }
        pendingDownloadSite = null
    }

    val uploadFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                val fileName = readDisplayFileName(context, uri)
                val text = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
                    it.readText()
                } ?: ""
                fileName to text
            }.onSuccess { (fileName, content) ->
                if (content.isBlank()) {
                    onShowMessage(if (isHi) "फ़ाइल खाली है।" else "Selected file is empty.")
                } else {
                    val imported = onImportHtmlWebsite(fileName, content)
                    onShowMessage(if (isHi) "अपलोड सफल: $imported" else "Uploaded & saved: $imported")
                }
            }.onFailure {
                onShowMessage(if (isHi) "फ़ाइल अपलोड नहीं हो सकी।" else "Unable to read file.")
            }
        }
    }

    val filteredInquiries = remember(savedInquiries, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) savedInquiries
        else savedInquiries.filter {
            it.fullName.lowercase().contains(q) ||
                it.serviceOrTopic.lowercase().contains(q) ||
                it.message.lowercase().contains(q)
        }
    }

    val filteredWebsites = remember(savedBlueprints, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) savedBlueprints
        else savedBlueprints.filter {
            it.toolName.lowercase().contains(q) ||
                it.topic.lowercase().contains(q) ||
                it.structuredDraft.lowercase().contains(q)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neuralGridBackground()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 1040.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                eyebrow = if (isHi) "सहेजी गई वेबसाइट्स और अनुरोध" else "SAVED WEBSITES WORKSPACE",
                title = if (isHi) "जनरेट की गई साइट्स, डाउनलोड व अपलोड हब" else "Saved Websites, Download & Upload Hub",
                subtitle = if (isHi) {
                    "अपनी वेबसाइट .html फ़ाइलें डाउनलोड करें, डिवाइस से HTML अपलोड करें, या लाइव होस्टिंग API पर डिप्लॉय करें।"
                } else {
                    "Download generated .html websites, upload existing HTML files from device, or deploy to your configured hosting API."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ApiConnectionBanner(
                title = if (isHi) "ऑफ़लाइन डाउनलोड और फ़ाइल अपलोड सक्रिय • क्लाउड होस्टिंग API" else "Offline Download & File Upload Active • Cloud Hosting API",
                description = if (isHi) {
                    "नीचे दी गई संख्याएँ आपके डिवाइस पर सहेजे गए वास्तविक रिकॉर्ड दर्शाती हैं। लाइव क्लाउड पब्लिशिंग के लिए नीचे अपना https:// API URL जोड़ें।"
                } else {
                    "Counts below reflect real records stored on this device. Add your https:// Hosting API URL below for remote server upload."
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth > 680.dp) 3 else 1
                val cards = listOf(
                    Triple(
                        if (isHi) "सहेजी गई वेबसाइट्स" else "Saved Websites",
                        savedBlueprints.size.toString(),
                        null as String?
                    ),
                    Triple(
                        if (isHi) "कस्टम बिल्ड अनुरोध" else "Build Requests",
                        savedInquiries.size.toString(),
                        null as String?
                    ),
                    Triple(
                        if (isHi) "WhatsApp नंबर" else "WhatsApp Line",
                        whatsAppNumber,
                        if (isHi) "बदलें" else "Edit"
                    )
                )
                if (cols == 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        cards.forEach { (label, value, action) ->
                            WorkspaceStatCard(
                                label = label,
                                value = value,
                                actionLabel = action,
                                onAction = if (action != null) onConfigureWhatsApp else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        cards.forEach { (label, value, action) ->
                            WorkspaceStatCard(
                                label = label,
                                value = value,
                                actionLabel = action,
                                onAction = if (action != null) onConfigureWhatsApp else null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = ElectricBlue) },
                placeholder = {
                    Text(
                        if (isHi) "सहेजी गई वेबसाइट या अनुरोध फ़िल्टर करें..." else "Filter saved websites & requests..."
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SAVED GENERATED WEBSITES HEADER + UPLOAD HTML + NEW WEBSITE BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHi) "सहेजी गई वेबसाइट्स (${filteredWebsites.size})" else "Saved Websites (${filteredWebsites.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { uploadFileLauncher.launch(arrayOf("text/html", "text/plain", "*/*")) },
                        border = BorderStroke(1.dp, DeepSapphire),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Filled.UploadFile, contentDescription = null, tint = DeepSapphire, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(if (isHi) "अपलोड" else "Upload", color = DeepSapphire)
                    }
                    OutlinedButton(
                        onClick = { onNavigate(AppPage.GENERATOR) },
                        border = BorderStroke(1.dp, ElectricBlue),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(if (isHi) "नई साइट" else "New Site", color = ElectricBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (filteredWebsites.isEmpty()) {
                EmptyWorkspaceCard(
                    icon = Icons.Filled.FolderOpen,
                    title = if (isHi) "कोई सहेजी गई वेबसाइट नहीं" else "No Saved Websites Yet",
                    subtitle = if (isHi) {
                        "वेब जनरेटर में नई वेबसाइट बनाएँ या डिवाइस से मौजूदा .html फ़ाइल अपलोड करें।"
                    } else {
                        "Generate a website in the Web Generator Studio or upload an existing .html file from your device."
                    },
                    buttonText = if (isHi) "वेब जनरेटर खोलें" else "Open Web Generator",
                    onClick = { onNavigate(AppPage.GENERATOR) }
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredWebsites.forEach { bp ->
                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, NavyBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = bp.toolName, style = MaterialTheme.typography.titleSmall, color = ElectricBlue)
                                        Text(text = "${bp.topic} • ${bp.timestamp}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    }
                                    IconButton(
                                        onClick = { onDeleteBlueprint(bp.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete saved website", tint = TextSecondary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bp.structuredDraft,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 4
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Download + Upload/Deploy row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            pendingDownloadSite = bp
                                            downloadLauncher.launch(buildSafeHtmlFileName(bp.topic))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Download, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(if (isHi) "डाउनलोड" else "Download", color = OnAccentWhite)
                                    }
                                    Button(
                                        onClick = { deployTargetSite = bp },
                                        colors = ButtonDefaults.buttonColors(containerColor = DeepSapphire),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(if (isHi) "अपलोड / डिप्लॉय" else "Upload", color = OnAccentWhite)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onReopenWebsite(bp) },
                                        border = BorderStroke(1.dp, CyanPulse),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(if (isHi) "खोलें" else "Re-open", color = CyanPulse)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(bp.structuredDraft))
                                            onShowMessage(if (isHi) "HTML कोड कॉपी किया गया" else "HTML code copied")
                                        },
                                        border = BorderStroke(1.dp, NavyBorder),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(if (isHi) "कॉपी" else "Copy", color = TextPrimary)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            shareTextContent(context, "WEBSHAKTI AI — ${bp.topic}", bp.structuredDraft)
                                        },
                                        border = BorderStroke(1.dp, NavyBorder),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "Share website code", tint = TextPrimary, modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SAVED CUSTOM WEBSITE REQUESTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHi) "कस्टम वेबसाइट अनुरोध (${filteredInquiries.size})" else "Custom Website Requests (${filteredInquiries.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                OutlinedButton(
                    onClick = { onNavigate(AppPage.CONTACT) },
                    border = BorderStroke(1.dp, ElectricBlue),
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHi) "नया अनुरोध" else "New Request", color = ElectricBlue)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (filteredInquiries.isEmpty()) {
                EmptyWorkspaceCard(
                    icon = Icons.Filled.Inbox,
                    title = if (isHi) "कोई कस्टम अनुरोध नहीं" else "No Custom Website Requests Yet",
                    subtitle = if (isHi) {
                        "हमारी वेबसाइट टीम के लिए नया कस्टम वेबसाइट अनुरोध दर्ज करें।"
                    } else {
                        "Submit a Custom Website Request to share your project specs with our web builders."
                    },
                    buttonText = if (isHi) "अनुरोध फ़ॉर्म खोलें" else "Request Custom Build",
                    onClick = { onNavigate(AppPage.CONTACT) }
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredInquiries.forEach { inquiry ->
                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, NavyBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${inquiry.serviceOrTopic} • ${inquiry.timeline}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = ElectricBlue
                                        )
                                        Text(
                                            text = "${inquiry.fullName} (${inquiry.email}) • ${inquiry.timestamp}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteInquiry(inquiry.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete request", tint = TextSecondary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = inquiry.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            launchWhatsApp(
                                                context,
                                                whatsAppNumber,
                                                "WEBSHAKTI AI Custom Website (${inquiry.serviceOrTopic})\nFrom: ${inquiry.fullName}\nDetails: ${inquiry.message}"
                                            )
                                        },
                                        border = BorderStroke(1.dp, WhatsAppGreen),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp", color = SuccessEmerald)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            shareTextContent(
                                                context,
                                                "WEBSHAKTI AI Website Request — ${inquiry.serviceOrTopic}",
                                                "Client: ${inquiry.fullName} (${inquiry.email})\nType: ${inquiry.serviceOrTopic}\nTimeline: ${inquiry.timeline}\n\n${inquiry.message}"
                                            )
                                        },
                                        border = BorderStroke(1.dp, NavyBorder),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "Share request", tint = CyanPulse, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isHi) "शेयर" else "Share", color = CyanPulse)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 4-STAGE WEBSITE DELIVERY PIPELINE
            Text(
                text = if (isHi) "वेबसाइट निर्माण पाइपलाइन" else "4-Stage Website Delivery Pipeline",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            val stages = if (isHi) {
                listOf(
                    "01. लेआउट और संरचना" to "सही वेबसाइट टूल चुनकर पेज सेक्शन और CTA व्यवस्थित करना।",
                    "02. HTML5 + CSS बिल्ड" to "क्लीन व्हाइट व ब्लैक थीम के साथ रिस्पॉन्सिव कोड जनरेट करना।",
                    "03. डाउनलोड और अपलोड" to "वेबसाइट को .html फ़ाइल में डाउनलोड करें या मौजूदा फ़ाइल अपलोड करें।",
                    "04. डोमेन और API लॉन्च" to "वेब क्रिएटर्स टीम के साथ कस्टम होस्टिंग और क्लाउड डिप्लॉयमेंट।"
                )
            } else {
                listOf(
                    "01. Structure & Wireframe" to "Map sections, navigation, and conversion CTAs for the chosen site type.",
                    "02. HTML5 + CSS Generation" to "Produce responsive grids, hero headers, and clean theme tokens.",
                    "03. Download & Upload Flow" to "Export .html locally or upload existing HTML packages into the studio.",
                    "04. Web Creator Launch" to "Connect custom domain hosting and optional live LLM APIs."
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stages.forEach { (stage, desc) ->
                    Surface(
                        color = NavySurface,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = stage, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // FUTURE HOSTING / UPLOAD API ENDPOINT CONFIG
            Surface(
                color = NavyElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Api, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "होस्टिंग अपलोड / AI API एंडपॉइंट" else "Hosting Upload / AI API Endpoint",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHi) {
                            "जब आप अपना लाइव होस्टिंग या डिप्लॉय सर्वर जोड़ें, तो उसका HTTPS URL यहाँ सहेजें ताकि 'अपलोड' बटन सीधे वहाँ वेबसाइट भेज सके।"
                        } else {
                            "Save your HTTPS hosting/deploy endpoint here so the Upload button can POST your generated HTML directly to your server."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = webhookDraft,
                        onValueChange = { webhookDraft = it },
                        label = { Text(if (isHi) "HTTPS अपलोड API URL" else "HTTPS Upload Endpoint URL") },
                        placeholder = { Text("https://api.yourwebstudio.com/v1/deploy") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val trimmed = webhookDraft.trim()
                            if (trimmed.isNotEmpty() && !trimmed.startsWith("https://")) {
                                onShowMessage(
                                    if (isHi) "कृपया मान्य https:// URL दर्ज करें" else "Please enter a valid https:// URL"
                                )
                            } else {
                                onSaveWebhookUrl(trimmed)
                                onShowMessage(
                                    if (isHi) "एंडपॉइंट कॉन्फ़िगरेशन सहेजा गया" else "Endpoint configuration saved locally"
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(if (isHi) "एंडपॉइंट सहेजें" else "Save Endpoint Config", color = OnAccentWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // API READINESS MATRIX
            Text(
                text = if (isHi) "भविष्य के वेबसाइट API मॉड्यूल" else "Future Website API Readiness",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AgencyCatalog.apiModules.forEach { mod ->
                    Surface(
                        color = NavySurface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHi) mod.nameHi else mod.nameEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ApiConnectionBadge(label = if (isHi) mod.statusHi else mod.statusEn)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isHi) mod.purposeHi else mod.purposeEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        AgencyFooter(
            language = language,
            whatsAppNumber = whatsAppNumber,
            onNavigate = onNavigate,
            onToggleLanguage = onToggleLanguage,
            onWhatsAppClick = onWhatsAppClick,
            onConfigureWhatsApp = onConfigureWhatsApp
        )
    }

    deployTargetSite?.let { target ->
        WebsiteUploadDeployDialog(
            language = language,
            siteName = target.topic,
            initialEndpoint = webhookUrl,
            onDismiss = { deployTargetSite = null },
            onUploadFromDevice = {
                deployTargetSite = null
                uploadFileLauncher.launch(arrayOf("text/html", "text/plain", "*/*"))
            },
            onDeployToServer = { endpoint ->
                onSaveWebhookUrl(endpoint)
                onDeploySiteToEndpoint(target.topic, target.structuredDraft, endpoint) { _, msg ->
                    onShowMessage(msg)
                }
                deployTargetSite = null
            }
        )
    }
}

@Composable
private fun WorkspaceStatCard(
    label: String,
    value: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, NavyBorder),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = CyanPulse, modifier = Modifier.weight(1f))
                if (actionLabel != null && onAction != null) {
                    OutlinedButton(
                        onClick = onAction,
                        border = BorderStroke(1.dp, ElectricBlue),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(actionLabel, color = ElectricBlue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        }
    }
}

@Composable
private fun EmptyWorkspaceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(34.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(buttonText, color = OnAccentWhite)
            }
        }
    }
}
