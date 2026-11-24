package com.example.data

import com.example.model.ApiIntegrationModule
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.WebsiteSpecialistRole
import com.example.model.WebsiteToolMeta
import com.example.model.WebsiteToolType

object AgencyCatalog {

    val websiteTools: List<WebsiteToolMeta> = listOf(
        WebsiteToolMeta(
            type = WebsiteToolType.BUSINESS_WEBSITE,
            titleEn = "Business & Corporate Website",
            titleHi = "बिज़नेस और कॉर्पोरेट वेबसाइट",
            subtitleEn = "Generate full company site layout with services, about & inquiry CTA.",
            subtitleHi = "कंपनी परिचय, सेवाओं और पूछताछ बटन के साथ पूर्ण बिज़नेस वेबसाइट बनाएँ।",
            defaultSectionsEn = listOf("Navbar & Brand", "Executive Hero", "Core Services Grid", "Team & Process", "Contact & Footer"),
            defaultSectionsHi = listOf("नेवबार और ब्रांड", "हीरो सेक्शन", "मुख्य सेवाएँ ग्रिड", "टीम और प्रक्रिया", "संपर्क और फ़ुटर"),
            brandHintEn = "e.g., Horizon Global Systems",
            brandHintHi = "उदा., होराइज़न ग्लोबल सिस्टम्स",
            nicheHintEn = "e.g., B2B IT Consulting & Cloud Solutions",
            nicheHintHi = "उदा., B2B IT कंसल्टिंग और क्लाउड सेवाएँ"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.ECOMMERCE_WEBSITE,
            titleEn = "E-Commerce Store Website",
            titleHi = "ई-कॉमर्स स्टोर वेबसाइट",
            subtitleEn = "Generate online store structure with product cards, categories & cart bar.",
            subtitleHi = "प्रोडक्ट कार्ड, कैटेगरी फ़िल्टर और कार्ट लेआउट के साथ ऑनलाइन स्टोर बनाएँ।",
            defaultSectionsEn = listOf("Store Header & Search", "Featured Collection Hero", "Product Showcase Grid", "Shipping & Trust Bar", "Store Footer"),
            defaultSectionsHi = listOf("स्टोर हेडर और खोज", "मुख्य कलेक्शन बैनर", "प्रोडक्ट शोकेस ग्रिड", "शिपिंग और सुरक्षा बार", "स्टोर फ़ुटर"),
            brandHintEn = "e.g., Aura Craft Studio",
            brandHintHi = "उदा., ऑरा क्राफ़्ट स्टूडियो",
            nicheHintEn = "e.g., Handcrafted Modern Home Decor",
            nicheHintHi = "उदा., आधुनिक होम डेकोर उत्पाद"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.PORTFOLIO_WEBSITE,
            titleEn = "Portfolio & Personal Website",
            titleHi = "पोर्टफ़ोलियो और पर्सनल वेबसाइट",
            subtitleEn = "Build a sleek creator, developer, or consultant showcase website.",
            subtitleHi = "क्रिएटर, डेवलपर या कंसल्टेंट के लिए आकर्षक पोर्टफ़ोलियो वेबसाइट बनाएँ।",
            defaultSectionsEn = listOf("Minimal Header", "Profile Intro Hero", "Project Case Grid", "Skills & Stack", "Hire Me Contact"),
            defaultSectionsHi = listOf("मिनिमल हेडर", "प्रोफ़ाइल परिचय", "प्रोजेक्ट केस ग्रिड", "कौशल और टेक स्टैक", "संपर्क सेक्शन"),
            brandHintEn = "e.g., Aarav Studio",
            brandHintHi = "उदा., आरव स्टूडियो",
            nicheHintEn = "e.g., Full-Stack Product Designer",
            nicheHintHi = "उदा., प्रोडक्ट डिज़ाइनर और वेब आर्किटेक्ट"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.SAAS_LANDING_PAGE,
            titleEn = "SaaS & Startup Landing Page",
            titleHi = "SaaS और स्टार्टअप लैंडिंग पेज",
            subtitleEn = "Conversion-focused product launch page with pricing tiers & FAQ.",
            subtitleHi = "प्रोडक्ट लॉन्च के लिए फ़ीचर्स, प्राइसिंग टेबल और FAQ लैंडिंग पेज बनाएँ।",
            defaultSectionsEn = listOf("Product Navbar", "Value Prop Hero", "Feature Matrix", "Pricing Tiers", "FAQ & CTA"),
            defaultSectionsHi = listOf("प्रोडक्ट नेवबार", "वैल्यू प्रॉप हीरो", "फ़ीचर मैट्रिक्स", "प्राइसिंग प्लान", "FAQ और CTA"),
            brandHintEn = "e.g., PulseFlow Cloud",
            brandHintHi = "उदा., पल्सफ़्लो क्लाउड",
            nicheHintEn = "e.g., Automated Team Workflow Software",
            nicheHintHi = "उदा., टीम वर्कफ़्लो ऑटोमेशन सॉफ़्टवेयर"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.BLOG_NEWS_WEBSITE,
            titleEn = "Blog & Magazine Website",
            titleHi = "ब्लॉग और न्यूज़ पोर्टल वेबसाइट",
            subtitleEn = "Editorial layout with featured story, article grid & newsletter signup.",
            subtitleHi = "फ़ीचर्ड स्टोरी, लेख ग्रिड और न्यूज़लेटर के साथ ब्लॉग वेबसाइट बनाएँ।",
            defaultSectionsEn = listOf("Editorial Masthead", "Featured Story Hero", "Article Cards Grid", "Category Sidebar", "Newsletter Footer"),
            defaultSectionsHi = listOf("संपादकीय हेडर", "मुख्य फ़ीचर्ड लेख", "लेख कार्ड ग्रिड", "श्रेणी टैग", "न्यूज़लेटर फ़ुटर"),
            brandHintEn = "e.g., TechInsight Daily",
            brandHintHi = "उदा., टेकइनसाइट डेली",
            nicheHintEn = "e.g., Global Software & Design Trends",
            nicheHintHi = "उदा., तकनीक और डिज़ाइन समाचार"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.RESTAURANT_BOOKING_SITE,
            titleEn = "Restaurant & Booking Website",
            titleHi = "रेस्टोरेंट और बुकिंग वेबसाइट",
            subtitleEn = "Hospitality & appointment site with menu highlights & table booking.",
            subtitleHi = "मेनू हाइलाइट्स, लोकेशन और टेबल बुकिंग CTA के साथ वेबसाइट बनाएँ।",
            defaultSectionsEn = listOf("Hospitality Header", "Ambiance Hero", "Signature Menu Grid", "Booking CTA Section", "Hours & Location"),
            defaultSectionsHi = listOf("हॉस्पिटैलिटी हेडर", "आकर्षक हीरो बैनर", "मुख्य मेनू ग्रिड", "टेबल बुकिंग CTA", "समय और पता"),
            brandHintEn = "e.g., Saffron Courtyard",
            brandHintHi = "उदा., केसर कोर्टयार्ड",
            nicheHintEn = "e.g., Contemporary Dining & Reservations",
            nicheHintHi = "उदा., आधुनिक डाइनिंग और टेबल बुकिंग"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.SEO_META_SCHEMA_TOOL,
            titleEn = "Website SEO & Meta Tag Tool",
            titleHi = "वेबसाइट SEO और मेटा टैग टूल",
            subtitleEn = "Generate complete HTML <meta>, Open Graph, and JSON-LD Schema tags.",
            subtitleHi = "किसी भी वेबसाइट के लिए पूर्ण HTML <meta>, Open Graph और JSON-LD स्कीमा बनाएँ।",
            defaultSectionsEn = listOf("Title & Meta Description", "Open Graph Tags", "Twitter Card Tags", "JSON-LD WebSite Schema"),
            defaultSectionsHi = listOf("टाइटल और मेटा विवरण", "Open Graph टैग", "Twitter कार्ड टैग", "JSON-LD वेबसाइट स्कीमा"),
            brandHintEn = "e.g., NovaWeb Solutions",
            brandHintHi = "उदा., नोवावेब सॉल्यूशंस",
            nicheHintEn = "e.g., Custom Web Engineering Studio",
            nicheHintHi = "उदा., कस्टम वेब इंजीनियरिंग स्टूडियो"
        ),
        WebsiteToolMeta(
            type = WebsiteToolType.LEGAL_PAGES_GENERATOR,
            titleEn = "Website Legal Pages Tool",
            titleHi = "वेबसाइट लीगल पेज जनरेटर",
            subtitleEn = "Generate structured Privacy Policy & Terms HTML sections for your site.",
            subtitleHi = "अपनी वेबसाइट के लिए Privacy Policy और Terms HTML सेक्शन तैयार करें।",
            defaultSectionsEn = listOf("Privacy Policy Header", "Data Collection Clause", "Cookies & Storage", "Terms of Use Section"),
            defaultSectionsHi = listOf("गोपनीयता नीति हेडर", "डेटा संग्रह खंड", "कुकीज़ और स्टोरेज", "सेवा की शर्तें खंड"),
            brandHintEn = "e.g., Vertex Digital",
            brandHintHi = "उदा., वर्टेक्स डिजिटल",
            nicheHintEn = "e.g., Online Consulting Platform",
            nicheHintHi = "उदा., ऑनलाइन कंसल्टिंग प्लेटफ़ॉर्म"
        )
    )

    val websiteTeamRoles: List<WebsiteSpecialistRole> = listOf(
        WebsiteSpecialistRole(
            titleEn = "Website UI/UX Architects",
            titleHi = "वेबसाइट UI/UX आर्किटेक्ट्स",
            roleEn = "Lead Structural & 3D Visual Designers",
            roleHi = "मुख्य विज़ुअल और 3D लेआउट डिज़ाइनर",
            focusEn = "Craft responsive, mobile-first layouts, high-contrast typography, and interactive user journeys.",
            focusHi = "मोबाइल-फ़र्स्ट रिस्पॉन्सिव लेआउट, आधुनिक टाइपोग्राफ़ी और सहज यूज़र फ़्लो डिज़ाइन करते हैं।"
        ),
        WebsiteSpecialistRole(
            titleEn = "Frontend & Code Builders",
            titleHi = "फ़्रंटएंड और कोड बिल्डर्स",
            roleEn = "Semantic HTML, CSS & Modern Web Engineers",
            roleHi = "HTML, CSS और मॉडर्न वेब इंजीनियर",
            focusEn = "Turn website blueprints into fast, accessible, cross-browser production code.",
            focusHi = "वेबसाइट ब्लूप्रिंट को तेज़, सुलभ और सभी ब्राउज़र के अनुकूल कोड में बदलते हैं।"
        ),
        WebsiteSpecialistRole(
            titleEn = "Technical SEO & Schema Specialists",
            titleHi = "टेक्निकल SEO और स्कीमा विशेषज्ञ",
            roleEn = "Search Architecture & Performance Leads",
            roleHi = "सर्च आर्किटेक्चर और स्पीड विशेषज्ञ",
            focusEn = "Ensure every generated site has clean heading hierarchy, Open Graph tags, and JSON-LD schema.",
            focusHi = "हर वेबसाइट में सही हेडिंग संरचना, मेटा टैग और JSON-LD स्कीमा सुनिश्चित करते हैं।"
        ),
        WebsiteSpecialistRole(
            titleEn = "Bilingual Website Launch Team",
            titleHi = "द्विभाषी वेबसाइट लॉन्च टीम",
            roleEn = "English & Hindi Localization + Deployment",
            roleHi = "अंग्रेज़ी व हिंदी लोकलाइज़ेशन और डिप्लॉयमेंट",
            focusEn = "Prepare multi-language website copy, WhatsApp inquiry routing, and custom domain readiness.",
            focusHi = "द्विभाषी वेबसाइट कॉपी, WhatsApp पूछताछ लिंक और लाइव डोमेन लॉन्च की तैयारी करते हैं।"
        )
    )

    val apiModules: List<ApiIntegrationModule> = listOf(
        ApiIntegrationModule(
            nameEn = "AI Master Doubt Assistant (Firebase AI Logic)",
            nameHi = "AI मास्टर संदेह निवारण (Firebase AI Logic)",
            purposeEn = "Live Gemini 3.8 Flash assistant for work doubts, HTML/CSS debugging, SEO, and website strategy.",
            purposeHi = "काम के संदेह, HTML/CSS समस्या, SEO और वेबसाइट मार्गदर्शन के लिए लाइव Gemini 3.8 Flash सहायक।",
            statusEn = "Live via Firebase AI Logic",
            statusHi = "Firebase AI Logic द्वारा सक्रिय"
        ),
        ApiIntegrationModule(
            nameEn = "Custom Domain & Hosting Upload API",
            nameHi = "कस्टम डोमेन और होस्टिंग अपलोड API",
            purposeEn = "Download .html packages locally or upload to your configured HTTPS hosting endpoint.",
            purposeHi = "HTML पैकेज लोकल डाउनलोड करें या अपने HTTPS होस्टिंग एंडपॉइंट पर अपलोड करें।",
            statusEn = "Local Export & HTTPS Upload Ready",
            statusHi = "लोकल एक्सपोर्ट और HTTPS अपलोड तैयार"
        ),
        ApiIntegrationModule(
            nameEn = "Website Inquiry & CRM Webhook API",
            nameHi = "वेबसाइट पूछताछ और CRM वेबहुक API",
            purposeEn = "Syncs custom website build requests directly to your cloud CRM or WhatsApp.",
            purposeHi = "कस्टम वेबसाइट अनुरोधों को सीधे आपके CRM या WhatsApp से जोड़ता है।",
            statusEn = "Local Workspace + WhatsApp Active",
            statusHi = "लोकल वर्कस्पेस + WhatsApp सक्रिय"
        )
    )

    fun pageTitle(page: AppPage, lang: AppLanguage): String = when (page) {
        AppPage.HOME -> if (lang == AppLanguage.EN) "Home" else "होम"
        AppPage.GENERATOR -> if (lang == AppLanguage.EN) "Web Generator" else "वेब जनरेटर"
        AppPage.AI_MASTER -> if (lang == AppLanguage.EN) "AI Master" else "AI मास्टर"
        AppPage.TEMPLATES -> if (lang == AppLanguage.EN) "Website Types" else "वेबसाइट प्रकार"
        AppPage.TEAM -> if (lang == AppLanguage.EN) "Web Creators" else "वेब क्रिएटर्स"
        AppPage.WORKSPACE -> if (lang == AppLanguage.EN) "Saved Sites" else "सहेजी गई साइट्स"
        AppPage.CONTACT -> if (lang == AppLanguage.EN) "Custom Build" else "कस्टम वेबसाइट"
        AppPage.ADMIN -> if (lang == AppLanguage.EN) "Admin Panel" else "एडमिन पैनल"
        AppPage.PRIVACY -> if (lang == AppLanguage.EN) "Privacy Policy" else "गोपनीयता नीति"
        AppPage.TERMS -> if (lang == AppLanguage.EN) "Terms & Conditions" else "नियम और शर्तें"
    }
}
