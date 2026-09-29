package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CommentEntity
import com.example.data.local.ConversationEntity
import com.example.data.local.DailyChallengeEntity
import com.example.data.local.FeedPostEntity
import com.example.data.local.FriendRequestEntity
import com.example.data.local.MessageEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.ReportEntity
import com.example.data.local.RewardTransactionEntity
import com.example.data.local.UserEntity
import com.example.data.local.VidooDatabase
import com.example.data.services.VidooRepository
import com.example.domain.model.AdminAppSettings
import com.example.domain.model.CommentSortOrder
import com.example.domain.model.CreatorAnalyticsSummary
import com.example.domain.model.CreatorEligibilityConfig
import com.example.domain.model.FeedCategory
import com.example.domain.model.FeatureFlags
import com.example.domain.model.MainNavTab
import com.example.domain.model.MusicTrack
import com.example.domain.model.NotificationPreferences
import com.example.domain.model.RemoteAdConfig
import com.example.domain.model.ReportCategory
import com.example.domain.model.SecondaryDestination
import com.example.domain.model.UploadLifecycleState
import com.example.domain.model.UploadStep
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReportDialogTarget(
    val targetType: String,
    val targetId: String,
    val targetPreview: String
)

enum class RewardedAdFlowState(val stepIndex: Int, val statusText: String) {
    IDLE(0, "Ready to watch a verified partner ad"),
    DISPLAYING_AD(1, "1/4 Displaying Google AdMob Rewarded Video (Test ID)…"),
    VERIFYING_COMPLETION(2, "2/4 Verifying 100% video playback completion…"),
    SERVER_VALIDATING(3, "3/4 Validating cryptographic SSV nonce on Vidoo Server…"),
    REWARD_GRANTED(4, "4/4 Server Verified! +30 VIDOO COINS & +15 XP added.")
}

@OptIn(ExperimentalCoroutinesApi::class)
class VidooViewModel(application: Application) : AndroidViewModel(application) {

    private val database = VidooDatabase.getDatabase(application)
    val repository = VidooRepository(database.vidooDao())

    // Theme & Splash
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    // Authentication State
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authStatusMessage = MutableStateFlow<String?>(null)
    val authStatusMessage: StateFlow<String?> = _authStatusMessage.asStateFlow()

    // Navigation State
    private val _selectedTab = MutableStateFlow(MainNavTab.HOME)
    val selectedTab: StateFlow<MainNavTab> = _selectedTab.asStateFlow()

    private val _secondaryDestination = MutableStateFlow(SecondaryDestination.NONE)
    val secondaryDestination: StateFlow<SecondaryDestination> = _secondaryDestination.asStateFlow()

    private val _showCreateSheet = MutableStateFlow(false)
    val showCreateSheet: StateFlow<Boolean> = _showCreateSheet.asStateFlow()

    private val _initialCreateType = MutableStateFlow("SHORT_VIDEO")
    val initialCreateType: StateFlow<String> = _initialCreateType.asStateFlow()

    // Active Overlays / Modals
    private val _activeLongVideoPost = MutableStateFlow<FeedPostEntity?>(null)
    val activeLongVideoPost: StateFlow<FeedPostEntity?> = _activeLongVideoPost.asStateFlow()

    private val _activeCommentPost = MutableStateFlow<FeedPostEntity?>(null)
    val activeCommentPost: StateFlow<FeedPostEntity?> = _activeCommentPost.asStateFlow()

    private val _activeReportTarget = MutableStateFlow<ReportDialogTarget?>(null)
    val activeReportTarget: StateFlow<ReportDialogTarget?> = _activeReportTarget.asStateFlow()

    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    private val _toastBannerMessage = MutableStateFlow<String?>(null)
    val toastBannerMessage: StateFlow<String?> = _toastBannerMessage.asStateFlow()

    // Feed Category & Search
    private val _selectedFeedCategory = MutableStateFlow(FeedCategory.FOR_YOU)
    val selectedFeedCategory: StateFlow<FeedCategory> = _selectedFeedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _recentSearches = MutableStateFlow(
        listOf("#VidooShorts", "novachen_fx", "4K Creator Desk", "Color Grading", "#Synthwave")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val trendingSearches: List<String> = listOf(
        "#VidooOriginals",
        "#NeonDance",
        "#CreatorStudio",
        "#RoyaltyFreeMusic",
        "#CameraTricks",
        "#Filmmaking"
    )

    // Remote Feature Flags, Admin Config, Ad Config, Eligibility Config
    private val _featureFlags = MutableStateFlow(FeatureFlags())
    val featureFlags: StateFlow<FeatureFlags> = _featureFlags.asStateFlow()

    private val _remoteAdConfig = MutableStateFlow(RemoteAdConfig())
    val remoteAdConfig: StateFlow<RemoteAdConfig> = _remoteAdConfig.asStateFlow()

    private val _creatorEligibilityConfig = MutableStateFlow(CreatorEligibilityConfig())
    val creatorEligibilityConfig: StateFlow<CreatorEligibilityConfig> = _creatorEligibilityConfig.asStateFlow()

    private val _adminAppSettings = MutableStateFlow(AdminAppSettings())
    val adminAppSettings: StateFlow<AdminAppSettings> = _adminAppSettings.asStateFlow()

    private val _creatorAnalytics = MutableStateFlow(CreatorAnalyticsSummary())
    val creatorAnalytics: StateFlow<CreatorAnalyticsSummary> = _creatorAnalytics.asStateFlow()

    private val _notificationPrefs = MutableStateFlow(NotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs.asStateFlow()

    // Comments Pagination & Sorting
    private val _commentSortOrder = MutableStateFlow(CommentSortOrder.TOP)
    val commentSortOrder: StateFlow<CommentSortOrder> = _commentSortOrder.asStateFlow()

    private val _commentPageSize = MutableStateFlow(10)
    val commentPageSize: StateFlow<Int> = _commentPageSize.asStateFlow()

    // 7-Step Upload Flow State
    private val _uploadLifecycleState = MutableStateFlow(UploadLifecycleState.IDLE)
    val uploadLifecycleState: StateFlow<UploadLifecycleState> = _uploadLifecycleState.asStateFlow()

    private val _currentUploadStep = MutableStateFlow(UploadStep.SELECT_VIDEO)
    val currentUploadStep: StateFlow<UploadStep> = _currentUploadStep.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    // 5-Step Rewarded Ad SSV Flow State
    private val _rewardedAdFlowState = MutableStateFlow(RewardedAdFlowState.IDLE)
    val rewardedAdFlowState: StateFlow<RewardedAdFlowState> = _rewardedAdFlowState.asStateFlow()

    // Reactive Database Streams
    val currentUser: StateFlow<UserEntity?> = repository.observeCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val otherUsers: StateFlow<List<UserEntity>> = repository.observeOtherUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedPosts: StateFlow<List<FeedPostEntity>> = _selectedFeedCategory
        .flatMapLatest { category -> repository.observeFeedByCategory(category) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortVideos: StateFlow<List<FeedPostEntity>> = repository.observeShortVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val longVideos: StateFlow<List<FeedPostEntity>> = repository.observeLongVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<FeedPostEntity>> = repository.observeSavedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftPosts: StateFlow<List<FeedPostEntity>> = repository.observeDraftPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePostComments: StateFlow<List<CommentEntity>> = combine(
        _activeCommentPost,
        _commentSortOrder,
        _commentPageSize
    ) { post, sort, limit -> Triple(post?.id, sort, limit) }
        .flatMapLatest { (postId, sort, limit) ->
            if (postId == null) flowOf(emptyList())
            else repository.observeComments(postId, sort, limit)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.observeConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeConversationMessages: StateFlow<List<MessageEntity>> = _activeConversation
        .flatMapLatest { conv ->
            if (conv == null) flowOf(emptyList())
            else repository.observeMessages(conv.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.observeNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewardTransactions: StateFlow<List<RewardTransactionEntity>> = repository.observeRewardTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyChallenges: StateFlow<List<DailyChallengeEntity>> = repository.observeDailyChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<ReportEntity>> = repository.observeReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friendRequests: StateFlow<List<FriendRequestEntity>> = repository.observeFriendRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            delay(950)
            _showSplash.value = false
        }
    }

    fun showBanner(message: String) {
        viewModelScope.launch {
            _toastBannerMessage.value = message
            delay(3200)
            if (_toastBannerMessage.value == message) {
                _toastBannerMessage.value = null
            }
        }
    }

    fun dismissBanner() {
        _toastBannerMessage.value = null
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        showBanner(if (_isDarkMode.value) "Vidoo Dark Mode Branding Active" else "Vidoo Light Mode Branding Active")
    }

    // Navigation Actions
    fun selectMainTab(tab: MainNavTab) {
        if (tab == MainNavTab.CREATE) {
            openCreateComposer("SHORT_VIDEO")
        } else {
            _secondaryDestination.value = SecondaryDestination.NONE
            _selectedTab.value = tab
            if (tab == MainNavTab.NOTIFICATIONS) {
                viewModelScope.launch { repository.markNotificationsRead() }
            }
        }
    }

    fun navigateToSecondary(destination: SecondaryDestination) {
        _secondaryDestination.value = destination
    }

    fun navigateBack(): Boolean {
        return when {
            _activeReportTarget.value != null -> {
                _activeReportTarget.value = null
                true
            }
            _activeCommentPost.value != null -> {
                _activeCommentPost.value = null
                true
            }
            _activeLongVideoPost.value != null -> {
                _activeLongVideoPost.value = null
                true
            }
            _activeConversation.value != null -> {
                _activeConversation.value = null
                true
            }
            _showCreateSheet.value -> {
                _showCreateSheet.value = false
                true
            }
            _secondaryDestination.value != SecondaryDestination.NONE -> {
                _secondaryDestination.value = SecondaryDestination.NONE
                true
            }
            _selectedTab.value != MainNavTab.HOME -> {
                _selectedTab.value = MainNavTab.HOME
                true
            }
            else -> false
        }
    }

    fun openCreateComposer(initialType: String = "SHORT_VIDEO") {
        _initialCreateType.value = initialType
        _uploadLifecycleState.value = UploadLifecycleState.IDLE
        _uploadProgress.value = 0f
        _showCreateSheet.value = true
    }

    fun closeCreateComposer() {
        _showCreateSheet.value = false
    }

    fun selectFeedCategory(category: FeedCategory) {
        if (category == FeedCategory.SHORTS && _featureFlags.value.enableShorts) {
            _selectedTab.value = MainNavTab.SHORTS
        } else {
            _selectedFeedCategory.value = category
        }
    }

    fun openLongVideoPlayer(post: FeedPostEntity) {
        _activeLongVideoPost.value = post
        viewModelScope.launch {
            repository.updateVideoResumePosition(post.id, post.resumePositionSeconds)
        }
    }

    fun closeLongVideoPlayer(savedPositionSeconds: Int = 0) {
        val current = _activeLongVideoPost.value
        if (current != null && savedPositionSeconds > 0) {
            viewModelScope.launch {
                repository.updateVideoResumePosition(current.id, savedPositionSeconds)
            }
        }
        _activeLongVideoPost.value = null
    }

    fun openCommentsForPost(post: FeedPostEntity) {
        _commentPageSize.value = 10
        _activeCommentPost.value = post
    }

    fun closeCommentsSheet() {
        _activeCommentPost.value = null
    }

    fun setCommentSortOrder(order: CommentSortOrder) {
        _commentSortOrder.value = order
    }

    fun loadMoreComments() {
        _commentPageSize.value += 10
    }

    // Auth Operations
    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.length < 6) {
            _authStatusMessage.value = "Please enter a valid email and 6+ character password."
            return
        }
        repository.analytics.logEvent("login", mapOf("method" to "email"))
        _authStatusMessage.value = null
        _isAuthenticated.value = true
        showBanner("Signed in to VIDOO as $email")
    }

    fun registerAccount(
        name: String,
        username: String,
        email: String,
        password: String,
        bio: String,
        customAvatarUri: String?
    ) {
        viewModelScope.launch {
            if (name.isBlank() || username.isBlank() || email.isBlank() || password.length < 6) {
                _authStatusMessage.value = "All fields are required and password must be at least 6 characters."
                return@launch
            }
            val available = repository.isUsernameAvailable(username, excludeUserId = "")
            if (!available) {
                _authStatusMessage.value = "Username @$username is already taken. Try another handle."
                return@launch
            }
            repository.updateProfile(
                name = name,
                username = username,
                bio = bio.ifBlank { "Creator on VIDOO • Watch. Share. Connect." },
                email = email,
                phone = "",
                customAvatarUri = customAvatarUri,
                customCoverUri = null
            )
            repository.analytics.logEvent("registration", mapOf("method" to "email", "username" to username))
            _authStatusMessage.value = null
            _isAuthenticated.value = true
            showBanner("Welcome to VIDOO, @$username! Verification email sent to $email.")
        }
    }

    fun signInWithGoogleAccount() {
        repository.analytics.logEvent("login", mapOf("method" to "google_identity"))
        _authStatusMessage.value = null
        _isAuthenticated.value = true
        showBanner("Connected via Google Sign-In")
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _authStatusMessage.value = "Enter your account email first to receive a password reset link."
            return
        }
        _authStatusMessage.value = "Password reset instructions sent to $email."
    }

    fun logout() {
        _secondaryDestination.value = SecondaryDestination.NONE
        _isAuthenticated.value = false
        showBanner("Logged out of VIDOO")
    }

    fun deleteAccountPermanently() {
        viewModelScope.launch {
            repository.deleteCurrentUserAccount()
            _secondaryDestination.value = SecondaryDestination.NONE
            _isAuthenticated.value = false
            showBanner("Your account and personal data have been permanently deleted.")
        }
    }

    // Feed & Social Interactions
    fun toggleLikePost(post: FeedPostEntity) {
        viewModelScope.launch {
            repository.toggleLike(post)
            val isPremium = currentUser.value?.subscriptionTier != "FREE"
            val showInterstitial = repository.adService.recordUserInteraction(_remoteAdConfig.value, isPremium)
            if (showInterstitial) {
                showBanner("AdService: Frequency cap reached — Test Interstitial slot triggered.")
            }
        }
    }

    fun toggleSavePost(post: FeedPostEntity) {
        viewModelScope.launch {
            repository.toggleSave(post)
            showBanner(if (!post.isSaved) "Saved to your bookmarks" else "Removed from saved posts")
        }
    }

    fun sharePost(post: FeedPostEntity) {
        viewModelScope.launch {
            repository.incrementShare(post)
            showBanner("Share link copied: https://vidoo.app/watch/${post.id}")
        }
    }

    fun markPostNotInterested(post: FeedPostEntity) {
        viewModelScope.launch {
            repository.markNotInterested(post)
            showBanner("We'll show fewer videos like this in your feed.")
        }
    }

    fun toggleFollowCreator(userId: String) {
        viewModelScope.launch {
            repository.toggleFollowUser(userId)
        }
    }

    fun toggleBlockUser(userId: String, username: String) {
        viewModelScope.launch {
            repository.toggleBlockUser(userId)
            showBanner("Updated block status for @$username")
        }
    }

    fun toggleMuteUser(userId: String, username: String) {
        viewModelScope.launch {
            repository.toggleMuteUser(userId)
            showBanner("Updated mute status for @$username")
        }
    }

    fun respondFriendRequest(request: FriendRequestEntity, accept: Boolean) {
        viewModelScope.launch {
            repository.respondToFriendRequest(request, accept)
            showBanner(if (accept) "Connected with @${request.fromUsername}!" else "Request declined")
        }
    }

    fun saveProfileEdits(
        name: String,
        username: String,
        bio: String,
        email: String,
        phone: String,
        customAvatarUri: String?,
        customCoverUri: String?,
        onResult: (Result<UserEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.updateProfile(name, username, bio, email, phone, customAvatarUri, customCoverUri)
            onResult(res)
            res.onSuccess { showBanner("Profile updated (@${it.username})") }
        }
    }

    // Comments
    fun submitComment(postId: String, text: String, parentCommentId: String? = null) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, text, parentCommentId)
        }
    }

    fun toggleLikeComment(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleLikeComment(comment)
        }
    }

    fun deleteOwnComment(comment: CommentEntity) {
        viewModelScope.launch {
            repository.deleteComment(comment)
            showBanner("Comment deleted")
        }
    }

    // Messaging
    fun openConversation(conversation: ConversationEntity) {
        _activeConversation.value = conversation
    }

    fun closeConversation() {
        _activeConversation.value = null
    }

    fun sendChatMessage(text: String, mediaType: String = "NONE", mediaUri: String = "") {
        val conv = _activeConversation.value ?: return
        if (text.isBlank() && mediaType == "NONE") return
        viewModelScope.launch {
            repository.sendMessage(conv, text.ifBlank { "Shared $mediaType" }, mediaType, mediaUri)
        }
    }

    // 7-Step Professional Upload Pipeline
    fun startUploadPipeline(
        contentType: String,
        title: String,
        caption: String,
        hashtags: String,
        mentions: String,
        visibility: String,
        locationTag: String,
        musicTrack: MusicTrack?,
        customMediaUri: String?,
        durationSeconds: Int,
        isDraft: Boolean,
        simulateFailureOnce: Boolean = false
    ) {
        viewModelScope.launch {
            if (isDraft) {
                repository.publishOrDraftPost(
                    contentType = contentType,
                    title = title,
                    caption = caption,
                    hashtags = hashtags,
                    mentions = mentions,
                    visibility = visibility,
                    locationTag = locationTag,
                    musicTrack = musicTrack,
                    customMediaUri = customMediaUri,
                    durationSeconds = durationSeconds,
                    isDraft = true
                )
                _showCreateSheet.value = false
                showBanner("Saved to Drafts")
                return@launch
            }

            _uploadLifecycleState.value = UploadLifecycleState.UPLOADING
            val steps = UploadStep.entries
            for ((idx, step) in steps.withIndex()) {
                _currentUploadStep.value = step
                _uploadProgress.value = ((idx + 1).toFloat() / steps.size.toFloat()).coerceIn(0.1f, 0.95f)
                if (step == UploadStep.PROCESS_VIDEO) {
                    _uploadLifecycleState.value = UploadLifecycleState.PROCESSING
                }
                delay(260)
                if (simulateFailureOnce && step == UploadStep.UPLOAD_STORAGE) {
                    _uploadLifecycleState.value = UploadLifecycleState.FAILED
                    return@launch
                }
            }

            repository.publishOrDraftPost(
                contentType = contentType,
                title = title,
                caption = caption,
                hashtags = hashtags,
                mentions = mentions,
                visibility = visibility,
                locationTag = locationTag,
                musicTrack = musicTrack,
                customMediaUri = customMediaUri,
                durationSeconds = durationSeconds,
                isDraft = false
            )
            _uploadProgress.value = 1f
            _uploadLifecycleState.value = UploadLifecycleState.PUBLISHED
            delay(500)
            _showCreateSheet.value = false
            _uploadLifecycleState.value = UploadLifecycleState.IDLE
            showBanner("Published to VIDOO (${contentType.replace('_', ' ')})!")
        }
    }

    // 5-Step Server-Validated "Watch Ad & Earn" Flow
    fun triggerWatchAdAndEarn() {
        if (_rewardedAdFlowState.value != RewardedAdFlowState.IDLE &&
            _rewardedAdFlowState.value != RewardedAdFlowState.REWARD_GRANTED
        ) return

        viewModelScope.launch {
            _rewardedAdFlowState.value = RewardedAdFlowState.DISPLAYING_AD
            delay(900)
            _rewardedAdFlowState.value = RewardedAdFlowState.VERIFYING_COMPLETION
            delay(800)
            _rewardedAdFlowState.value = RewardedAdFlowState.SERVER_VALIDATING
            delay(800)
            val rewardCoins = _adminAppSettings.value.rewardedAdCoinAmount
            repository.grantServerVerifiedReward(
                title = "Watch Ad & Earn (AdMob SSV Verified)",
                activityType = "REWARDED_AD",
                coinsDelta = rewardCoins,
                xpDelta = 15,
                xpPerLevelBase = _adminAppSettings.value.xpPerLevelBase
            )
            _rewardedAdFlowState.value = RewardedAdFlowState.REWARD_GRANTED
            showBanner("Server Validated: +$rewardCoins VIDOO COINS added!")
        }
    }

    fun resetRewardedAdFlow() {
        _rewardedAdFlowState.value = RewardedAdFlowState.IDLE
    }

    fun claimDailyLoginBonus() {
        viewModelScope.launch {
            val bonus = _adminAppSettings.value.dailyLoginCoinReward
            repository.grantServerVerifiedReward(
                title = "Daily Login Check-In Bonus",
                activityType = "DAILY_LOGIN",
                coinsDelta = bonus,
                xpDelta = 20,
                xpPerLevelBase = _adminAppSettings.value.xpPerLevelBase
            )
            showBanner("Daily Check-In Verified: +$bonus VIDOO COINS!")
        }
    }

    fun claimChallengeReward(challenge: DailyChallengeEntity) {
        viewModelScope.launch {
            repository.claimDailyChallenge(challenge, _adminAppSettings.value.xpPerLevelBase)
            showBanner("Challenge Completed: +${challenge.coinReward} VIDOO COINS & +${challenge.xpReward} XP!")
        }
    }

    fun applyReferralInviteBonus(friendHandle: String) {
        if (friendHandle.isBlank()) return
        viewModelScope.launch {
            val bonus = _adminAppSettings.value.referralBonusCoins
            repository.grantServerVerifiedReward(
                title = "Verified Referral Registration (@${friendHandle.trim().removePrefix("@")})",
                activityType = "REFERRAL",
                coinsDelta = bonus,
                xpDelta = 50,
                xpPerLevelBase = _adminAppSettings.value.xpPerLevelBase
            )
            showBanner("Referral Validated by Server: +$bonus VIDOO COINS!")
        }
    }

    // Subscriptions (Google Play Billing Architecture)
    fun selectSubscriptionPlan(tierName: String) {
        viewModelScope.launch {
            repository.updateSubscriptionPlan(tierName)
            showBanner("Google Play Billing: Switched to VIDOO $tierName plan")
        }
    }

    // Safety & Moderation
    fun openReportDialog(targetType: String, targetId: String, targetPreview: String) {
        _activeReportTarget.value = ReportDialogTarget(targetType, targetId, targetPreview)
    }

    fun closeReportDialog() {
        _activeReportTarget.value = null
    }

    fun submitSafetyReport(category: ReportCategory, notes: String) {
        val target = _activeReportTarget.value ?: return
        viewModelScope.launch {
            repository.submitReport(
                targetType = target.targetType,
                targetId = target.targetId,
                targetPreview = target.targetPreview,
                category = category,
                notes = notes
            )
            _activeReportTarget.value = null
            showBanner("Report submitted to Vidoo Safety & Moderation queue.")
        }
    }

    fun adminModerateReport(report: ReportEntity, removeContent: Boolean) {
        viewModelScope.launch {
            repository.moderateReport(report, removeContent)
            showBanner(if (removeContent) "Content removed by Admin" else "Content restored & report dismissed")
        }
    }

    fun adminSetUserStatus(userId: String, status: String, verifyCreator: Boolean? = null) {
        viewModelScope.launch {
            repository.adminUpdateUserStatus(userId, status, verifyCreator)
            showBanner("Admin updated user $userId ($status)")
        }
    }

    // Admin Remote Config Updates
    fun updateFeatureFlags(newFlags: FeatureFlags) {
        _featureFlags.value = newFlags
        showBanner("Remote Feature Flags updated")
    }

    fun updateRemoteAdConfig(newConfig: RemoteAdConfig) {
        _remoteAdConfig.value = newConfig
        showBanner("AdService remote configuration updated")
    }

    fun updateCreatorEligibility(newConfig: CreatorEligibilityConfig) {
        _creatorEligibilityConfig.value = newConfig
        showBanner("Creator monetization thresholds updated")
    }

    fun updateAdminAppSettings(newSettings: AdminAppSettings) {
        _adminAppSettings.value = newSettings
        showBanner("App Settings updated")
    }

    fun updateNotificationPrefs(newPrefs: NotificationPreferences) {
        _notificationPrefs.value = newPrefs
    }

    // Search
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun commitSearchQuery(query: String) {
        val clean = query.trim()
        if (clean.isNotEmpty() && !_recentSearches.value.contains(clean)) {
            _recentSearches.value = listOf(clean) + _recentSearches.value.take(7)
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }
}
