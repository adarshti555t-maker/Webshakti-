package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AgencyCatalog
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.WebsiteToolType
import com.example.ui.components.AgencyFooter
import com.example.ui.components.ApiConnectionBanner
import com.example.ui.components.SectionHeader
import com.example.ui.components.launchWhatsApp
import com.example.ui.components.neuralGridBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun ContactScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    initialTab: Int,
    prefilledWebsiteType: WebsiteToolType?,
    prefilledRequirements: String?,
    onSelectTab: (Int) -> Unit,
    onSubmitContact: (String, String, String, String) -> Unit,
    onSubmitWebsiteRequest: (String, String, String, String, String, String) -> Unit,
    onShowMessage: (String) -> Unit,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val context = LocalContext.current

    // Contact Form State
    var cName by rememberSaveable { mutableStateOf("") }
    var cEmail by rememberSaveable { mutableStateOf("") }
    var cSubject by rememberSaveable { mutableStateOf("") }
    var cMessage by rememberSaveable { mutableStateOf("") }

    // Custom Website Request Form State
    var sName by rememberSaveable { mutableStateOf("") }
    var sEmail by rememberSaveable { mutableStateOf("") }
    var sCompany by rememberSaveable { mutableStateOf("") }
    var sTimeline by rememberSaveable { mutableStateOf("1–2 Weeks") }
    var sRequirements by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(prefilledRequirements) {
        if (!prefilledRequirements.isNullOrBlank()) {
            sRequirements = prefilledRequirements
        }
    }

    var selectedWebsiteTool by remember(prefilledWebsiteType) {
        mutableStateOf(
            AgencyCatalog.websiteTools.firstOrNull { it.type == prefilledWebsiteType }
                ?: AgencyCatalog.websiteTools.first()
        )
    }

    LaunchedEffect(prefilledWebsiteType, prefilledRequirements) {
        if (prefilledWebsiteType != null || !prefilledRequirements.isNullOrBlank()) {
            onSelectTab(1)
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
                .widthIn(max = 960.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                eyebrow = if (isHi) "कस्टम वेबसाइट अनुरोध और संपर्क" else "CUSTOM WEBSITE BUILD & CONTACT",
                title = if (isHi) "हमारी वेबसाइट टीम से कस्टम साइट बनवाएँ" else "Request a Custom Website or Contact Us",
                subtitle = if (isHi) {
                    "अपनी वेबसाइट का प्रकार चुनें, कस्टम आवश्यकताएँ सहेजें, या सीधे अपने कॉन्फ़िगर किए गए WhatsApp नंबर पर चैट करें।"
                } else {
                    "Select your website category, submit your custom build specifications, or connect directly with our web creators on WhatsApp."
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Configurable WhatsApp Direct Card
            Surface(
                color = NavyElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHi) "कॉन्फ़िगर करने योग्य WhatsApp लाइन" else "Configurable WhatsApp Direct Line",
                                style = MaterialTheme.typography.titleMedium,
                                color = WhatsAppGreen
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = whatsAppNumber,
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary
                            )
                        }
                        OutlinedButton(
                            onClick = onConfigureWhatsApp,
                            border = BorderStroke(1.dp, CyanPulse),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("edit_whatsapp_number_button")
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHi) "नंबर बदलें" else "Edit Number", color = CyanPulse)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onWhatsAppClick,
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("contact_whatsapp_launch")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "WhatsApp पर तुरंत चैट करें" else "Message Web Team on WhatsApp",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ApiConnectionBanner(
                title = if (isHi) "लोकल सेव + WhatsApp / ईमेल हैंडऑफ़ (CRM API लंबित)" else "Local Save + Direct Handoff (CRM Webhook Pending)",
                description = if (isHi) {
                    "सबमिट किए गए कस्टम वेबसाइट अनुरोध 'सहेजी गई साइट्स' (Saved Sites) में तुरंत सहेजे जाते हैं। स्वचालित क्लाउड CRM सिंक के लिए भविष्य का वेबहुक API आवश्यक है।"
                } else {
                    "Submitted website briefs are saved locally in Saved Sites right away. Automated server CRM sync requires connecting a future Webhook API."
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val safeTab = initialTab.coerceIn(0, 1)
                    TabRow(
                        selectedTabIndex = safeTab,
                        containerColor = NavyElevated,
                        contentColor = CyanPulse,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[safeTab]),
                                color = CyanPulse
                            )
                        }
                    ) {
                        Tab(
                            selected = safeTab == 0,
                            onClick = { onSelectTab(0) },
                            text = { Text(if (isHi) "सामान्य संपर्क" else "Contact Form") }
                        )
                        Tab(
                            selected = safeTab == 1,
                            onClick = { onSelectTab(1) },
                            text = { Text(if (isHi) "कस्टम वेबसाइट अनुरोध" else "Custom Website Request") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (safeTab == 0) {
                        OutlinedTextField(
                            value = cName,
                            onValueChange = { cName = it },
                            label = { Text(if (isHi) "पूरा नाम *" else "Full Name *") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_name_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cEmail,
                            onValueChange = { cEmail = it },
                            label = { Text(if (isHi) "बिज़नेस ईमेल *" else "Business Email *") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_email_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cSubject,
                            onValueChange = { cSubject = it },
                            label = { Text(if (isHi) "विषय" else "Subject") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cMessage,
                            onValueChange = { cMessage = it },
                            label = { Text(if (isHi) "आपका संदेश *" else "Your Message *") },
                            minLines = 3,
                            colors = contactFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_message_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (cName.isBlank() || cEmail.isBlank() || cMessage.isBlank()) {
                                    onShowMessage(
                                        if (isHi) "कृपया नाम, ईमेल और संदेश भरें।" else "Please fill in name, email, and message."
                                    )
                                } else {
                                    onSubmitContact(cName, cEmail, cSubject.ifBlank { "Website Consultation" }, cMessage)
                                    onShowMessage(
                                        if (isHi) "संपर्क संदेश सहेजा गया!" else "Contact message saved to Saved Sites!"
                                    )
                                    cName = ""
                                    cEmail = ""
                                    cSubject = ""
                                    cMessage = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            contentPadding = PaddingValues(vertical = 14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("submit_contact_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHi) "संदेश सहेजें" else "Save Contact Message",
                                color = OnAccentWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = sName,
                            onValueChange = { sName = it },
                            label = { Text(if (isHi) "पूरा नाम *" else "Full Name *") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("website_req_name_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = sEmail,
                            onValueChange = { sEmail = it },
                            label = { Text(if (isHi) "ईमेल *" else "Work Email *") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("website_req_email_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = sCompany,
                            onValueChange = { sCompany = it },
                            label = { Text(if (isHi) "ब्रांड / कंपनी का नाम" else "Brand / Website Name") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isHi) "वेबसाइट का प्रकार चुनें" else "Select Website Type",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanPulse
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AgencyCatalog.websiteTools.forEach { tool ->
                                val active = selectedWebsiteTool.type == tool.type
                                FilterChip(
                                    selected = active,
                                    onClick = { selectedWebsiteTool = tool },
                                    label = { Text(if (isHi) tool.titleHi else tool.titleEn) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricBlue,
                                        selectedLabelColor = OnAccentWhite,
                                        containerColor = NavyElevated,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = sTimeline,
                            onValueChange = { sTimeline = it },
                            label = { Text(if (isHi) "अनुमानित लॉन्च समय" else "Target Launch Timeline") },
                            singleLine = true,
                            colors = contactFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = sRequirements,
                            onValueChange = { sRequirements = it },
                            label = { Text(if (isHi) "वेबसाइट के पेज, फ़ीचर्स और आवश्यकताएँ *" else "Pages, Features & Website Requirements *") },
                            minLines = 3,
                            colors = contactFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("website_req_details_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (sName.isBlank() || sEmail.isBlank() || sRequirements.isBlank()) {
                                        onShowMessage(
                                            if (isHi) "कृपया नाम, ईमेल और वेबसाइट आवश्यकताएँ भरें।" else "Please complete name, email, and website requirements."
                                        )
                                    } else {
                                        val siteTypeTitle = if (isHi) selectedWebsiteTool.titleHi else selectedWebsiteTool.titleEn
                                        onSubmitWebsiteRequest(
                                            sName,
                                            sEmail,
                                            sCompany,
                                            siteTypeTitle,
                                            sTimeline,
                                            sRequirements
                                        )
                                        onShowMessage(
                                            if (isHi) "कस्टम वेबसाइट अनुरोध सहेजा गया!" else "Custom website request saved to Saved Sites!"
                                        )
                                        sRequirements = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("submit_website_request_button")
                            ) {
                                Text(
                                    text = if (isHi) "अनुरोध सहेजें" else "Save Request",
                                    color = OnAccentWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val siteTypeTitle = if (isHi) selectedWebsiteTool.titleHi else selectedWebsiteTool.titleEn
                                    val body = "WEBSHAKTI AI Custom Website Request\nName: $sName\nBrand: $sCompany\nWebsite Type: $siteTypeTitle\nTimeline: $sTimeline\nRequirements: $sRequirements"
                                    launchWhatsApp(context, whatsAppNumber, body)
                                },
                                border = BorderStroke(1.dp, WhatsAppGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHi) "WhatsApp भेजें" else "Send WhatsApp",
                                    color = WhatsAppGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val siteTypeTitle = if (isHi) selectedWebsiteTool.titleHi else selectedWebsiteTool.titleEn
                                val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_SUBJECT, "WEBSHAKTI AI Custom Website Brief: $siteTypeTitle")
                                    putExtra(Intent.EXTRA_TEXT, "Name: $sName\nBrand: $sCompany\nTimeline: $sTimeline\n\n$sRequirements")
                                }
                                runCatching { context.startActivity(mailIntent) }.onFailure {
                                    Toast.makeText(context, "No email app available", Toast.LENGTH_SHORT).show()
                                }
                            },
                            border = BorderStroke(1.dp, NavyBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "ईमेल ऐप से खोलें" else "Open in Email App",
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
}

@Composable
private fun contactFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanPulse,
    unfocusedBorderColor = NavyBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = CyanPulse,
    unfocusedLabelColor = TextMuted
)
