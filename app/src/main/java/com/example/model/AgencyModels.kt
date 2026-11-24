package com.example.model

object AppRoutes {
    const val HOME = "home"
    const val GENERATOR = "generator"
    const val AI_MASTER = "ai_master"
    const val TEMPLATES = "templates"
    const val TEAM = "team"
    const val WORKSPACE = "workspace"
    const val CONTACT = "contact"
    const val PRIVACY = "privacy"
    const val TERMS = "terms"
    const val ADMIN = "admin"
}

enum class AppPage(val route: String) {
    HOME(AppRoutes.HOME),
    GENERATOR(AppRoutes.GENERATOR),
    AI_MASTER(AppRoutes.AI_MASTER),
    TEMPLATES(AppRoutes.TEMPLATES),
    TEAM(AppRoutes.TEAM),
    WORKSPACE(AppRoutes.WORKSPACE),
    CONTACT(AppRoutes.CONTACT),
    ADMIN(AppRoutes.ADMIN),
    PRIVACY(AppRoutes.PRIVACY),
    TERMS(AppRoutes.TERMS)
}

data class AiMasterMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String
)

sealed interface AiMasterStatus {
    data object Idle : AiMasterStatus
    data object Loading : AiMasterStatus
    data class Streaming(val partialText: String) : AiMasterStatus
    data class Error(val message: String) : AiMasterStatus
}

enum class AppLanguage(val code: String, val label: String) {
    EN("en", "EN"),
    HI("hi", "हिं")
}

enum class WebsiteToolType {
    BUSINESS_WEBSITE,
    ECOMMERCE_WEBSITE,
    PORTFOLIO_WEBSITE,
    SAAS_LANDING_PAGE,
    BLOG_NEWS_WEBSITE,
    RESTAURANT_BOOKING_SITE,
    SEO_META_SCHEMA_TOOL,
    LEGAL_PAGES_GENERATOR
}

data class WebsiteToolMeta(
    val type: WebsiteToolType,
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val defaultSectionsEn: List<String>,
    val defaultSectionsHi: List<String>,
    val brandHintEn: String,
    val brandHintHi: String,
    val nicheHintEn: String,
    val nicheHintHi: String
)

data class GeneratedWebsiteResult(
    val toolType: WebsiteToolType,
    val brandName: String,
    val niche: String,
    val accentHex: String,
    val wireframeSections: List<Pair<String, String>>,
    val htmlCode: String,
    val apiPromptPayload: String,
    val createdAtLabel: String
)

data class WebsiteSpecialistRole(
    val titleEn: String,
    val titleHi: String,
    val roleEn: String,
    val roleHi: String,
    val focusEn: String,
    val focusHi: String
)

data class SavedInquiry(
    val id: String,
    val type: String,
    val fullName: String,
    val email: String,
    val companyOrSubject: String,
    val serviceOrTopic: String,
    val planTier: String,
    val timeline: String,
    val message: String,
    val timestamp: String
)

data class SavedBlueprint(
    val id: String,
    val toolName: String,
    val topic: String,
    val structuredDraft: String,
    val apiPromptPayload: String,
    val timestamp: String
)

data class ApiIntegrationModule(
    val nameEn: String,
    val nameHi: String,
    val purposeEn: String,
    val purposeHi: String,
    val statusEn: String,
    val statusHi: String
)

data class AdminCustomTool(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val baseType: WebsiteToolType,
    val defaultBrandHint: String,
    val sections: List<String>,
    val customHtmlTemplate: String,
    val addedByAi: Boolean,
    val timestamp: String
)

data class AdminAnnouncement(
    val id: String,
    val title: String,
    val message: String,
    val badge: String,
    val addedByAi: Boolean,
    val timestamp: String
)

sealed interface AdminAiAddStatus {
    data object Idle : AdminAiAddStatus
    data object Loading : AdminAiAddStatus
    data class Success(val message: String) : AdminAiAddStatus
    data class Error(val message: String) : AdminAiAddStatus
}
