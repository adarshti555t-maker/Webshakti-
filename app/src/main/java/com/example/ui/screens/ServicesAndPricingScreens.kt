package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.AgencyCatalog
import com.example.model.AdminCustomTool
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.WebsiteToolType
import com.example.ui.components.AgencyFooter
import com.example.ui.components.ApiConnectionBadge
import com.example.ui.components.ApiConnectionBanner
import com.example.ui.components.Extruded3DCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.neuralGridBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WebsiteTypesScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    adminTools: List<AdminCustomTool>,
    onOpenWebsiteTool: (WebsiteToolType) -> Unit,
    onOpenAdminTool: (AdminCustomTool) -> Unit,
    onRequestCustomWebsite: (WebsiteToolType) -> Unit,
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
                .widthIn(max = 1040.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                eyebrow = if (isHi) "8 प्रकार के वेबसाइट जनरेटर" else "8 SPECIALIZED WEBSITE TYPES",
                title = if (isHi) "हर प्रकार की वेबसाइट बनाने के टूल्स" else "Every Type of Website We Generate & Build",
                subtitle = if (isHi) {
                    "तुरंत HTML5+CSS और वायरफ़्रेम बनाने के लिए कोई भी वेबसाइट प्रकार चुनें, या हमारी वेबसाइट टीम से कस्टम बिल्ड का अनुरोध करें।"
                } else {
                    "Pick any website type to launch the instant HTML5+CSS generator studio, or send a custom build request directly to our web creators."
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ApiConnectionBadge(
                label = if (isHi) {
                    "तत्काल वायरफ़्रेम + HTML5 कोड • क्लाउड AI विस्तार हेतु API तैयार"
                } else {
                    "Instant Wireframe + HTML5 Export • Cloud AI Expansion Ready"
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                AgencyCatalog.websiteTools.forEachIndexed { index, tool ->
                    var expanded by remember { mutableStateOf(index == 0) }
                    val accent = when (index % 3) {
                        0 -> ElectricBlue
                        1 -> CyanPulse
                        else -> DeepSapphire
                    }

                    Extruded3DCard(
                        depthOffset = 5.dp,
                        cornerRadius = 20.dp,
                        accentGlow = accent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(accent.copy(alpha = 0.12f))
                                        .border(1.dp, accent.copy(alpha = 0.42f), RoundedCornerShape(12.dp)),
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
                                        text = "0${index + 1} • WEBSITE GENERATOR TOOL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanPulse
                                    )
                                    Text(
                                        text = if (isHi) tool.titleHi else tool.titleEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { expanded = !expanded },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = if (expanded) "Collapse" else "Expand",
                                        tint = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHi) tool.subtitleHi else tool.subtitleEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            AnimatedVisibility(visible = expanded) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Text(
                                        text = if (isHi) "शामिल वेबसाइट सेक्शन और आउटपुट" else "Included Sections & Output Structure",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CyanPulse
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val sections = if (isHi) tool.defaultSectionsHi else tool.defaultSectionsEn
                                    sections.forEach { sectionName ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = SuccessEmerald,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = sectionName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                val wide = maxWidth > 480.dp
                                if (wide) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { onOpenWebsiteTool(tool.type) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .minimumInteractiveComponentSize()
                                                .testTag("generate_site_${tool.type.name.lowercase()}")
                                        ) {
                                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(17.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHi) "यह वेबसाइट जनरेट करें" else "Generate This Website",
                                                color = OnAccentWhite,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { onRequestCustomWebsite(tool.type) },
                                            border = BorderStroke(1.dp, CyanPulse.copy(alpha = 0.65f)),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .minimumInteractiveComponentSize()
                                                .testTag("request_custom_${tool.type.name.lowercase()}")
                                        ) {
                                            Icon(Icons.Filled.Mail, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHi) "कस्टम बिल्ड अनुरोध" else "Request Custom Build",
                                                color = CyanPulse,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { onOpenWebsiteTool(tool.type) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .minimumInteractiveComponentSize()
                                                .testTag("generate_site_${tool.type.name.lowercase()}")
                                        ) {
                                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(17.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHi) "यह वेबसाइट जनरेट करें" else "Generate This Website",
                                                color = OnAccentWhite,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(16.dp))
                                        }
                                        OutlinedButton(
                                            onClick = { onRequestCustomWebsite(tool.type) },
                                            border = BorderStroke(1.dp, CyanPulse.copy(alpha = 0.65f)),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .minimumInteractiveComponentSize()
                                                .testTag("request_custom_${tool.type.name.lowercase()}")
                                        ) {
                                            Icon(Icons.Filled.Mail, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHi) "कस्टम वेबसाइट बनवाएँ" else "Request Custom Build",
                                                color = CyanPulse
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (adminTools.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(
                    eyebrow = if (isHi) "एडमिन द्वारा जोड़े गए" else "ADDED BY ADMIN",
                    title = if (isHi) "कस्टम वेबसाइट टूल्स" else "Custom Studio Website Tools"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    adminTools.forEach { customTool ->
                        Extruded3DCard(
                            depthOffset = 5.dp,
                            cornerRadius = 20.dp,
                            accentGlow = DeepSapphire,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (customTool.addedByAi) {
                                        if (isHi) "AI द्वारा जोड़ा गया टूल" else "AI GENERATED ADMIN TOOL"
                                    } else {
                                        if (isHi) "एडमिन कस्टम टूल" else "ADMIN CUSTOM TOOL"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepSapphire
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHi) customTool.titleHi else customTool.titleEn,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHi) customTool.subtitleHi else customTool.subtitleEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onOpenAdminTool(customTool) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepSapphire),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(17.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHi) "यह कस्टम वेबसाइट खोलें" else "Open Custom Website",
                                        color = OnAccentWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            ApiConnectionBanner(
                title = if (isHi) "कस्टम डिप्लॉयमेंट और AI विस्तार API" else "Custom Cloud Hosting & Live AI API Readiness",
                description = if (isHi) {
                    "इस ऐप में सभी 8 वेबसाइट जनरेटर ऑफ़लाइन लाइव वायरफ़्रेम और HTML5+CSS बनाते हैं। लाइव डोमेन पब्लिशिंग और डायनामिक बैकएंड के लिए भविष्य का API कनेक्शन चिह्नित है।"
                } else {
                    "All 8 website tools generate structural wireframes and responsive HTML5+CSS offline right now. Cloud hosting deployment and live LLM expansion require future API connection."
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
}
