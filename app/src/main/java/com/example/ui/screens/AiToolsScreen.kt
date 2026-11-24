package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AgencyCatalog
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.GeneratedWebsiteResult
import com.example.model.WebsiteToolType
import com.example.ui.components.AgencyFooter
import com.example.ui.components.ApiConnectionBanner
import com.example.ui.components.SectionHeader
import com.example.ui.components.neuralGridBackground
import com.example.ui.components.shareTextContent
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

fun readDisplayFileName(context: Context, uri: Uri): String {
    var name = "uploaded_website.html"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && cursor.moveToFirst()) {
            name = cursor.getString(idx) ?: name
        }
    }
    return name
}

fun buildSafeHtmlFileName(brandName: String): String {
    val slug = brandName.lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .ifBlank { "webshakti_site" }
    return "${slug}_website.html"
}

@Composable
fun WebsiteGeneratorScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    webhookUrl: String,
    selectedTool: WebsiteToolType,
    generatedSite: GeneratedWebsiteResult?,
    onSelectTool: (WebsiteToolType) -> Unit,
    onGenerateWebsite: (WebsiteToolType, String, String, String, String) -> Unit,
    onImportHtmlWebsite: (String, String) -> String,
    onSaveWebsite: () -> Unit,
    onSaveWebhookUrl: (String) -> Unit,
    onDeploySiteToEndpoint: (String, String, String, (Boolean, String) -> Unit) -> Unit,
    onShowMessage: (String) -> Unit,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val currentMeta = AgencyCatalog.websiteTools.first { it.type == selectedTool }

    var brandName by rememberSaveable(selectedTool) { mutableStateOf("") }
    var niche by rememberSaveable(selectedTool) { mutableStateOf("") }
    var ctaText by rememberSaveable(selectedTool) { mutableStateOf("") }
    var selectedAccentHex by rememberSaveable(selectedTool) { mutableStateOf("#155EEF") }
    var outputTab by remember { mutableIntStateOf(0) }
    var showDeployDialog by remember { mutableStateOf(false) }

    // 3 Crisp Accent Swatches matching the app theme
    val accentSwatches = listOf(
        "#155EEF" to ElectricBlue,
        "#0284C7" to CyanPulse,
        "#4338CA" to DeepSapphire
    )

    // SAF Download Launcher: saves generated HTML directly to Downloads / device storage (zero permissions required)
    val downloadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri ->
        val htmlToWrite = generatedSite?.htmlCode
        if (uri != null && htmlToWrite != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(htmlToWrite.toByteArray(Charsets.UTF_8))
                }
            }.onSuccess {
                onShowMessage(
                    if (isHi) "वेबसाइट HTML फ़ाइल सफलतापूर्वक डाउनलोड हुई!"
                    else "Website HTML file downloaded successfully!"
                )
            }.onFailure {
                onShowMessage(
                    if (isHi) "डाउनलोड विफल रहा।" else "Download failed."
                )
            }
        }
    }

    // SAF Upload Launcher: imports an existing .html / .txt website file from the device
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
                    onShowMessage(if (isHi) "चयनित फ़ाइल खाली है।" else "Selected file is empty.")
                } else {
                    val importedName = onImportHtmlWebsite(fileName, content)
                    outputTab = 1
                    onShowMessage(
                        if (isHi) "अपलोड सफल: $importedName"
                        else "Uploaded & loaded: $importedName"
                    )
                }
            }.onFailure {
                onShowMessage(if (isHi) "फ़ाइल अपलोड नहीं हो सकी।" else "Unable to read uploaded file.")
            }
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
                .widthIn(max = 1000.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                eyebrow = if (isHi) "8 वेबसाइट जनरेटर टूल्स" else "WEBSITE GENERATOR STUDIO",
                title = if (isHi) "सभी प्रकार के वेबसाइट बिल्डर टूल्स" else "Generate, Download & Upload Websites",
                subtitle = if (isHi) {
                    "कोई भी वेबसाइट प्रकार चुनें, कोड जनरेट करें, .html फ़ाइल डाउनलोड करें या डिवाइस/सर्वर पर अपलोड करें।"
                } else {
                    "Pick any website tool, generate live HTML5+CSS code, download .html to your device, or upload an existing HTML file."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Upload Existing HTML Website Card
            Surface(
                color = NavyElevated,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHi) "मौजूदा HTML वेबसाइट अपलोड करें" else "Upload Existing Website File",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isHi) {
                                "डिवाइस से .html फ़ाइल अपलोड करके प्रीव्यू, एडिट और सेव करें।"
                            } else {
                                "Upload a .html file from device storage to inspect, save & re-export."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(
                        onClick = { uploadFileLauncher.launch(arrayOf("text/html", "text/plain", "*/*")) },
                        border = BorderStroke(1.dp, ElectricBlue),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("upload_html_file_button")
                    ) {
                        Icon(Icons.Filled.UploadFile, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHi) "अपलोड फ़ाइल" else "Upload HTML", color = ElectricBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 8 Website Tool Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AgencyCatalog.websiteTools.forEach { tool ->
                    val active = tool.type == selectedTool
                    FilterChip(
                        selected = active,
                        onClick = { onSelectTool(tool.type) },
                        label = {
                            Text(
                                text = if (isHi) tool.titleHi else tool.titleEn,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = websiteToolIcon(tool.type),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = OnAccentWhite,
                            selectedLeadingIconColor = OnAccentWhite,
                            containerColor = NavySurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = active,
                            borderColor = NavyBorder,
                            selectedBorderColor = ElectricBlue
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("tool_chip_${tool.type.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Website Builder Input Card
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, NavyBorder),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = websiteToolIcon(currentMeta.type),
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) currentMeta.titleHi else currentMeta.titleEn,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHi) currentMeta.subtitleHi else currentMeta.subtitleEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanPulse
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text(if (isHi) "वेबसाइट / ब्रांड का नाम *" else "Website / Brand Name *") },
                        placeholder = { Text(if (isHi) currentMeta.brandHintHi else currentMeta.brandHintEn) },
                        singleLine = true,
                        colors = toolFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_topic_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = niche,
                        onValueChange = { niche = it },
                        label = { Text(if (isHi) "बिज़नेस श्रेणी / विवरण" else "Industry / Website Purpose") },
                        placeholder = { Text(if (isHi) currentMeta.nicheHintHi else currentMeta.nicheHintEn) },
                        singleLine = true,
                        colors = toolFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_audience_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ctaText,
                        onValueChange = { ctaText = it },
                        label = { Text(if (isHi) "प्रमुख बटन (CTA टेक्स्ट)" else "Primary Button Text (CTA)") },
                        placeholder = { Text(if (isHi) "उदा. अभी संपर्क करें / खरीदें" else "e.g., Book a Call / Shop Now") },
                        singleLine = true,
                        colors = toolFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHi) "थीम एक्सेंट रंग चुनें (3 रंग)" else "Select Accent Color (3 Curated Accents)",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        accentSwatches.forEach { (hex, color) ->
                            val selected = selectedAccentHex == hex
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selected) 3.dp else 1.dp,
                                        color = if (selected) TextPrimary else NavyBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAccentHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selected) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (brandName.isBlank()) {
                                onShowMessage(
                                    if (isHi) "कृपया पहले वेबसाइट / ब्रांड का नाम दर्ज करें।" else "Please enter a website or brand name first."
                                )
                            } else {
                                onGenerateWebsite(selectedTool, brandName, niche, ctaText, selectedAccentHex)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("build_blueprint_button")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "वेबसाइट कोड और प्रीव्यू जनरेट करें" else "Generate Website & HTML Code",
                            color = OnAccentWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Generated / Uploaded Website Output Card (Preview | HTML+CSS | API Prompt + Download & Upload)
            AnimatedVisibility(visible = generatedSite != null) {
                generatedSite?.let { site ->
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Surface(
                            color = NavySurface,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.4.dp, ElectricBlue.copy(alpha = 0.55f)),
                            shadowElevation = 6.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TabRow(
                                    selectedTabIndex = outputTab,
                                    containerColor = NavyDeep,
                                    contentColor = ElectricBlue,
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[outputTab]),
                                            color = ElectricBlue
                                        )
                                    }
                                ) {
                                    Tab(
                                        selected = outputTab == 0,
                                        onClick = { outputTab = 0 },
                                        text = { Text(if (isHi) "प्रीव्यू" else "Preview") },
                                        icon = { Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                    Tab(
                                        selected = outputTab == 1,
                                        onClick = { outputTab = 1 },
                                        text = { Text("HTML + CSS") },
                                        icon = { Icon(Icons.Filled.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                    Tab(
                                        selected = outputTab == 2,
                                        onClick = { outputTab = 2 },
                                        text = { Text(if (isHi) "API प्रॉम्प्ट" else "API Prompt") },
                                        icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                val activeCopyText = when (outputTab) {
                                    0 -> site.wireframeSections.joinToString("\n") { "${it.first}: ${it.second}" }
                                    1 -> site.htmlCode
                                    else -> site.apiPromptPayload
                                }

                                if (outputTab == 0) {
                                    Surface(
                                        color = NavyDeep,
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, NavyBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(NavySurface)
                                                    .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = site.brandName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = ElectricBlue
                                                )
                                                Text(
                                                    text = "https://${site.brandName.lowercase().replace(Regex("[^a-z0-9]"), "")}.com",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            site.wireframeSections.forEach { (sectionTitle, sectionBody) ->
                                                Surface(
                                                    color = NavySurface,
                                                    shape = RoundedCornerShape(12.dp),
                                                    border = BorderStroke(1.dp, NavyBorder),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Text(
                                                            text = sectionTitle,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = CyanPulse
                                                        )
                                                        Spacer(modifier = Modifier.height(3.dp))
                                                        Text(
                                                            text = sectionBody,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = TextPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val lineCount = activeCopyText.lines().size
                                    val charCount = activeCopyText.length
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TelemetryChip(
                                            label = if (isHi) "$lineCount पंक्तियाँ" else "$lineCount lines",
                                            modifier = Modifier.weight(1f)
                                        )
                                        TelemetryChip(
                                            label = if (isHi) "$charCount अक्षर" else "$charCount chars",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = NavyDeep,
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, NavyBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = activeCopyText,
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = JetBrainsMonoFamily),
                                            color = TextPrimary,
                                            modifier = Modifier
                                                .padding(14.dp)
                                                .testTag("tool_output_text")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // PRIMARY DOWNLOAD & UPLOAD ACTION BAR
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            downloadLauncher.launch(buildSafeHtmlFileName(site.brandName))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("download_website_button")
                                    ) {
                                        Icon(Icons.Filled.Download, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(17.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHi) "डाउनलोड (.html)" else "Download .html",
                                            color = OnAccentWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = { showDeployDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = DeepSapphire),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("upload_deploy_website_button")
                                    ) {
                                        Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(17.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHi) "अपलोड / डिप्लॉय" else "Upload / Deploy",
                                            color = OnAccentWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // SECONDARY COPY / SHARE / SAVE WORKSPACE ROW
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(activeCopyText))
                                            onShowMessage(if (isHi) "क्लिपबोर्ड पर कॉपी किया गया" else "Copied to clipboard")
                                        },
                                        border = BorderStroke(1.dp, CyanPulse),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isHi) "कॉपी" else "Copy", color = CyanPulse)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            shareTextContent(context, "WEBSHAKTI AI — ${site.brandName}", site.htmlCode)
                                        },
                                        border = BorderStroke(1.dp, NavyBorder),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "Share HTML", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onSaveWebsite()
                                            onShowMessage(if (isHi) "वेबसाइट प्रोजेक्ट सहेजा गया" else "Website saved to Workspace")
                                        },
                                        border = BorderStroke(1.dp, ElectricBlue),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("save_blueprint_button")
                                    ) {
                                        Icon(Icons.Filled.BookmarkAdd, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isHi) "सहेजें" else "Save", color = ElectricBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            ApiConnectionBanner(
                title = if (isHi) "ऑफ़लाइन डाउनलोड सक्रिय • क्लाउड होस्टिंग API" else "Offline Download & File Upload Active • Hosting API",
                description = if (isHi) {
                    "जनरेट की गई वेबसाइट को सीधे .html फ़ाइल के रूप में डाउनलोड या डिवाइस से अपलोड करें। लाइव सर्वर पब्लिशिंग के लिए आपका https:// होस्टिंग API आवश्यक है।"
                } else {
                    "Download generated websites as .html files or upload existing HTML files immediately offline. Live web hosting deployment requires your https:// API endpoint."
                }
            )
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

    if (showDeployDialog && generatedSite != null) {
        WebsiteUploadDeployDialog(
            language = language,
            siteName = generatedSite.brandName,
            initialEndpoint = webhookUrl,
            onDismiss = { showDeployDialog = false },
            onUploadFromDevice = {
                showDeployDialog = false
                uploadFileLauncher.launch(arrayOf("text/html", "text/plain", "*/*"))
            },
            onDeployToServer = { endpoint ->
                onSaveWebhookUrl(endpoint)
                onDeploySiteToEndpoint(generatedSite.brandName, generatedSite.htmlCode, endpoint) { _, msg ->
                    onShowMessage(msg)
                }
                showDeployDialog = false
            }
        )
    }
}

@Composable
fun WebsiteUploadDeployDialog(
    language: AppLanguage,
    siteName: String,
    initialEndpoint: String,
    onDismiss: () -> Unit,
    onUploadFromDevice: () -> Unit,
    onDeployToServer: (String) -> Unit
) {
    val isHi = language == AppLanguage.HI
    var endpointDraft by remember(initialEndpoint) { mutableStateOf(initialEndpoint) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = if (isHi) "वेबसाइट अपलोड / डिप्लॉय करें" else "Upload or Deploy Website",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isHi) {
                        "1) डिवाइस से अपनी HTML वेबसाइट फ़ाइल अपलोड करें, या 2) \"$siteName\" को अपने लाइव https:// होस्टिंग API पर भेजें।"
                    } else {
                        "Choose: 1) Upload an existing .html file from device storage, or 2) Upload \"$siteName\" to your configured https:// Hosting API."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                OutlinedButton(
                    onClick = onUploadFromDevice,
                    border = BorderStroke(1.dp, CyanPulse),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.UploadFile, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "डिवाइस से .html फ़ाइल अपलोड करें" else "Upload .html File from Device",
                        color = CyanPulse
                    )
                }
                OutlinedTextField(
                    value = endpointDraft,
                    onValueChange = { endpointDraft = it },
                    label = { Text(if (isHi) "लाइव होस्टिंग / डिप्लॉय API URL" else "Hosting / Deploy API URL (https://)") },
                    placeholder = { Text("https://api.yourhost.com/v1/deploy") },
                    singleLine = true,
                    colors = toolFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onDeployToServer(endpointDraft) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text(
                    text = if (isHi) "सर्वर पर अपलोड करें" else "Upload to Server",
                    color = OnAccentWhite
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHi) "बंद करें" else "Close", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun TelemetryChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NavyDeep,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CyanPulse,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun toolFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricBlue,
    unfocusedBorderColor = NavyBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = ElectricBlue,
    unfocusedLabelColor = TextMuted,
    focusedPlaceholderColor = TextMuted,
    unfocusedPlaceholderColor = TextMuted
)
