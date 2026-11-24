package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AgencyCatalog
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.ui.theme.AmberNotice
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyAbyss
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhatsAppGreen

fun pageIcon(page: AppPage): ImageVector = when (page) {
    AppPage.HOME -> Icons.Filled.Home
    AppPage.GENERATOR -> Icons.Filled.AutoAwesome
    AppPage.AI_MASTER -> Icons.Filled.Psychology
    AppPage.TEMPLATES -> Icons.Filled.Web
    AppPage.TEAM -> Icons.Filled.Groups
    AppPage.WORKSPACE -> Icons.Filled.Dashboard
    AppPage.CONTACT -> Icons.Filled.Mail
    AppPage.ADMIN -> Icons.Filled.AdminPanelSettings
    AppPage.PRIVACY -> Icons.Filled.PrivacyTip
    AppPage.TERMS -> Icons.Filled.Gavel
}

fun launchWhatsApp(context: Context, rawNumber: String, prefilledText: String) {
    val digits = rawNumber.filter { it.isDigit() }
    val uri = Uri.parse("https://wa.me/$digits?text=${Uri.encode(prefilledText)}")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching {
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(
            context,
            "No browser or WhatsApp handler found for $rawNumber",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun shareTextContent(context: Context, subject: String, body: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    val chooser = Intent.createChooser(sendIntent, subject).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching {
        context.startActivity(chooser)
    }.onFailure {
        Toast.makeText(context, "Unable to open share sheet", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun Modifier.neuralGridBackground(): Modifier = this.drawBehind {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(NavyAbyss, NavyDeep, NavyAbyss)
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                ElectricBlue.copy(alpha = 0.08f),
                DeepSapphire.copy(alpha = 0.04f),
                Color.Transparent
            ),
            center = Offset(size.width * 0.85f, size.height * 0.12f),
            radius = size.maxDimension * 0.55f
        )
    )
    val step = 56.dp.toPx()
    val gridColor = ElectricBlue.copy(alpha = 0.06f)
    var x = 0f
    while (x <= size.width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
        x += step
    }
    var y = 0f
    while (y <= size.height) {
        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += step
    }
}

@Composable
fun WebShaktiLogoMark(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF010C22),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(CyanPulse.copy(alpha = 0.7f), ElectricBlue.copy(alpha = 0.5f)))
        ),
        shadowElevation = 4.dp,
        modifier = modifier.size(34.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_art),
            contentDescription = "WEBSHAKTI AI Brand Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun WebShaktiTopBar(
    currentPage: AppPage,
    language: AppLanguage,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigateHome: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Surface(
        color = NavySurface.copy(alpha = 0.96f),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("menu_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = if (language == AppLanguage.EN) "Open navigation menu" else "नेविगेशन मेनू खोलें",
                        tint = TextPrimary
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateHome)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WebShaktiLogoMark()
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "WEBSHAKTI AI",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = AgencyCatalog.pageTitle(currentPage, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanPulse,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("topbar_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = if (language == AppLanguage.EN) "Quick search" else "त्वरित खोज",
                        tint = CyanPulse
                    )
                }

                // Language Toggle Pill (EN | हिं)
                Surface(
                    onClick = onToggleLanguage,
                    shape = RoundedCornerShape(50),
                    color = NavyElevated,
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("language_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = if (language == AppLanguage.EN) "Switch language" else "भाषा बदलें",
                            tint = CyanPulse,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (language == AppLanguage.EN) "EN • हिं" else "हिं • EN",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onWhatsAppClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("topbar_whatsapp_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreen.copy(alpha = 0.18f))
                            .border(1.dp, WhatsAppGreen.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "WhatsApp Contact",
                            tint = WhatsAppGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = NavyBorder.copy(alpha = 0.7f), thickness = 1.dp)
        }
    }
}

@Composable
fun AgencyDrawerContent(
    currentPage: AppPage,
    language: AppLanguage,
    whatsAppNumber: String,
    onSelectPage: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenWhatsAppConfig: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    ModalDrawerSheet(
        drawerContainerColor = NavyDeep,
        drawerContentColor = TextPrimary,
        modifier = Modifier.widthIn(max = 310.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                WebShaktiLogoMark(modifier = Modifier.size(38.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "WEBSHAKTI AI",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isHi) "आपका वैश्विक AI बिज़नेस पार्टनर" else "Your Global AI Business Partner",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanPulse
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = NavyBorder)
            Spacer(modifier = Modifier.height(12.dp))

            AppPage.entries.forEach { page ->
                val selected = currentPage == page
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = pageIcon(page),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = AgencyCatalog.pageTitle(page, language),
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    selected = selected,
                    onClick = { onSelectPage(page) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = ElectricBlue.copy(alpha = 0.22f),
                        selectedIconColor = CyanPulse,
                        selectedTextColor = TextPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .testTag("drawer_nav_${page.route}")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = NavyBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Quick settings inside drawer
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHi) "त्वरित सेटिंग्स" else "Quick Configuration",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanPulse
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize(),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Language, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "English में बदलें" else "Switch to हिंदी (Hindi)",
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onOpenWhatsAppConfig,
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize(),
                        border = BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WhatsApp: $whatsAppNumber",
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private val primaryNavPages = listOf(
    AppPage.HOME,
    AppPage.GENERATOR,
    AppPage.AI_MASTER,
    AppPage.WORKSPACE,
    AppPage.CONTACT
)

@Composable
fun AgencyBottomNavBar(
    currentPage: AppPage,
    language: AppLanguage,
    onSelectPage: (AppPage) -> Unit
) {
    NavigationBar(
        containerColor = NavyDeep,
        contentColor = TextPrimary,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 8.dp
    ) {
        primaryNavPages.forEach { page ->
            val selected = currentPage == page
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectPage(page) },
                icon = {
                    Icon(
                        imageVector = pageIcon(page),
                        contentDescription = AgencyCatalog.pageTitle(page, language)
                    )
                },
                label = {
                    Text(
                        text = when (page) {
                            AppPage.GENERATOR -> if (language == AppLanguage.EN) "Generator" else "जनरेटर"
                            AppPage.AI_MASTER -> if (language == AppLanguage.EN) "AI Master" else "AI मास्टर"
                            AppPage.WORKSPACE -> if (language == AppLanguage.EN) "Saved" else "सहेजे गए"
                            AppPage.CONTACT -> if (language == AppLanguage.EN) "Request" else "अनुरोध"
                            else -> AgencyCatalog.pageTitle(page, language)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricBlue,
                    selectedTextColor = ElectricBlue,
                    indicatorColor = ElectricBlue.copy(alpha = 0.14f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("bottom_nav_${page.route}")
            )
        }
    }
}

@Composable
fun AgencyNavRail(
    currentPage: AppPage,
    language: AppLanguage,
    onSelectPage: (AppPage) -> Unit
) {
    NavigationRail(
        containerColor = NavySurface,
        contentColor = TextPrimary
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        primaryNavPages.forEach { page ->
            val selected = currentPage == page
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectPage(page) },
                icon = {
                    Icon(
                        imageVector = pageIcon(page),
                        contentDescription = AgencyCatalog.pageTitle(page, language)
                    )
                },
                label = {
                    Text(
                        text = AgencyCatalog.pageTitle(page, language),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ElectricBlue,
                    selectedTextColor = ElectricBlue,
                    indicatorColor = ElectricBlue.copy(alpha = 0.14f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

@Composable
fun ApiConnectionBadge(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = AmberNotice.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, AmberNotice.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Api,
                contentDescription = null,
                tint = AmberNotice,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AmberNotice
            )
        }
    }
}

@Composable
fun ApiConnectionBanner(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NavyElevated.copy(alpha = 0.85f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AmberNotice.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AmberNotice.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Api,
                    contentDescription = null,
                    tint = AmberNotice,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = AmberNotice
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = CyanPulse
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun WhatsAppConfigDialog(
    currentNumber: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val isHi = language == AppLanguage.HI
    var draft by remember(currentNumber) { mutableStateOf(currentNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = if (isHi) "WhatsApp बिज़नेस नंबर सेट करें" else "Configure WhatsApp Contact Number",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = if (isHi) {
                        "कंट्री कोड के साथ अपना सक्रिय WhatsApp बिज़नेस नंबर दर्ज करें (उदा. +919569221396)।"
                    } else {
                        "Enter your active WhatsApp Business number with country code (e.g., +919569221396)."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    singleLine = true,
                    label = { Text(if (isHi) "WhatsApp नंबर" else "WhatsApp Number") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPulse,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = CyanPulse,
                        unfocusedLabelColor = TextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("whatsapp_config_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(draft)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.testTag("whatsapp_config_save")
            ) {
                Text(if (isHi) "सहेजें" else "Save Number", color = OnAccentWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHi) "रद्द करें" else "Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgencyFooter(
    language: AppLanguage,
    whatsAppNumber: String,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    Surface(
        color = NavyDeep,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WebShaktiLogoMark(modifier = Modifier.size(34.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "WEBSHAKTI AI",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isHi) "आपका वैश्विक AI बिज़नेस पार्टनर" else "Your Global AI Business Partner",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanPulse
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isHi) {
                    "हर प्रकार की व्यावसायिक, ई-कॉमर्स, पोर्टफ़ोलियो, SaaS, ब्लॉग और SEO वेबसाइट जनरेट करने का संपूर्ण स्टूडियो।"
                } else {
                    "Dedicated website generation studio & web-builder toolkit for every kind of business, store, portfolio, SaaS, and blog."
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isHi) "त्वरित नेविगेशन" else "Quick Navigation",
                style = MaterialTheme.typography.labelMedium,
                color = CyanPulse
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppPage.entries.forEach { page ->
                    Surface(
                        onClick = { onNavigate(page) },
                        color = NavySurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = AgencyCatalog.pageTitle(page, language),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onWhatsAppClick,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHi) "WhatsApp चैट" else "WhatsApp Chat",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onConfigureWhatsApp,
                    border = BorderStroke(1.dp, NavyBorder),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = CyanPulse,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = whatsAppNumber,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = NavyBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHi) {
                        "© WEBSHAKTI AI • पारदर्शी ऑफ़लाइन प्रीव्यू बिल्ड"
                    } else {
                        "© WEBSHAKTI AI • Transparent Offline Preview Build"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onToggleLanguage) {
                    Text(
                        text = if (isHi) "English" else "हिंदी",
                        color = CyanPulse
                    )
                }
            }
        }
    }
}
