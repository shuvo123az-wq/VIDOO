package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.FeedPostEntity
import com.example.domain.model.FeedCategory
import com.example.domain.model.MainNavTab
import com.example.domain.model.SecondaryDestination
import com.example.ui.components.VerifiedCreatorBadge
import com.example.ui.components.VidooBrandHeader
import com.example.ui.components.VidooMediaThumbnail
import com.example.ui.components.VidooSplashScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ArchitectureSpecsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommentsOverlaySheet
import com.example.ui.screens.CreateContentSheet
import com.example.ui.screens.CreatorStudioScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.HomeFeedScreen
import com.example.ui.screens.LongVideoPlayerOverlay
import com.example.ui.screens.MessagingScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileAndSocialScreen
import com.example.ui.screens.SafetyReportDialog
import com.example.ui.screens.SettingsAndLegalScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.VidooFeedPostCard
import com.example.ui.screens.VidooRewardsScreen
import com.example.ui.theme.VidooCoinGold
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.theme.VidooTheme
import com.example.ui.viewmodel.VidooViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vidooViewModel: VidooViewModel = viewModel()
            val isDarkMode by vidooViewModel.isDarkMode.collectAsStateWithLifecycle()

            VidooTheme(darkTheme = isDarkMode) {
                VidooAppRoot(viewModel = vidooViewModel)
            }
        }
    }
}

@Composable
fun VidooAppRoot(viewModel: VidooViewModel) {
    val showSplash by viewModel.showSplash.collectAsStateWithLifecycle()
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val authStatusMessage by viewModel.authStatusMessage.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val secondaryDest by viewModel.secondaryDestination.collectAsStateWithLifecycle()
    val showCreateSheet by viewModel.showCreateSheet.collectAsStateWithLifecycle()
    val initialCreateType by viewModel.initialCreateType.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val otherUsers by viewModel.otherUsers.collectAsStateWithLifecycle()
    val feedPosts by viewModel.feedPosts.collectAsStateWithLifecycle()
    val shortVideos by viewModel.shortVideos.collectAsStateWithLifecycle()
    val longVideos by viewModel.longVideos.collectAsStateWithLifecycle()
    val savedPosts by viewModel.savedPosts.collectAsStateWithLifecycle()
    val draftPosts by viewModel.draftPosts.collectAsStateWithLifecycle()
    val selectedFeedCategory by viewModel.selectedFeedCategory.collectAsStateWithLifecycle()

    val activeLongVideo by viewModel.activeLongVideoPost.collectAsStateWithLifecycle()
    val activeCommentPost by viewModel.activeCommentPost.collectAsStateWithLifecycle()
    val activePostComments by viewModel.activePostComments.collectAsStateWithLifecycle()
    val commentSortOrder by viewModel.commentSortOrder.collectAsStateWithLifecycle()
    val activeReportTarget by viewModel.activeReportTarget.collectAsStateWithLifecycle()

    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeConversationMessages.collectAsStateWithLifecycle()

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val notificationPrefs by viewModel.notificationPrefs.collectAsStateWithLifecycle()
    val rewardTransactions by viewModel.rewardTransactions.collectAsStateWithLifecycle()
    val dailyChallenges by viewModel.dailyChallenges.collectAsStateWithLifecycle()
    val rewardedAdFlowState by viewModel.rewardedAdFlowState.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()

    val featureFlags by viewModel.featureFlags.collectAsStateWithLifecycle()
    val remoteAdConfig by viewModel.remoteAdConfig.collectAsStateWithLifecycle()
    val creatorEligibility by viewModel.creatorEligibilityConfig.collectAsStateWithLifecycle()
    val adminAppSettings by viewModel.adminAppSettings.collectAsStateWithLifecycle()
    val creatorAnalytics by viewModel.creatorAnalytics.collectAsStateWithLifecycle()

    val uploadState by viewModel.uploadLifecycleState.collectAsStateWithLifecycle()
    val uploadStep by viewModel.currentUploadStep.collectAsStateWithLifecycle()
    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val toastBannerMessage by viewModel.toastBannerMessage.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val hasBackTarget = activeReportTarget != null ||
        activeCommentPost != null ||
        activeLongVideo != null ||
        activeConversation != null ||
        showCreateSheet ||
        secondaryDest != SecondaryDestination.NONE ||
        selectedTab != MainNavTab.HOME

    BackHandler(enabled = hasBackTarget) {
        viewModel.navigateBack()
    }

    if (showSplash) {
        VidooSplashScreen()
        return
    }

    if (!isAuthenticated) {
        AuthScreen(
            viewModel = viewModel,
            statusMessage = authStatusMessage
        )
        return
    }

    val unreadNotificationsCount = notifications.count { !it.isRead }
    val unreadMessagesCount = conversations.sumOf { it.unreadCount }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            VidooSideMenuSheet(
                userName = currentUser?.name ?: "Alex Rivera",
                username = currentUser?.username ?: "alexrivera",
                avatarRes = currentUser?.avatarResName ?: "img_avatar_creator_1790684817836",
                customAvatarUri = currentUser?.customAvatarUri,
                level = currentUser?.level ?: 5,
                xp = currentUser?.currentXp ?: 75,
                coins = currentUser?.coinBalance ?: 540,
                onSelectSideItem = { action ->
                    scope.launch { drawerState.close() }
                    action()
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (secondaryDest == SecondaryDestination.NONE &&
                    activeLongVideo == null &&
                    !showCreateSheet
                ) {
                    VidooBottomNavigationBar(
                        selectedTab = selectedTab,
                        unreadNotificationsCount = unreadNotificationsCount,
                        enableShorts = featureFlags.enableShorts,
                        onSelectTab = { viewModel.selectMainTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Content or Secondary Destination
                when (secondaryDest) {
                    SecondaryDestination.SEARCH -> {
                        GlobalSearchScreen(
                            viewModel = viewModel,
                            query = searchQuery,
                            recentSearches = recentSearches,
                            trendingSearches = viewModel.trendingSearches,
                            users = otherUsers,
                            posts = feedPosts,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.MESSAGES -> {
                        MessagingScreen(
                            viewModel = viewModel,
                            conversations = conversations,
                            activeConversation = activeConversation,
                            messages = activeMessages,
                            currentUserId = viewModel.repository.currentUserId,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.LONG_VIDEOS -> {
                        SavedOrLongVideosCatalogScreen(
                            title = "Long Videos (16:9 HD/4K)",
                            subtitle = "Scalable Cloud Storage streaming • Playback speed & Resume support",
                            posts = longVideos,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.SAVED_POSTS -> {
                        SavedOrLongVideosCatalogScreen(
                            title = "Saved Content & Drafts",
                            subtitle = "Bookmarked posts, Shorts, Long Videos & ${draftPosts.size} saved drafts",
                            posts = savedPosts + draftPosts,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.REWARDS,
                    SecondaryDestination.DAILY_CHALLENGE,
                    SecondaryDestination.INVITE_FRIENDS -> {
                        VidooRewardsScreen(
                            viewModel = viewModel,
                            user = currentUser,
                            challenges = dailyChallenges,
                            transactions = rewardTransactions,
                            adFlowState = rewardedAdFlowState,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.CREATOR_STUDIO -> {
                        CreatorStudioScreen(
                            analytics = creatorAnalytics,
                            eligibilityConfig = creatorEligibility,
                            user = currentUser,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            viewModel = viewModel,
                            currentTierName = currentUser?.subscriptionTier ?: "CREATOR",
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.ADMIN_PANEL -> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            users = otherUsers,
                            posts = feedPosts,
                            reports = reports,
                            featureFlags = featureFlags,
                            adConfig = remoteAdConfig,
                            appSettings = adminAppSettings,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.SETTINGS,
                    SecondaryDestination.LEGAL_CENTER,
                    SecondaryDestination.HELP_SUPPORT -> {
                        SettingsAndLegalScreen(
                            viewModel = viewModel,
                            user = currentUser,
                            isDarkMode = isDarkMode,
                            draftsCount = draftPosts.size,
                            savedCount = savedPosts.size,
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.NONE) }
                        )
                    }
                    SecondaryDestination.ARCHITECTURE_SPECS -> {
                        ArchitectureSpecsScreen(
                            onBack = { viewModel.navigateToSecondary(SecondaryDestination.SETTINGS) }
                        )
                    }
                    SecondaryDestination.NONE -> {
                        when (selectedTab) {
                            MainNavTab.HOME -> {
                                HomeFeedScreen(
                                    viewModel = viewModel,
                                    currentUser = currentUser,
                                    posts = feedPosts,
                                    longVideos = longVideos,
                                    selectedCategory = selectedFeedCategory,
                                    unreadNotificationsCount = unreadNotificationsCount,
                                    unreadMessagesCount = unreadMessagesCount,
                                    featureFlags = featureFlags,
                                    adConfig = remoteAdConfig,
                                    systemAnnouncement = adminAppSettings.systemAnnouncement,
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }
                            MainNavTab.SHORTS -> {
                                ShortsScreen(
                                    viewModel = viewModel,
                                    shortsList = shortVideos,
                                    maxShortDurationSeconds = adminAppSettings.maxShortVideoDurationSeconds
                                )
                            }
                            MainNavTab.NOTIFICATIONS -> {
                                NotificationsScreen(
                                    viewModel = viewModel,
                                    notifications = notifications,
                                    prefs = notificationPrefs
                                )
                            }
                            MainNavTab.PROFILE -> {
                                ProfileAndSocialScreen(
                                    viewModel = viewModel,
                                    user = currentUser,
                                    allPosts = feedPosts,
                                    savedPosts = savedPosts,
                                    otherUsers = otherUsers,
                                    friendRequests = friendRequests
                                )
                            }
                            MainNavTab.CREATE -> {
                                // Handled via Create Sheet
                            }
                        }
                    }
                }

                // Create Post / Upload Video Sheet
                if (showCreateSheet) {
                    CreateContentSheet(
                        viewModel = viewModel,
                        initialCreateType = initialCreateType,
                        uploadState = uploadState,
                        currentStep = uploadStep,
                        uploadProgress = uploadProgress,
                        maxShortDurationSec = adminAppSettings.maxShortVideoDurationSeconds,
                        onDismiss = { viewModel.closeCreateComposer() }
                    )
                }

                // Full-screen Long Video Player Overlay
                if (activeLongVideo != null) {
                    LongVideoPlayerOverlay(
                        post = activeLongVideo!!,
                        relatedVideos = longVideos,
                        onClose = { savedSec -> viewModel.closeLongVideoPlayer(savedSec) },
                        onLike = { viewModel.toggleLikePost(activeLongVideo!!) },
                        onComment = { viewModel.openCommentsForPost(activeLongVideo!!) },
                        onShare = { viewModel.sharePost(activeLongVideo!!) },
                        onSave = { viewModel.toggleSavePost(activeLongVideo!!) },
                        onFollow = { viewModel.toggleFollowCreator(activeLongVideo!!.authorId) },
                        onSelectRelated = { viewModel.openLongVideoPlayer(it) }
                    )
                }

                // Comments & Replies Overlay Sheet
                if (activeCommentPost != null) {
                    CommentsOverlaySheet(
                        viewModel = viewModel,
                        post = activeCommentPost!!,
                        comments = activePostComments,
                        sortOrder = commentSortOrder,
                        currentUserId = viewModel.repository.currentUserId,
                        onClose = { viewModel.closeCommentsSheet() }
                    )
                }

                // Safety & Moderation Report Dialog
                if (activeReportTarget != null) {
                    SafetyReportDialog(
                        target = activeReportTarget!!,
                        onDismiss = { viewModel.closeReportDialog() },
                        onSubmit = { cat, notes -> viewModel.submitSafetyReport(cat, notes) }
                    )
                }

                // Floating Toast Banner
                AnimatedVisibility(
                    visible = toastBannerMessage != null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(50),
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .border(1.dp, VidooCyan, RoundedCornerShape(50))
                            .clickable { viewModel.dismissBanner() }
                    ) {
                        Text(
                            text = toastBannerMessage ?: "",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = VidooCyan,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section 28: Bottom Navigation (HOME, SHORTS, CREATE, NOTIFICATIONS, PROFILE)
 */
@Composable
private fun VidooBottomNavigationBar(
    selectedTab: MainNavTab,
    unreadNotificationsCount: Int,
    enableShorts: Boolean,
    onSelectTab: (MainNavTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_navigation_bar")
    ) {
        NavigationBarItem(
            selected = selectedTab == MainNavTab.HOME,
            onClick = { onSelectTab(MainNavTab.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VidooCyan,
                selectedTextColor = VidooCyan,
                indicatorColor = VidooCyan.copy(alpha = 0.16f)
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        if (enableShorts) {
            NavigationBarItem(
                selected = selectedTab == MainNavTab.SHORTS,
                onClick = { onSelectTab(MainNavTab.SHORTS) },
                icon = { Icon(Icons.Filled.SmartDisplay, contentDescription = "Shorts") },
                label = { Text("Shorts") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VidooCyan,
                    selectedTextColor = VidooCyan,
                    indicatorColor = VidooCyan.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_tab_shorts")
            )
        }

        // Center CREATE button
        NavigationBarItem(
            selected = false,
            onClick = { onSelectTab(MainNavTab.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(colors = listOf(VidooCyan, VidooPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Content",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            label = { Text("Create") },
            modifier = Modifier.testTag("nav_tab_create")
        )

        NavigationBarItem(
            selected = selectedTab == MainNavTab.NOTIFICATIONS,
            onClick = { onSelectTab(MainNavTab.NOTIFICATIONS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadNotificationsCount > 0) {
                            Badge(containerColor = VidooPurple, contentColor = Color.White) {
                                Text(unreadNotificationsCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                }
            },
            label = { Text("Notifications") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VidooCyan,
                selectedTextColor = VidooCyan,
                indicatorColor = VidooCyan.copy(alpha = 0.16f)
            ),
            modifier = Modifier.testTag("nav_tab_notifications")
        )

        NavigationBarItem(
            selected = selectedTab == MainNavTab.PROFILE,
            onClick = { onSelectTab(MainNavTab.PROFILE) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VidooCyan,
                selectedTextColor = VidooCyan,
                indicatorColor = VidooCyan.copy(alpha = 0.16f)
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}

/**
 * Section 29: Vidoo Side Menu Drawer
 */
@Composable
private fun VidooSideMenuSheet(
    userName: String,
    username: String,
    avatarRes: String,
    customAvatarUri: String?,
    level: Int,
    xp: Int,
    coins: Int,
    onSelectSideItem: ((() -> Unit)) -> Unit
) {
    val vidooViewModel: VidooViewModel = viewModel()

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(305.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            VidooBrandHeader(showTagline = true)
            Spacer(modifier = Modifier.height(16.dp))

            // User Card
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, VidooCyan, CircleShape)
                    ) {
                        VidooMediaThumbnail(
                            mediaResName = avatarRes,
                            customUri = customAvatarUri,
                            contentDescription = userName,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedCreatorBadge(size = 14.dp)
                        }
                        Text(
                            text = "@$username • Level $level ($xp XP)",
                            style = MaterialTheme.typography.labelSmall,
                            color = VidooCyan
                        )
                        Text(
                            text = "$coins VIDOO COINS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = VidooCoinGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            SideMenuDrawerEntry("Profile", Icons.Filled.Person) {
                onSelectSideItem { vidooViewModel.selectMainTab(MainNavTab.PROFILE) }
            }
            SideMenuDrawerEntry("Daily Challenge", Icons.Filled.EmojiEvents) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.DAILY_CHALLENGE) }
            }
            SideMenuDrawerEntry("Trending", Icons.AutoMirrored.Filled.TrendingUp) {
                onSelectSideItem {
                    vidooViewModel.selectMainTab(MainNavTab.HOME)
                    vidooViewModel.selectFeedCategory(FeedCategory.TRENDING)
                }
            }
            SideMenuDrawerEntry("Saved Posts", Icons.Filled.Bookmark) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.SAVED_POSTS) }
            }
            SideMenuDrawerEntry("Creator Studio", Icons.Filled.VideoSettings) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.CREATOR_STUDIO) }
            }
            SideMenuDrawerEntry("Subscription", Icons.Filled.WorkspacePremium) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.SUBSCRIPTIONS) }
            }
            SideMenuDrawerEntry("Rewards & Coins", Icons.Filled.Stars) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.REWARDS) }
            }
            SideMenuDrawerEntry("Invite Friends", Icons.Filled.PersonAdd) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.INVITE_FRIENDS) }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SideMenuDrawerEntry("Admin Panel", Icons.Filled.AdminPanelSettings) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.ADMIN_PANEL) }
            }
            SideMenuDrawerEntry("Settings", Icons.Filled.Settings) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.SETTINGS) }
            }
            SideMenuDrawerEntry("Help & Support", Icons.AutoMirrored.Filled.HelpOutline) {
                onSelectSideItem { vidooViewModel.navigateToSecondary(SecondaryDestination.HELP_SUPPORT) }
            }
        }
    }
}

@Composable
private fun SideMenuDrawerEntry(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label, tint = VidooCyan) },
        modifier = Modifier
            .padding(vertical = 2.dp)
            .testTag("drawer_item_${label.lowercase().replace(" ", "_")}")
    )
}

@Composable
private fun SavedOrLongVideosCatalogScreen(
    title: String,
    subtitle: String,
    posts: List<FeedPostEntity>,
    viewModel: VidooViewModel,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts, key = { "cat_${it.id}" }) { post ->
                    VidooFeedPostCard(
                        post = post,
                        onLike = { viewModel.toggleLikePost(post) },
                        onComment = { viewModel.openCommentsForPost(post) },
                        onShare = { viewModel.sharePost(post) },
                        onSave = { viewModel.toggleSavePost(post) },
                        onFollowToggle = { viewModel.toggleFollowCreator(post.authorId) },
                        onReport = { viewModel.openReportDialog("VIDEO", post.id, post.title) },
                        onNotInterested = { viewModel.markPostNotInterested(post) },
                        onOpenFullVideo = { viewModel.openLongVideoPlayer(post) }
                    )
                }
            }
        }
    }
}
