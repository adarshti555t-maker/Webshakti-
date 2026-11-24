package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ai.configureAppCheck
import com.example.model.AppLanguage
import com.example.model.AppPage
import com.example.model.AppRoutes
import com.example.ui.components.AgencyBottomNavBar
import com.example.ui.components.AgencyDrawerContent
import com.example.ui.components.AgencyNavRail
import com.example.ui.components.GlobalCommandSearchDialog
import com.example.ui.components.WebShaktiTopBar
import com.example.ui.components.WhatsAppConfigDialog
import com.example.ui.components.launchWhatsApp
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiMasterScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SavedSitesWorkspaceScreen
import com.example.ui.screens.TermsScreen
import com.example.ui.screens.WebCreatorsTeamScreen
import com.example.ui.screens.WebsiteGeneratorScreen
import com.example.ui.screens.WebsiteTypesScreen
import com.example.ui.theme.NavyAbyss
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WebShaktiTheme
import com.example.ui.theme.WhatsAppGreen
import com.example.viewmodel.WebShaktiViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before any UI or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                androidx.compose.ui.graphics.Color.White.toArgb(),
                androidx.compose.ui.graphics.Color.White.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                androidx.compose.ui.graphics.Color.White.toArgb(),
                androidx.compose.ui.graphics.Color.White.toArgb()
            )
        )
        setContent {
            WebShaktiTheme {
                WebShaktiAgencyApp()
            }
        }
    }
}

@Composable
fun WebShaktiAgencyApp(
    viewModel: WebShaktiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppRoutes.HOME
    val currentPage = AppPage.entries.firstOrNull { it.route == currentRoute } ?: AppPage.HOME

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showWhatsAppConfig by remember { mutableStateOf(false) }
    var showCommandSearch by remember { mutableStateOf(false) }

    fun navigateTo(page: AppPage) {
        navController.navigate(page.route) {
            popUpTo(AppRoutes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun openWhatsAppDefault() {
        val greeting = if (uiState.language == AppLanguage.HI) {
            "नमस्ते WEBSHAKTI AI वेब टीम, मैं अपनी वेबसाइट बनवाने के बारे में बात करना चाहता/चाहती हूँ।"
        } else {
            "Hello WEBSHAKTI AI Web Creators team, I would like to build a website."
        }
        launchWhatsApp(context, uiState.whatsAppNumber, greeting)
    }

    fun showMessage(msg: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AgencyDrawerContent(
                currentPage = currentPage,
                language = uiState.language,
                whatsAppNumber = uiState.whatsAppNumber,
                onSelectPage = { page ->
                    scope.launch { drawerState.close() }
                    navigateTo(page)
                },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenWhatsAppConfig = {
                    scope.launch { drawerState.close() }
                    showWhatsAppConfig = true
                }
            )
        }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideLayout = maxWidth >= 720.dp

            Scaffold(
                containerColor = NavyAbyss,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    WebShaktiTopBar(
                        currentPage = currentPage,
                        language = uiState.language,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onOpenSearch = { showCommandSearch = true },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onNavigateHome = { navigateTo(AppPage.HOME) },
                        onWhatsAppClick = { openWhatsAppDefault() }
                    )
                },
                bottomBar = {
                    if (!isWideLayout) {
                        AgencyBottomNavBar(
                            currentPage = currentPage,
                            language = uiState.language,
                            onSelectPage = { navigateTo(it) }
                        )
                    }
                },
                floatingActionButton = {
                    if (currentPage != AppPage.CONTACT) {
                        FloatingActionButton(
                            onClick = { openWhatsAppDefault() },
                            containerColor = WhatsAppGreen,
                            contentColor = TextPrimary,
                            modifier = Modifier.testTag("whatsapp_fab")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Contact Web Team on WhatsApp"
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWideLayout) {
                        AgencyNavRail(
                            currentPage = currentPage,
                            language = uiState.language,
                            onSelectPage = { navigateTo(it) }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        NavHost(
                            navController = navController,
                            startDestination = AppRoutes.HOME
                        ) {
                            composable(AppRoutes.HOME) {
                                HomeScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    adminTools = uiState.adminCustomTools,
                                    adminAnnouncements = uiState.adminAnnouncements,
                                    onNavigate = { navigateTo(it) },
                                    onOpenSearch = { showCommandSearch = true },
                                    onOpenWebsiteTool = { toolType ->
                                        viewModel.selectWebsiteTool(toolType)
                                        navigateTo(AppPage.GENERATOR)
                                    },
                                    onOpenAdminTool = { customTool ->
                                        viewModel.openAdminCustomTool(customTool)
                                        navigateTo(AppPage.GENERATOR)
                                    },
                                    onAskAiMaster = { presetQuestion ->
                                        if (!presetQuestion.isNullOrBlank()) {
                                            viewModel.askAiMaster(presetQuestion)
                                        }
                                        navigateTo(AppPage.AI_MASTER)
                                    },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.AI_MASTER) {
                                AiMasterScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    messages = uiState.aiMasterMessages,
                                    status = uiState.aiMasterStatus,
                                    onAskQuestion = { viewModel.askAiMaster(it) },
                                    onClearChat = { viewModel.clearAiMasterChat() },
                                    onDismissError = { viewModel.dismissAiMasterError() },
                                    onShowMessage = { showMessage(it) },
                                    onWhatsAppClick = { openWhatsAppDefault() }
                                )
                            }

                            composable(AppRoutes.GENERATOR) {
                                WebsiteGeneratorScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    webhookUrl = uiState.webhookUrl,
                                    selectedTool = uiState.selectedTool,
                                    generatedSite = uiState.lastGeneratedSite,
                                    onSelectTool = { viewModel.selectWebsiteTool(it) },
                                    onGenerateWebsite = { type, brand, niche, cta, accent ->
                                        viewModel.generateWebsitePackage(type, brand, niche, cta, accent)
                                    },
                                    onImportHtmlWebsite = { fileName, content ->
                                        viewModel.importUploadedWebsite(fileName, content)
                                    },
                                    onSaveWebsite = {
                                        viewModel.saveCurrentWebsiteToWorkspace()
                                    },
                                    onSaveWebhookUrl = { viewModel.updateWebhookUrl(it) },
                                    onDeploySiteToEndpoint = { siteName, html, url, cb ->
                                        viewModel.deployWebsiteToConfiguredEndpoint(siteName, html, url, cb)
                                    },
                                    onShowMessage = { showMessage(it) },
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.TEMPLATES) {
                                WebsiteTypesScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    adminTools = uiState.adminCustomTools,
                                    onOpenWebsiteTool = { toolType ->
                                        viewModel.selectWebsiteTool(toolType)
                                        navigateTo(AppPage.GENERATOR)
                                    },
                                    onOpenAdminTool = { customTool ->
                                        viewModel.openAdminCustomTool(customTool)
                                        navigateTo(AppPage.GENERATOR)
                                    },
                                    onRequestCustomWebsite = { toolType ->
                                        viewModel.prepareCustomWebsiteRequest(websiteType = toolType)
                                        navigateTo(AppPage.CONTACT)
                                    },
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.TEAM) {
                                WebCreatorsTeamScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.WORKSPACE) {
                                SavedSitesWorkspaceScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    webhookUrl = uiState.webhookUrl,
                                    savedInquiries = uiState.savedInquiries,
                                    savedBlueprints = uiState.savedBlueprints,
                                    onSaveWebhookUrl = { viewModel.updateWebhookUrl(it) },
                                    onReopenWebsite = { bp ->
                                        viewModel.openSavedWebsite(bp)
                                        navigateTo(AppPage.GENERATOR)
                                    },
                                    onImportHtmlWebsite = { fileName, content ->
                                        viewModel.importUploadedWebsite(fileName, content)
                                    },
                                    onDeploySiteToEndpoint = { siteName, html, url, cb ->
                                        viewModel.deployWebsiteToConfiguredEndpoint(siteName, html, url, cb)
                                    },
                                    onDeleteInquiry = { viewModel.deleteInquiry(it) },
                                    onDeleteBlueprint = { viewModel.deleteBlueprint(it) },
                                    onShowMessage = { showMessage(it) },
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.CONTACT) {
                                ContactScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    initialTab = uiState.contactInitialTab,
                                    prefilledWebsiteType = uiState.prefilledWebsiteType,
                                    prefilledRequirements = uiState.prefilledRequirements,
                                    onSelectTab = { viewModel.setContactTab(it) },
                                    onSubmitContact = { name, email, subject, msg ->
                                        viewModel.submitContactInquiry(name, email, subject, msg)
                                    },
                                    onSubmitWebsiteRequest = { name, email, company, siteType, timeline, req ->
                                        viewModel.submitWebsiteRequest(name, email, company, siteType, timeline, req)
                                    },
                                    onShowMessage = { showMessage(it) },
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.ADMIN) {
                                AdminPanelScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    isAdminUnlocked = uiState.isAdminUnlocked,
                                    unlockError = uiState.adminUnlockError,
                                    adminTools = uiState.adminCustomTools,
                                    adminAnnouncements = uiState.adminAnnouncements,
                                    aiAddStatus = uiState.adminAiAddStatus,
                                    onUnlock = { viewModel.unlockAdminPanel(it) },
                                    onLock = { viewModel.lockAdminPanel() },
                                    onClearUnlockError = { viewModel.clearAdminUnlockError() },
                                    onAddToolViaAi = { viewModel.addAdminToolViaAi(it) },
                                    onAddAnnouncementViaAi = { viewModel.addAdminAnnouncementViaAi(it) },
                                    onAddToolManually = { title, sub, sec, type ->
                                        viewModel.addAdminToolManually(title, sub, sec, type)
                                    },
                                    onAddAnnouncementManually = { title, msg, badge ->
                                        viewModel.addAdminAnnouncementManually(title, msg, badge)
                                    },
                                    onDeleteTool = { viewModel.deleteAdminCustomTool(it) },
                                    onDeleteAnnouncement = { viewModel.deleteAdminAnnouncement(it) },
                                    onOpenAdminTool = { customTool ->
                                        viewModel.openAdminCustomTool(customTool)
                                        navigateTo(AppPage.GENERATOR)
                                    },
                                    onClearAiAddStatus = { viewModel.clearAdminAiAddStatus() },
                                    onShowMessage = { showMessage(it) },
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.PRIVACY) {
                                PrivacyPolicyScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }

                            composable(AppRoutes.TERMS) {
                                TermsScreen(
                                    language = uiState.language,
                                    whatsAppNumber = uiState.whatsAppNumber,
                                    onNavigate = { navigateTo(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onWhatsAppClick = { openWhatsAppDefault() },
                                    onConfigureWhatsApp = { showWhatsAppConfig = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCommandSearch) {
        GlobalCommandSearchDialog(
            language = uiState.language,
            onDismiss = { showCommandSearch = false },
            onNavigatePage = { navigateTo(it) },
            onOpenWebsiteTool = { tool ->
                viewModel.selectWebsiteTool(tool)
                navigateTo(AppPage.GENERATOR)
            }
        )
    }

    if (showWhatsAppConfig) {
        WhatsAppConfigDialog(
            currentNumber = uiState.whatsAppNumber,
            language = uiState.language,
            onDismiss = { showWhatsAppConfig = false },
            onSave = { newNumber ->
                viewModel.updateWhatsAppNumber(newNumber)
                showMessage(
                    if (uiState.language == AppLanguage.HI) {
                        "WhatsApp नंबर अपडेट किया गया: $newNumber"
                    } else {
                        "WhatsApp number updated: $newNumber"
                    }
                )
            }
        )
    }
}
