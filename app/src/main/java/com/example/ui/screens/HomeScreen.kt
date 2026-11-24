package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.R
import com.example.data.AgencyCatalog
import com.example.model.AdminAnnouncement
import com.example.model.AdminCustomTool
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.WebsiteToolType
import com.example.ui.components.AgencyFooter
import com.example.ui.components.ApiConnectionBadge
import com.example.ui.components.ApiConnectionBanner
import com.example.ui.components.Extruded3DCard
import com.example.ui.components.Interactive3DStageContainer
import com.example.ui.components.Rotating3DNeuralGlobe
import com.example.ui.components.SectionHeader
import com.example.ui.components.neuralGridBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun websiteToolIcon(type: WebsiteToolType): ImageVector = when (type) {
    WebsiteToolType.BUSINESS_WEBSITE -> Icons.Filled.Business
    WebsiteToolType.ECOMMERCE_WEBSITE -> Icons.Filled.ShoppingBag
    WebsiteToolType.PORTFOLIO_WEBSITE -> Icons.Filled.Badge
    WebsiteToolType.SAAS_LANDING_PAGE -> Icons.Filled.RocketLaunch
    WebsiteToolType.BLOG_NEWS_WEBSITE -> Icons.Filled.Article
    WebsiteToolType.RESTAURANT_BOOKING_SITE -> Icons.Filled.Restaurant
    WebsiteToolType.SEO_META_SCHEMA_TOOL -> Icons.Filled.Code
    WebsiteToolType.LEGAL_PAGES_GENERATOR -> Icons.Filled.Gavel
}

@Composable
fun HomeScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    adminTools: List<AdminCustomTool>,
    adminAnnouncements: List<AdminAnnouncement>,
    onNavigate: (AppPage) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenWebsiteTool: (WebsiteToolType) -> Unit,
    onOpenAdminTool: (AdminCustomTool) -> Unit,
    onAskAiMaster: (String?) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val density = LocalDensity.current.density
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { reveal = true }

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
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Quick Command Search Bar
            Surface(
                onClick = onOpenSearch,
                color = NavySurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(ElectricBlue.copy(alpha = 0.55f), NavyBorder))
                ),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("home_search_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isHi) "सभी वेबसाइट जनरेटर टूल्स खोजें..." else "Quick jump: search all website generator tools...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // 1. CRISP WHITE + BLACK 3D INTERACTIVE WEBSITE GENERATOR HERO STAGE (3 ACCENT COLORS)
            AnimatedVisibility(
                visible = reveal,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { 28 }
            ) {
                Interactive3DStageContainer(modifier = Modifier.fillMaxWidth()) { tiltX, tiltY ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        // 3D Extruded Accent Plate Underneath
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .offset(x = 3.dp, y = 8.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            ElectricBlue.copy(alpha = 0.25f),
                                            DeepSapphire.copy(alpha = 0.24f),
                                            CyanPulse.copy(alpha = 0.20f)
                                        )
                                    )
                                )
                        )

                        Surface(
                            shape = RoundedCornerShape(26.dp),
                            color = NavySurface,
                            border = BorderStroke(
                                1.4.dp,
                                Brush.linearGradient(
                                    listOf(
                                        ElectricBlue.copy(alpha = 0.65f),
                                        CyanPulse.copy(alpha = 0.45f),
                                        DeepSapphire.copy(alpha = 0.45f)
                                    )
                                )
                            ),
                            shadowElevation = 10.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // Subtle watermarked 3D visual background over white
                                Image(
                                    painter = painterResource(id = R.drawable.img_hero_agency),
                                    contentDescription = "WEBSHAKTI AI 3D Website Builder Hero",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(310.dp)
                                        .alpha(0.14f)
                                        .graphicsLayer {
                                            scaleX = 1.06f
                                            scaleY = 1.06f
                                            translationX = tiltY * 1.4f
                                            translationY = -tiltX * 1.4f
                                        }
                                )

                                // Bright White Frost Gradient Overlay for crisp Black Text contrast
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(310.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    NavySurface.copy(alpha = 0.68f),
                                                    NavyDeep.copy(alpha = 0.88f),
                                                    NavySurface
                                                )
                                            )
                                        )
                                )

                                // Real-Time Rotating 3D Globe
                                Rotating3DNeuralGlobe(
                                    extraYawRad = (tiltY * PI.toFloat() / 180f) * 2.2f,
                                    extraPitchRad = (tiltX * PI.toFloat() / 180f) * 1.8f,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 8.dp, end = 6.dp)
                                        .size(180.dp)
                                        .offset {
                                            IntOffset(
                                                x = (tiltY * 1.6f).roundToInt(),
                                                y = (-tiltX * 1.6f).roundToInt()
                                            )
                                        }
                                )

                                // Floating 3D Tilt Hint Chip
                                Surface(
                                    color = NavySurface.copy(alpha = 0.94f),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f)),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 152.dp, end = 18.dp)
                                        .offset {
                                            IntOffset(
                                                x = (tiltY * 2.3f).roundToInt(),
                                                y = (-tiltX * 2.3f).roundToInt()
                                            )
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.ViewInAr,
                                            contentDescription = null,
                                            tint = ElectricBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (isHi) "3D टिल्ट के लिए ड्रैग करें" else "Drag to tilt 3D",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextPrimary
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Surface(
                                        color = NavyElevated,
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Web,
                                                contentDescription = null,
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHi) "वेबसाइट जनरेटर स्टूडियो" else "WEBSITE GENERATOR STUDIO",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = ElectricBlue
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(70.dp))

                                    Text(
                                        text = "WEBSHAKTI AI",
                                        style = MaterialTheme.typography.displayLarge,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isHi) {
                                            "हर प्रकार की वेबसाइट बनाने का स्मार्ट स्टूडियो"
                                        } else {
                                            "Generate Every Type of Modern Website."
                                        },
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = CyanPulse
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    ) {
                                        HeroMiniBadge(if (isHi) "8 वेब टूल्स" else "8 Web Tools")
                                        HeroMiniBadge(if (isHi) "डाउनलोड + अपलोड" else "Download + Upload")
                                        HeroMiniBadge(if (isHi) "वेब टीम" else "Web Creators")
                                    }

                                    Text(
                                        text = if (isHi) {
                                            "बिज़नेस, ई-कॉमर्स, पोर्टफ़ोलियो, SaaS, ब्लॉग, रेस्टोरेंट, SEO मेटा और लीगल पेज—तुरंत वेबसाइट कोड जनरेट, डाउनलोड व अपलोड करें या हमारी वेब टीम से कस्टम बनवाएँ।"
                                        } else {
                                            "Build Business, E-Commerce, Portfolio, SaaS, Blog, Restaurant, SEO Meta, and Legal sites—generate, download .html, upload existing files, or collaborate with our web creators."
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                        val isWide = maxWidth > 520.dp
                                        if (isWide) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Button(
                                                    onClick = { onNavigate(AppPage.GENERATOR) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                                                    modifier = Modifier
                                                        .minimumInteractiveComponentSize()
                                                        .testTag("hero_launch_generator")
                                                ) {
                                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = if (isHi) "वेबसाइट जनरेट करें" else "Launch Web Generator",
                                                        color = OnAccentWhite,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                OutlinedButton(
                                                    onClick = { onNavigate(AppPage.TEMPLATES) },
                                                    border = BorderStroke(1.dp, CyanPulse),
                                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                                                    modifier = Modifier.minimumInteractiveComponentSize()
                                                ) {
                                                    Text(
                                                        text = if (isHi) "वेबसाइट प्रकार देखें" else "All Website Types",
                                                        color = CyanPulse
                                                    )
                                                }
                                                OutlinedButton(
                                                    onClick = { onNavigate(AppPage.TEAM) },
                                                    border = BorderStroke(1.dp, DeepSapphire.copy(alpha = 0.55f)),
                                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                                                    modifier = Modifier.minimumInteractiveComponentSize()
                                                ) {
                                                    Text(
                                                        text = if (isHi) "वेब क्रिएटर्स" else "Web Creators",
                                                        color = DeepSapphire
                                                    )
                                                }
                                            }
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Button(
                                                    onClick = { onNavigate(AppPage.GENERATOR) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .minimumInteractiveComponentSize()
                                                        .testTag("hero_launch_generator")
                                                ) {
                                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = if (isHi) "वेबसाइट जनरेटर खोलें" else "Launch Website Generator",
                                                        color = OnAccentWhite,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    OutlinedButton(
                                                        onClick = { onNavigate(AppPage.TEMPLATES) },
                                                        border = BorderStroke(1.dp, CyanPulse),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .minimumInteractiveComponentSize()
                                                    ) {
                                                        Text(
                                                            text = if (isHi) "वेबसाइट प्रकार" else "Site Types",
                                                            color = CyanPulse,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    OutlinedButton(
                                                        onClick = { onNavigate(AppPage.TEAM) },
                                                        border = BorderStroke(1.dp, DeepSapphire.copy(alpha = 0.55f)),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .minimumInteractiveComponentSize()
                                                    ) {
                                                        Text(
                                                            text = if (isHi) "वेब टीम" else "Web Team",
                                                            color = DeepSapphire,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 1B. AI MASTER — WORK & WEBSITE DOUBT CLEARANCE ASSISTANT
            Extruded3DCard(
                depthOffset = 6.dp,
                cornerRadius = 22.dp,
                accentGlow = ElectricBlue,
                onClick = { onAskAiMaster(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_ai_master_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ElectricBlue.copy(alpha = 0.12f))
                                .border(1.dp, ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHi) "AI मास्टर • 24/7 संदेह निवारण" else "AI MASTER • DOUBT SOLVER",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricBlue
                            )
                            Text(
                                text = if (isHi) "अपने काम और वेबसाइट के हर संदेह को दूर करें" else "Clear Any Work, Code, or Website Doubt",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHi) {
                            "HTML/CSS त्रुटि हो, होस्टिंग/डोमेन समझना हो या ऑनलाइन बिज़नेस रणनीति—AI Master से हिंदी या English में तुरंत समाधान पाएँ।"
                        } else {
                            "Stuck with HTML/CSS bugs, hosting, SEO tags, or launching your site? Ask AI Master in English or Hindi and get step-by-step answers."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onAskAiMaster(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("home_ask_ai_master_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = OnAccentWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "AI मास्टर से पूछें" else "Ask AI Master",
                                color = OnAccentWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                onAskAiMaster(
                                    if (isHi) "मेरी डाउनलोड की गई HTML वेबसाइट को ऑनलाइन लाइव कैसे होस्ट करें?"
                                    else "How do I host my downloaded .html website online?"
                                )
                            },
                            border = BorderStroke(1.dp, CyanPulse),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                        ) {
                            Text(
                                text = if (isHi) "होस्टिंग गाइड पूछें" else "Hosting Guide",
                                color = CyanPulse,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // 1C. ADMIN-ADDED STUDIO UPDATES & CUSTOM TOOLS (LIVE WHEN ADDED IN ADMIN PANEL)
            if (adminAnnouncements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    eyebrow = if (isHi) "एडमिन स्टूडियो अपडेट्स" else "STUDIO UPDATES",
                    title = if (isHi) "नवीनतम घोषणाएँ" else "Latest Studio Announcements"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    adminAnnouncements.forEach { notice ->
                        Surface(
                            color = NavyElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, CyanPulse.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = notice.badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanPulse,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = notice.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notice.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            if (adminTools.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    eyebrow = if (isHi) "एडमिन कस्टम टूल्स" else "ADMIN CUSTOM WEBSITE TOOLS",
                    title = if (isHi) "एडमिन द्वारा जोड़े गए नए वेबसाइट टूल्स" else "Custom Website Tools Added by Admin"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    adminTools.forEach { customTool ->
                        Extruded3DCard(
                            depthOffset = 4.dp,
                            cornerRadius = 18.dp,
                            accentGlow = DeepSapphire,
                            onClick = { onOpenAdminTool(customTool) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DeepSapphire.copy(alpha = 0.12f))
                                        .border(1.dp, DeepSapphire.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = DeepSapphire, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isHi) customTool.titleHi else customTool.titleEn,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (isHi) customTool.subtitleHi else customTool.subtitleEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = DeepSapphire, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 2. WEBSITE CREATORS TEAM
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                SectionHeader(
                    eyebrow = if (isHi) "वेबसाइट बनाने वाली टीम" else "WEBSITE CREATORS TEAM",
                    title = if (isHi) "वेबसाइट जनरेट और बिल्ड करने वाले विशेषज्ञ" else "Dedicated Website Builders & Architects",
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onNavigate(AppPage.TEAM) }) {
                    Text(text = if (isHi) "टीम देखें" else "Meet Team", color = ElectricBlue)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth > 620.dp) 4 else 2
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AgencyCatalog.websiteTeamRoles.chunked(cols).forEach { rowRoles ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowRoles.forEachIndexed { idx, role ->
                                val accent = when (idx % 3) {
                                    0 -> ElectricBlue
                                    1 -> CyanPulse
                                    else -> DeepSapphire
                                }
                                Extruded3DCard(
                                    depthOffset = 5.dp,
                                    cornerRadius = 18.dp,
                                    accentGlow = accent,
                                    onClick = { onNavigate(AppPage.TEAM) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(accent.copy(alpha = 0.12f))
                                                .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Groups, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (isHi) role.titleHi else role.titleEn,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isHi) role.focusHi else role.focusEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 3. 3D PERSPECTIVE SWIPEABLE WEBSITE GENERATOR TOOLS DECK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                SectionHeader(
                    eyebrow = if (isHi) "3D वेबसाइट टूल्स डेक • स्वाइप करें" else "3D WEBSITE TOOLS DECK • SWIPE",
                    title = if (isHi) "सभी प्रकार के वेबसाइट जनरेटर टूल्स" else "All Types of Website Generator Tools",
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onNavigate(AppPage.GENERATOR) }) {
                    Text(text = if (isHi) "सभी खोलें" else "Open Studio", color = ElectricBlue)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ApiConnectionBadge(
                label = if (isHi) {
                    "HTML5+CSS डाउनलोड व अपलोड तैयार • AI विस्तार हेतु API"
                } else {
                    "HTML5+CSS Download & Upload Ready • LLM API Hookup"
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            val toolsPagerState = rememberPagerState(pageCount = { AgencyCatalog.websiteTools.size })
            HorizontalPager(
                state = toolsPagerState,
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val tool = AgencyCatalog.websiteTools[page]
                val accent = when (page % 3) {
                    0 -> ElectricBlue
                    1 -> CyanPulse
                    else -> DeepSapphire
                }
                val pageOffset = ((toolsPagerState.currentPage - page) + toolsPagerState.currentPageOffsetFraction)
                val absOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            rotationY = pageOffset * -18f
                            cameraDistance = 14f * density
                            val scale = lerp(0.90f, 1f, 1f - absOffset)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(0.72f, 1f, 1f - absOffset)
                        }
                ) {
                    Extruded3DCard(
                        depthOffset = 6.dp,
                        cornerRadius = 22.dp,
                        accentGlow = accent,
                        onClick = { onOpenWebsiteTool(tool.type) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(accent.copy(alpha = 0.12f))
                                        .border(1.dp, accent.copy(alpha = 0.40f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = websiteToolIcon(tool.type),
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "0${page + 1} / 0${AgencyCatalog.websiteTools.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = accent
                                    )
                                    Text(
                                        text = if (isHi) tool.titleHi else tool.titleEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isHi) tool.subtitleHi else tool.subtitleEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHi) "जनरेट और डाउनलोड करने के लिए टैप करें" else "Tap to generate & download",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = accent
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            PagerDotsIndicator(
                total = AgencyCatalog.websiteTools.size,
                current = toolsPagerState.currentPage
            )

            Spacer(modifier = Modifier.height(22.dp))

            // 4. ALL 8 WEBSITE GENERATOR TOOLS QUICK-ACCESS GRID
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth > 620.dp) 4 else 2
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AgencyCatalog.websiteTools.chunked(cols).forEachIndexed { rowIdx, rowTools ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowTools.forEachIndexed { colIdx, tool ->
                                val accent = when ((rowIdx * cols + colIdx) % 3) {
                                    0 -> ElectricBlue
                                    1 -> CyanPulse
                                    else -> DeepSapphire
                                }
                                Extruded3DCard(
                                    depthOffset = 4.dp,
                                    cornerRadius = 16.dp,
                                    accentGlow = accent,
                                    onClick = { onOpenWebsiteTool(tool.type) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(accent.copy(alpha = 0.12f))
                                                .border(1.dp, accent.copy(alpha = 0.40f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = websiteToolIcon(tool.type),
                                                contentDescription = null,
                                                tint = accent,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (isHi) tool.titleHi else tool.titleEn,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isHi) tool.subtitleHi else tool.subtitleEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            repeat(cols - rowTools.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ApiConnectionBanner(
                title = if (isHi) "डाउनलोड + अपलोड और पारदर्शी API स्थिति" else "Download, Upload & Transparent Future API Status",
                description = if (isHi) {
                    "जनरेट की गई वेबसाइट को सीधे .html फ़ाइल के रूप में डाउनलोड करें या डिवाइस से अपनी HTML फ़ाइल अपलोड करें। लाइव क्लाउड होस्टिंग व AI विस्तार हेतु API एंडपॉइंट चिह्नित हैं।"
                } else {
                    "Download generated websites as .html files or upload existing HTML files from your device. Cloud hosting deployment & live LLM APIs are clearly labeled."
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                                .size(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(SuccessEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "कस्टम वेबसाइट बिल्ड के लिए तैयार" else "READY TO BUILD YOUR WEBSITE",
                            style = MaterialTheme.typography.labelMedium,
                            color = SuccessEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHi) "हमारी वेबसाइट टीम के साथ कस्टम साइट शुरू करें" else "Launch a Custom Website With Our Creators",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHi) {
                            "सहेजे गए वेबसाइट प्रोजेक्ट देखें या कस्टम वेबसाइट निर्माण का अनुरोध भेजें।"
                        } else {
                            "Inspect your saved generated websites or send a custom website build brief directly."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onNavigate(AppPage.CONTACT) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                        ) {
                            Text(if (isHi) "कस्टम अनुरोध" else "Custom Request", color = OnAccentWhite)
                        }
                        OutlinedButton(
                            onClick = { onNavigate(AppPage.WORKSPACE) },
                            border = BorderStroke(1.dp, DeepSapphire),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                        ) {
                            Text(if (isHi) "सहेजी गई साइट्स" else "Saved Sites", color = DeepSapphire)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onNavigate(AppPage.ADMIN) },
                        border = BorderStroke(1.dp, CyanPulse),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("home_open_admin_button")
                    ) {
                        Text(
                            text = if (isHi) "एडमिन पैनल खोलें (पासवर्ड लॉक)" else "Open Admin Panel (Passcode Lock)",
                            color = CyanPulse
                        )
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
private fun HeroMiniBadge(text: String) {
    Surface(
        color = NavyElevated,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.35f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = DeepSapphire,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun PagerDotsIndicator(
    total: Int,
    current: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { idx ->
            val active = idx == current
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(6.dp)
                    .width(if (active) 20.dp else 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (active) ElectricBlue else NavyBorder)
            )
        }
    }
}
