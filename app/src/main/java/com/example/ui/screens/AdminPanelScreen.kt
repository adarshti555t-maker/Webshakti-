package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.AgencyCatalog
import com.example.model.AdminAiAddStatus
import com.example.model.AdminAnnouncement
import com.example.model.AdminCustomTool
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.WebsiteToolType
import com.example.ui.components.AgencyFooter
import com.example.ui.components.Extruded3DCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.neuralGridBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminPanelScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    isAdminUnlocked: Boolean,
    unlockError: String?,
    adminTools: List<AdminCustomTool>,
    adminAnnouncements: List<AdminAnnouncement>,
    aiAddStatus: AdminAiAddStatus,
    onUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onClearUnlockError: () -> Unit,
    onAddToolViaAi: (String) -> Unit,
    onAddAnnouncementViaAi: (String) -> Unit,
    onAddToolManually: (String, String, String, WebsiteToolType) -> Unit,
    onAddAnnouncementManually: (String, String, String) -> Unit,
    onDeleteTool: (String) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
    onOpenAdminTool: (AdminCustomTool) -> Unit,
    onClearAiAddStatus: () -> Unit,
    onShowMessage: (String) -> Unit,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI

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
                eyebrow = if (isHi) "पासवर्ड सुरक्षित नियंत्रण केंद्र" else "PASSCODE PROTECTED CONTROL STUDIO",
                title = if (isHi) "WEBSHAKTI AI एडमिन पैनल" else "WEBSHAKTI AI Admin Panel",
                subtitle = if (isHi) {
                    "पासवर्ड डालकर लॉगिन करें और AI द्वारा या मैन्युअल रूप से नए वेबसाइट टूल्स व अपडेट जोड़ें।"
                } else {
                    "Enter the admin passcode to unlock and add new website tools or studio updates via AI or manually."
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isAdminUnlocked) {
                AdminPasscodeGateCard(
                    isHi = isHi,
                    unlockError = unlockError,
                    onPasscodeChanged = onClearUnlockError,
                    onSubmitPasscode = { code ->
                        if (onUnlock(code)) {
                            onShowMessage(
                                if (isHi) "एडमिन पैनल अनलॉक हो गया!" else "Admin Panel unlocked!"
                            )
                        }
                    }
                )
            } else {
                AdminUnlockedConsole(
                    isHi = isHi,
                    language = language,
                    adminTools = adminTools,
                    adminAnnouncements = adminAnnouncements,
                    aiAddStatus = aiAddStatus,
                    onLock = {
                        onLock()
                        onShowMessage(if (isHi) "एडमिन पैनल लॉक हो गया" else "Admin Panel locked")
                    },
                    onAddToolViaAi = onAddToolViaAi,
                    onAddAnnouncementViaAi = onAddAnnouncementViaAi,
                    onAddToolManually = onAddToolManually,
                    onAddAnnouncementManually = onAddAnnouncementManually,
                    onDeleteTool = onDeleteTool,
                    onDeleteAnnouncement = onDeleteAnnouncement,
                    onOpenAdminTool = onOpenAdminTool,
                    onClearAiAddStatus = onClearAiAddStatus,
                    onShowMessage = onShowMessage
                )
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
private fun AdminPasscodeGateCard(
    isHi: Boolean,
    unlockError: String?,
    onPasscodeChanged: () -> Unit,
    onSubmitPasscode: (String) -> Unit
) {
    var passcodeInput by remember { mutableStateOf("") }
    var showPasscode by remember { mutableStateOf(false) }

    Extruded3DCard(
        depthOffset = 6.dp,
        cornerRadius = 22.dp,
        accentGlow = DeepSapphire,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DeepSapphire.copy(alpha = 0.12f))
                        .border(1.dp, DeepSapphire.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = DeepSapphire,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHi) "एडमिन एक्सेस लॉक है" else "Admin Access Locked",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHi) {
                            "AI द्वारा नए फीचर/वेबसाइट टूल जोड़ने के लिए एडमिन पासवर्ड दर्ज करें।"
                        } else {
                            "Enter the Admin Passcode to unlock AI & manual content additions."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passcodeInput,
                onValueChange = {
                    passcodeInput = it
                    if (unlockError != null) onPasscodeChanged()
                },
                label = { Text(if (isHi) "एडमिन पासवर्ड दर्ज करें *" else "Admin Passcode *") },
                placeholder = { Text(if (isHi) "पासवर्ड डालें..." else "Enter passcode...") },
                singleLine = true,
                isError = unlockError != null,
                visualTransformation = if (showPasscode) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { showPasscode = !showPasscode },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = if (showPasscode) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPasscode) "Hide passcode" else "Show passcode",
                            tint = TextMuted
                        )
                    }
                },
                colors = adminFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_passcode_input")
            )

            if (unlockError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = ErrorCoral.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ErrorCoral.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = ErrorCoral,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = unlockError,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorCoral
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSubmitPasscode(passcodeInput) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("admin_unlock_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.LockOpen,
                    contentDescription = null,
                    tint = OnAccentWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi) "एडमिन पैनल खोलें" else "Unlock Admin Panel",
                    color = OnAccentWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminUnlockedConsole(
    isHi: Boolean,
    language: AppLanguage,
    adminTools: List<AdminCustomTool>,
    adminAnnouncements: List<AdminAnnouncement>,
    aiAddStatus: AdminAiAddStatus,
    onLock: () -> Unit,
    onAddToolViaAi: (String) -> Unit,
    onAddAnnouncementViaAi: (String) -> Unit,
    onAddToolManually: (String, String, String, WebsiteToolType) -> Unit,
    onAddAnnouncementManually: (String, String, String) -> Unit,
    onDeleteTool: (String) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
    onOpenAdminTool: (AdminCustomTool) -> Unit,
    onClearAiAddStatus: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) }

    // Unlocked Status Banner
    Surface(
        color = NavyElevated,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.2.dp, SuccessEmerald.copy(alpha = 0.55f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Filled.AdminPanelSettings,
                    contentDescription = null,
                    tint = SuccessEmerald,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHi) "एडमिन मोड सक्रिय" else "Admin Mode Unlocked",
                        style = MaterialTheme.typography.titleSmall,
                        color = SuccessEmerald,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHi) {
                            "${adminTools.size} कस्टम टूल्स • ${adminAnnouncements.size} अपडेट्स"
                        } else {
                            "${adminTools.size} custom tools • ${adminAnnouncements.size} updates live"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            OutlinedButton(
                onClick = onLock,
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("admin_lock_button")
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isHi) "लॉक करें" else "Lock", color = TextPrimary)
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Tab Switcher: AI Add | Manual Add | Manage Live Items
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = NavyDeep,
            contentColor = ElectricBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = ElectricBlue
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text(if (isHi) "AI से जोड़ें" else "Add via AI", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text(if (isHi) "मैन्युअल जोड़ें" else "Manual Add", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    val count = adminTools.size + adminAnnouncements.size
                    Text(if (isHi) "सक्रिय ($count)" else "Live ($count)", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                icon = { Icon(Icons.Filled.Web, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    when (activeTab) {
        0 -> AdminAiAddTab(
            isHi = isHi,
            aiAddStatus = aiAddStatus,
            onAddToolViaAi = onAddToolViaAi,
            onAddAnnouncementViaAi = onAddAnnouncementViaAi,
            onClearStatus = onClearAiAddStatus,
            onShowMessage = onShowMessage
        )
        1 -> AdminManualAddTab(
            isHi = isHi,
            language = language,
            onAddToolManually = onAddToolManually,
            onAddAnnouncementManually = onAddAnnouncementManually,
            onShowMessage = onShowMessage
        )
        else -> AdminManageItemsTab(
            isHi = isHi,
            adminTools = adminTools,
            adminAnnouncements = adminAnnouncements,
            onOpenAdminTool = onOpenAdminTool,
            onDeleteTool = onDeleteTool,
            onDeleteAnnouncement = onDeleteAnnouncement,
            onShowMessage = onShowMessage
        )
    }
}

@Composable
private fun AdminAiAddTab(
    isHi: Boolean,
    aiAddStatus: AdminAiAddStatus,
    onAddToolViaAi: (String) -> Unit,
    onAddAnnouncementViaAi: (String) -> Unit,
    onClearStatus: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var addMode by remember { mutableIntStateOf(0) } // 0 = Website Tool, 1 = Studio Update
    var promptDraft by remember { mutableStateOf("") }
    val isBusy = aiAddStatus is AdminAiAddStatus.Loading

    val samplePrompts = remember(addMode, isHi) {
        if (addMode == 0) {
            if (isHi) {
                listOf(
                    "रियल एस्टेट प्रॉपर्टी लिस्टिंग वेबसाइट टूल जोड़ें",
                    "डेंटल क्लिनिक अपॉइंटमेंट और डॉक्टर प्रोफाइल वेबसाइट टूल बनाएँ",
                    "कोचिंग इंस्टीट्यूट व ऑनलाइन कोर्स वेबसाइट टूल जोड़ें"
                )
            } else {
                listOf(
                    "Add a Real Estate Property Listing website generator tool",
                    "Create a Dental Clinic & Doctor Appointment website tool",
                    "Add a Coaching Institute & Online Courses website tool"
                )
            }
        } else {
            if (isHi) {
                listOf(
                    "सभी यूज़र्स के लिए नया 3D वेबसाइट टूल उपलब्ध होने की सूचना जोड़ें",
                    "कस्टम वेबसाइट बनवाने के लिए WhatsApp सपोर्ट अपडेट पोस्ट करें"
                )
            } else {
                listOf(
                    "Post an announcement about our new AI-powered website templates",
                    "Publish a studio notice for quick WhatsApp custom website support"
                )
            }
        }
    }

    Extruded3DCard(
        depthOffset = 5.dp,
        cornerRadius = 20.dp,
        accentGlow = ElectricBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHi) "AI द्वारा ऐप में नया आइटम जोड़ें" else "Add New Items via AI (Gemini 3.8 Flash)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isHi) {
                    "बस लिखें कि आप ऐप में कौन-सा नया वेबसाइट टूल या अपडेट जोड़ना चाहते हैं—AI उसका HTML टेम्प्लेट और सेक्शन खुद तैयार करके लाइव जोड़ देगा।"
                } else {
                    "Describe the new website tool or studio update you want to add—AI builds the bilingual metadata, layout sections, and HTML5 template automatically."
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = addMode == 0,
                    onClick = { addMode = 0 },
                    label = { Text(if (isHi) "वेबसाइट टूल" else "Website Tool") },
                    leadingIcon = { Icon(Icons.Filled.Web, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = OnAccentWhite,
                        selectedLeadingIconColor = OnAccentWhite
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
                FilterChip(
                    selected = addMode == 1,
                    onClick = { addMode = 1 },
                    label = { Text(if (isHi) "स्टूडियो अपडेट" else "Studio Update") },
                    leadingIcon = { Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPulse,
                        selectedLabelColor = OnAccentWhite,
                        selectedLeadingIconColor = OnAccentWhite
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                samplePrompts.forEach { sample ->
                    Surface(
                        onClick = { promptDraft = sample },
                        color = NavyElevated,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f)),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = sample,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = promptDraft,
                onValueChange = {
                    promptDraft = it
                    if (aiAddStatus !is AdminAiAddStatus.Idle && !isBusy) onClearStatus()
                },
                label = {
                    Text(
                        if (addMode == 0) {
                            if (isHi) "AI को नया वेबसाइट टूल बनाने का निर्देश दें *" else "Describe Website Tool to Add via AI *"
                        } else {
                            if (isHi) "AI को स्टूडियो अपडेट लिखने का निर्देश दें *" else "Describe Studio Announcement for AI *"
                        }
                    )
                },
                placeholder = {
                    Text(
                        if (isHi) "उदा. जिम और फिटनेस मेंबरशिप वेबसाइट टूल जोड़ो..." else "e.g., Add a Gym & Fitness Club website tool..."
                    )
                },
                minLines = 3,
                maxLines = 5,
                colors = adminFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_ai_prompt_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (promptDraft.isBlank()) {
                        onShowMessage(if (isHi) "कृपया पहले निर्देश लिखें।" else "Please enter an instruction first.")
                    } else if (addMode == 0) {
                        onAddToolViaAi(promptDraft)
                    } else {
                        onAddAnnouncementViaAi(promptDraft)
                    }
                },
                enabled = !isBusy,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("admin_ai_generate_add_button")
            ) {
                if (isBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.2.dp,
                        color = OnAccentWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "AI तैयार कर रहा है..." else "AI Generating & Adding...",
                        color = OnAccentWhite
                    )
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "AI द्वारा जनरेट कर जोड़ें" else "Generate & Add via AI",
                        color = OnAccentWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (aiAddStatus) {
                is AdminAiAddStatus.Success -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = SuccessEmerald.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = aiAddStatus.message, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }
                is AdminAiAddStatus.Error -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = ErrorCoral.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ErrorCoral.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = ErrorCoral, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = aiAddStatus.message, style = MaterialTheme.typography.bodySmall, color = ErrorCoral)
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun AdminManualAddTab(
    isHi: Boolean,
    language: AppLanguage,
    onAddToolManually: (String, String, String, WebsiteToolType) -> Unit,
    onAddAnnouncementManually: (String, String, String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    var mode by remember { mutableIntStateOf(0) } // 0 = Tool, 1 = Announcement
    var toolTitle by remember { mutableStateOf("") }
    var toolSubtitle by remember { mutableStateOf("") }
    var toolSections by remember { mutableStateOf("Hero Section, Features, Pricing, Contact Form") }
    var baseType by remember { mutableStateOf(WebsiteToolType.BUSINESS_WEBSITE) }

    var noticeTitle by remember { mutableStateOf("") }
    var noticeMessage by remember { mutableStateOf("") }
    var noticeBadge by remember { mutableStateOf("NEW") }

    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, NavyBorder),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = mode == 0,
                    onClick = { mode = 0 },
                    label = { Text(if (isHi) "कस्टम वेब टूल" else "Custom Web Tool") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = OnAccentWhite
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
                FilterChip(
                    selected = mode == 1,
                    onClick = { mode = 1 },
                    label = { Text(if (isHi) "नोटिस / अपडेट" else "Studio Update") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPulse,
                        selectedLabelColor = OnAccentWhite
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mode == 0) {
                OutlinedTextField(
                    value = toolTitle,
                    onValueChange = { toolTitle = it },
                    label = { Text(if (isHi) "वेबसाइट टूल का नाम *" else "Website Tool Title *") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_manual_tool_title")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = toolSubtitle,
                    onValueChange = { toolSubtitle = it },
                    label = { Text(if (isHi) "विवरण / उद्देश्य" else "Subtitle / Purpose") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = toolSections,
                    onValueChange = { toolSections = it },
                    label = { Text(if (isHi) "सेक्शन (कॉमा से अलग करें)" else "Page Sections (comma separated)") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isHi) "बेस टेम्प्लेट शैली चुनें" else "Base Template Style",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AgencyCatalog.websiteTools.take(6).forEach { meta ->
                        val active = baseType == meta.type
                        FilterChip(
                            selected = active,
                            onClick = { baseType = meta.type },
                            label = {
                                Text(if (language == AppLanguage.HI) meta.titleHi else meta.titleEn)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepSapphire,
                                selectedLabelColor = OnAccentWhite
                            ),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (toolTitle.isBlank()) {
                            onShowMessage(if (isHi) "टूल का नाम दर्ज करें" else "Please enter tool title")
                        } else {
                            onAddToolManually(toolTitle, toolSubtitle, toolSections, baseType)
                            toolTitle = ""
                            toolSubtitle = ""
                            onShowMessage(if (isHi) "नया वेबसाइट टूल जोड़ा गया!" else "Custom website tool added!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("admin_manual_add_tool_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHi) "वेबसाइट टूल जोड़ें" else "Add Website Tool", color = OnAccentWhite, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedTextField(
                    value = noticeTitle,
                    onValueChange = { noticeTitle = it },
                    label = { Text(if (isHi) "अपडेट शीर्षक *" else "Update Title *") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = noticeMessage,
                    onValueChange = { noticeMessage = it },
                    label = { Text(if (isHi) "संदेश *" else "Update Message *") },
                    minLines = 2,
                    maxLines = 4,
                    colors = adminFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = noticeBadge,
                    onValueChange = { noticeBadge = it },
                    label = { Text(if (isHi) "बैज लेबल" else "Badge Label") },
                    singleLine = true,
                    colors = adminFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (noticeTitle.isBlank() || noticeMessage.isBlank()) {
                            onShowMessage(if (isHi) "शीर्षक और संदेश दोनों भरें" else "Enter both title and message")
                        } else {
                            onAddAnnouncementManually(noticeTitle, noticeMessage, noticeBadge)
                            noticeTitle = ""
                            noticeMessage = ""
                            onShowMessage(if (isHi) "स्टूडियो अपडेट जोड़ा गया!" else "Studio update added!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPulse),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHi) "अपडेट जोड़ें" else "Publish Update", color = OnAccentWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminManageItemsTab(
    isHi: Boolean,
    adminTools: List<AdminCustomTool>,
    adminAnnouncements: List<AdminAnnouncement>,
    onOpenAdminTool: (AdminCustomTool) -> Unit,
    onDeleteTool: (String) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (isHi) "एडमिन द्वारा जोड़े गए वेबसाइट टूल्स (${adminTools.size})" else "Admin-Added Website Tools (${adminTools.size})",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        if (adminTools.isEmpty()) {
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isHi) "अभी कोई एडमिन टूल नहीं जोड़ा गया है।" else "No admin custom website tools added yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            adminTools.forEach { tool ->
                Surface(
                    color = NavySurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (tool.addedByAi) {
                                        if (isHi) "AI द्वारा जोड़ा गया" else "ADDED VIA AI"
                                    } else {
                                        if (isHi) "एडमिन कस्टम" else "ADMIN CUSTOM"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricBlue
                                )
                                Text(
                                    text = if (isHi) tool.titleHi else tool.titleEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                            }
                            IconButton(
                                onClick = {
                                    onDeleteTool(tool.id)
                                    onShowMessage(if (isHi) "टूल हटाया गया" else "Tool removed")
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete tool", tint = ErrorCoral)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHi) tool.subtitleHi else tool.subtitleEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { onOpenAdminTool(tool) },
                            border = BorderStroke(1.dp, ElectricBlue),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHi) "जनरेटर में खोलें" else "Open in Web Generator", color = ElectricBlue)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isHi) "एडमिन स्टूडियो अपडेट्स (${adminAnnouncements.size})" else "Admin Studio Updates (${adminAnnouncements.size})",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        if (adminAnnouncements.isEmpty()) {
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isHi) "अभी कोई अपडेट प्रकाशित नहीं है।" else "No studio updates published yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            adminAnnouncements.forEach { notice ->
                Surface(
                    color = NavySurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CyanPulse.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = notice.badge, style = MaterialTheme.typography.labelSmall, color = CyanPulse)
                            Text(text = notice.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = notice.message, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        IconButton(
                            onClick = {
                                onDeleteAnnouncement(notice.id)
                                onShowMessage(if (isHi) "अपडेट हटाया गया" else "Update removed")
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete update", tint = ErrorCoral)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun adminFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricBlue,
    unfocusedBorderColor = NavyBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = ElectricBlue,
    unfocusedLabelColor = TextMuted,
    focusedPlaceholderColor = TextMuted,
    unfocusedPlaceholderColor = TextMuted
)
