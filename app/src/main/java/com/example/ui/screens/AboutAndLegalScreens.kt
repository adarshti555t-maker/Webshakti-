package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AgencyCatalog
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.ui.components.AgencyFooter
import com.example.ui.components.Extruded3DCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.neuralGridBackground
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAccentWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WebCreatorsTeamScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI

    val roleIcons = listOf(
        Icons.Filled.DesignServices,
        Icons.Filled.Code,
        Icons.Filled.Search,
        Icons.Filled.Language
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neuralGridBackground()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 980.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionHeader(
                eyebrow = if (isHi) "वेबसाइट जनरेट और बिल्ड करने वाले लोग" else "WEBSITE CREATORS & BUILDERS",
                title = if (isHi) "WEBSHAKTI AI वेबसाइट टीम" else "The People Behind Every Website Build",
                subtitle = if (isHi) {
                    "हमारी टीम पूरी तरह वेबसाइट आर्किटेक्चर, रिस्पॉन्सिव HTML/CSS कोडिंग, टेक्निकल SEO और द्विभाषी वेब लॉन्च पर केंद्रित है।"
                } else {
                    "Our specialists focus exclusively on website generation—combining 3D UI architecture, clean frontend engineering, technical SEO, and bilingual launch support."
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AgencyCatalog.websiteTeamRoles.forEachIndexed { idx, role ->
                    val icon = roleIcons.getOrElse(idx) { Icons.Filled.Groups }
                    val accent = when (idx % 3) {
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
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(accent.copy(alpha = 0.12f))
                                    .border(1.dp, accent.copy(alpha = 0.42f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHi) role.roleHi else role.roleEn,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CyanPulse
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isHi) role.titleHi else role.titleEn,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHi) role.focusHi else role.focusEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = if (isHi) "वेबसाइट निर्माण प्रक्रिया (4 चरण)" else "How Our Web Creators Build Your Site",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            val steps = if (isHi) {
                listOf(
                    "01. वेबसाइट टूल और प्रकार चयन" to "बिज़नेस, ई-कॉमर्स, पोर्टफ़ोलियो, SaaS, ब्लॉग या बुकिंग में से चुनें।",
                    "02. वायरफ़्रेम और HTML5 जनरेशन" to "तुरंत मोबाइल-फ़र्स्ट लेआउट और स्वच्छ CSS कोड तैयार करें।",
                    "03. SEO मेटा व लीगल पेज जांच" to "Open Graph, JSON-LD स्कीमा और Privacy/Terms सेक्शन जोड़ें।",
                    "04. कस्टम टीम समीक्षा व डिप्लॉय" to "हमारी वेबसाइट टीम के साथ कस्टमाइज़ेशन और लाइव होस्टिंग लॉन्च करें।"
                )
            } else {
                listOf(
                    "01. Select Website Tool & Niche" to "Choose Business, E-Commerce, Portfolio, SaaS, Blog, Restaurant, SEO, or Legal.",
                    "02. Instant Wireframe & HTML5 Export" to "Generate responsive page sections, palette tokens, and clean CSS.",
                    "03. Technical SEO & Legal Readiness" to "Attach Open Graph tags, JSON-LD Schema.org metadata, and policy pages.",
                    "04. Web Creator Review & Launch" to "Collaborate with our web builders for custom engineering and domain launch."
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEach { (stepTitle, stepDesc) ->
                    Surface(
                        color = NavySurface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SuccessEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = stepTitle, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = stepDesc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                color = NavyElevated,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isHi) "पारदर्शिता प्रतिबद्धता" else "Transparency Pledge",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyanPulse
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val pledges = if (isHi) {
                        listOf(
                            "हम कोई नकली ग्राहक समीक्षा या गढ़े हुए आँकड़े नहीं दिखाते।",
                            "क्लाउड LLM और लाइव होस्टिंग जैसे API एकीकरण स्पष्ट रूप से चिह्नित हैं।",
                            "कोई नकली पेमेंट पुष्टि नहीं — सभी कस्टम वेबसाइट अनुरोध सीधी चर्चा से शुरू होते हैं।"
                        )
                    } else {
                        listOf(
                            "Zero fabricated client reviews, fake portfolio stats, or simulated payments.",
                            "External cloud LLM and domain hosting APIs are transparently labeled as pending connection.",
                            "HTML5+CSS wireframes generated in the studio work immediately offline with no paid API key required."
                        )
                    }
                    pledges.forEach { line ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SuccessEmerald,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = line, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(AppPage.GENERATOR) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("team_open_generator")
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = OnAccentWhite, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "वेब जनरेटर खोलें" else "Open Generator",
                                color = OnAccentWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = { onNavigate(AppPage.CONTACT) },
                            border = BorderStroke(1.dp, CyanPulse),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("team_request_build")
                        ) {
                            Text(
                                text = if (isHi) "कस्टम बिल्ड पूछें" else "Request Custom Site",
                                color = CyanPulse
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
fun PrivacyPolicyScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val sections = if (isHi) {
        listOf(
            "1. स्थानीय वेबसाइट डेटा संग्रह" to "इस ऑफ़लाइन-रेडी ऐप प्रीव्यू में आपके जनरेट किए गए वेबसाइट HTML ड्राफ़्ट, कस्टम वेबसाइट अनुरोध और संपर्क फ़ॉर्म केवल आपके डिवाइस पर DataStore में स्थानीय रूप से सहेजे जाते हैं।",
            "2. भाषा और WhatsApp प्राथमिकताएँ" to "आपकी चयनित भाषा (EN / हिं) और कॉन्फ़िगर किया गया WhatsApp नंबर केवल आपकी सुविधा के लिए डिवाइस पर सहेजा जाता है।",
            "3. WhatsApp और ईमेल हैंडऑफ़" to "WhatsApp बटन पर टैप करने से आपके डिवाइस का WhatsApp या ब्राउज़र खुलता है। उसके बाद का संवाद संबंधित प्लेटफ़ॉर्म की गोपनीयता नीति के अधीन है।",
            "4. AI Master और API कनेक्शन" to "AI Master में पूछे गए प्रश्न Firebase AI Logic और App Check के माध्यम से सुरक्षित HTTPS पर संसाधित होते हैं। कस्टम होस्टिंग अपलोड भी केवल HTTPS का उपयोग करते हैं।",
            "5. डेटा नियंत्रण और हटाना" to "आप 'सहेजी गई साइट्स' (Saved Sites) से किसी भी सहेजे गए वेबसाइट कोड या पूछताछ को कभी भी हटा सकते हैं।"
        )
    } else {
        listOf(
            "1. Local Website Data Storage" to "In this app, generated website HTML blueprints, custom website build requests, and contact drafts are stored locally on your device via Android DataStore.",
            "2. Language & WhatsApp Preferences" to "Your active language (English or Hindi) and configurable WhatsApp number are saved on-device solely to personalize your workspace.",
            "3. External WhatsApp & Email Links" to "Tapping WhatsApp or Email opens your device's corresponding app to send your website brief directly. External communications follow those platforms' policies.",
            "4. AI Master & Cloud APIs" to "Questions sent to AI Master are processed over encrypted HTTPS via Firebase AI Logic protected by Firebase App Check. Custom hosting endpoint uploads also require HTTPS.",
            "5. Full Data Control" to "You can delete any saved website blueprint or custom build request anytime from the Saved Sites workspace."
        )
    }

    LegalDocumentScaffold(
        icon = Icons.Filled.PrivacyTip,
        eyebrow = if (isHi) "कानूनी दस्तावेज़" else "LEGAL & COMPLIANCE",
        title = if (isHi) "गोपनीयता नीति (Privacy Policy)" else "Privacy Policy",
        subtitle = if (isHi) "अंतिम अपडेट: नवंबर 2026 • WEBSHAKTI AI" else "Effective: November 2026 • WEBSHAKTI AI",
        sections = sections,
        language = language,
        whatsAppNumber = whatsAppNumber,
        onNavigate = onNavigate,
        onToggleLanguage = onToggleLanguage,
        onWhatsAppClick = onWhatsAppClick,
        onConfigureWhatsApp = onConfigureWhatsApp
    )
}

@Composable
fun TermsScreen(
    language: AppLanguage,
    whatsAppNumber: String,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    val isHi = language == AppLanguage.HI
    val sections = if (isHi) {
        listOf(
            "1. शर्तों की स्वीकृति" to "WEBSHAKTI AI वेबसाइट जनरेटर और कस्टम वेब बिल्ड टूल्स का उपयोग करके आप इन नियमों और शर्तों से सहमत होते हैं।",
            "2. वेबसाइट जनरेटर आउटपुट" to "सभी 8 वेबसाइट टूल्स ऑफ़लाइन वायरफ़्रेम, HTML5+CSS टेम्प्लेट और API-रेडी JSON प्रॉम्प्ट पेलोड तैयार करते हैं। प्रोडक्शन उपयोग से पहले कोड की समीक्षा और परीक्षण करें।",
            "3. कस्टम वेबसाइट अनुरोध" to "कस्टम वेबसाइट बिल्ड अनुरोध केवल औपचारिक समीक्षा और सहमति के बाद शुरू होते हैं। यह ऐप कोई स्वचालित पेमेंट नहीं लेता।",
            "4. बौद्धिक संपदा" to "जनरेट किए गए HTML5 टेम्प्लेट का उपयोग आप अपनी वेबसाइट के लिए कर सकते हैं। WEBSHAKTI AI ब्रांड पहचान एजेंसी की संपत्ति है।",
            "5. भविष्य के API मॉड्यूल" to "लाइव AI कॉपी जनरेशन और वन-क्लिक क्लाउड होस्टिंग के लिए बाहरी API कनेक्शन आवश्यक होगा।"
        )
    } else {
        listOf(
            "1. Acceptance of Terms" to "By using the WEBSHAKTI AI Website Generator Studio and custom build tools, you agree to these Terms and Conditions.",
            "2. Website Generator Output" to "All 8 website tools generate structured wireframes, responsive HTML5+CSS templates, and API-ready JSON payloads locally. Review and test exported code before live domain launch.",
            "3. Custom Website Requests" to "Custom build requests submitted in this app do not charge payments or simulate invoices; projects begin only after formal scope review.",
            "4. Intellectual Property" to "You may customize and deploy HTML5 code generated for your brand. WEBSHAKTI AI platform branding remains property of WEBSHAKTI AI.",
            "5. Future API Connections" to "Live generative AI expansion and automated cloud hosting require connecting authorized external APIs."
        )
    }

    LegalDocumentScaffold(
        icon = Icons.Filled.Gavel,
        eyebrow = if (isHi) "सेवा की शर्तें" else "TERMS OF SERVICE",
        title = if (isHi) "नियम और शर्तें (Terms & Conditions)" else "Terms & Conditions",
        subtitle = if (isHi) "अंतिम अपडेट: नवंबर 2026 • WEBSHAKTI AI" else "Effective: November 2026 • WEBSHAKTI AI",
        sections = sections,
        language = language,
        whatsAppNumber = whatsAppNumber,
        onNavigate = onNavigate,
        onToggleLanguage = onToggleLanguage,
        onWhatsAppClick = onWhatsAppClick,
        onConfigureWhatsApp = onConfigureWhatsApp
    )
}

@Composable
private fun LegalDocumentScaffold(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    eyebrow: String,
    title: String,
    subtitle: String,
    sections: List<Pair<String, String>>,
    language: AppLanguage,
    whatsAppNumber: String,
    onNavigate: (AppPage) -> Unit,
    onToggleLanguage: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onConfigureWhatsApp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .neuralGridBackground()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = CyanPulse, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                SectionHeader(eyebrow = eyebrow, title = title, subtitle = subtitle)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                sections.forEach { (heading, content) ->
                    Surface(
                        color = NavySurface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = heading, style = MaterialTheme.typography.titleMedium, color = CyanPulse)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = content, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
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
