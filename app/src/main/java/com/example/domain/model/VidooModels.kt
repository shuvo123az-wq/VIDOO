package com.example.domain.model

sealed interface UiResultState<out T> {
    data object Loading : UiResultState<Nothing>
    data class Success<T>(val data: T) : UiResultState<T>
    data class Empty(val message: String, val subtitle: String = "") : UiResultState<Nothing>
    data class Error(val message: String, val technicalDetail: String = "") : UiResultState<Nothing>
}

enum class MainNavTab(val label: String) {
    HOME("Home"),
    SHORTS("Shorts"),
    CREATE("Create"),
    NOTIFICATIONS("Alerts"),
    PROFILE("Profile")
}

enum class SecondaryDestination(val title: String) {
    NONE(""),
    SEARCH("Global Search"),
    MESSAGES("Messages"),
    LONG_VIDEOS("Long Videos"),
    REWARDS("Vidoo Rewards & Coins"),
    DAILY_CHALLENGE("Daily Challenges"),
    CREATOR_STUDIO("Vidoo Creator Studio"),
    SUBSCRIPTIONS("Subscriptions & Plans"),
    SAVED_POSTS("Saved Content"),
    INVITE_FRIENDS("Refer & Earn"),
    ADMIN_PANEL("Admin Dashboard"),
    SETTINGS("Settings & Privacy"),
    LEGAL_CENTER("Policies & Safety"),
    HELP_SUPPORT("Help & Support"),
    ARCHITECTURE_SPECS("Cloud & Play Store Specs")
}

enum class FeedCategory(val label: String) {
    FOR_YOU("For You"),
    FOLLOWING("Following"),
    TRENDING("Trending"),
    RECENT("Recent"),
    SHORTS("Shorts")
}

enum class ContentType(val label: String) {
    TEXT("Text Post"),
    PHOTO("Photo Post"),
    VIDEO("Video Post"),
    SHORT_VIDEO("Short Video"),
    LONG_VIDEO("Long Video")
}

enum class PostVisibility(val label: String) {
    PUBLIC("Public"),
    FOLLOWERS("Followers Only"),
    PRIVATE("Private")
}

enum class SubscriptionTier(
    val title: String,
    val priceDisplay: String,
    val skuId: String,
    val badgeLabel: String,
    val features: List<String>
) {
    FREE(
        title = "FREE",
        priceDisplay = "$0 / month",
        skuId = "vidoo_plan_free",
        badgeLabel = "Standard",
        features = listOf(
            "Personalized For You & Shorts feed",
            "Upload photos, posts, Shorts & Long Videos",
            "Standard profile & messaging",
            "Earn Vidoo Coins from daily challenges"
        )
    ),
    PREMIUM(
        title = "PREMIUM",
        priceDisplay = "$4.99 / month",
        skuId = "vidoo_plan_premium_monthly",
        badgeLabel = "PRO",
        features = listOf(
            "Reduced advertising across eligible feeds",
            "Exclusive Cyan/Purple profile customization",
            "Verified Premium supporter badge",
            "4K Ultra HD playback & background resume",
            "2x Daily Streak XP multiplier"
        )
    ),
    CREATOR(
        title = "CREATOR",
        priceDisplay = "$12.99 / month",
        skuId = "vidoo_plan_creator_monthly",
        badgeLabel = "CREATOR+",
        features = listOf(
            "Full Vidoo Creator Studio & audience retention analytics",
            "Creator monetization & ad-revenue share tools",
            "Priority 1080p/4K cloud video transcoding",
            "Licensed royalty-cleared music library pro tracks",
            "Scheduled publishing & brand sponsorship kit"
        )
    )
}

enum class UploadStep(val stepNumber: Int, val title: String) {
    SELECT_VIDEO(1, "Step 1: Select Media"),
    GENERATE_THUMBNAIL(2, "Step 2: Generate Thumbnail"),
    COMPRESS_VIDEO(3, "Step 3: Adaptive Video Compression"),
    UPLOAD_STORAGE(4, "Step 4: Upload to Cloud Storage"),
    SHOW_PROGRESS(5, "Step 5: Verify Upload Integrity"),
    PROCESS_VIDEO(6, "Step 6: Multi-Resolution Transcoding"),
    PUBLISH(7, "Step 7: Publish to Vidoo Feed")
}

enum class UploadLifecycleState(val label: String) {
    IDLE("Ready"),
    UPLOADING("Uploading"),
    PROCESSING("Processing"),
    PUBLISHED("Published"),
    FAILED("Failed"),
    RETRY("Retrying")
}

enum class ReportCategory(val label: String, val description: String) {
    SPAM("Spam", "Misleading metadata, repetitive comments, or bot activity"),
    HARASSMENT("Harassment", "Targeted bullying, intimidation, or personal attacks"),
    HATE_SPEECH("Hate / Abusive Content", "Attacks against protected groups or hate speech"),
    SEXUAL_CONTENT("Sexual Content", "Nudity, sexually explicit, or suggestive material"),
    VIOLENCE("Violence", "Graphic violence, dangerous acts, or self-harm"),
    COPYRIGHT("Copyright Concern", "Unauthorized use of copyrighted video, audio, or art"),
    SCAM_FRAUD("Scam / Fraud", "Phishing links, fake giveaways, or financial scams"),
    OTHER("Other", "Other violation of Vidoo Community Guidelines")
}

enum class NotificationType(val label: String, val categoryKey: String) {
    NEW_FOLLOWER("New Follower", "social"),
    FRIEND_REQUEST("Friend Request", "social"),
    ACCEPTED_REQUEST("Accepted Request", "social"),
    LIKE("Post Like", "social"),
    COMMENT("New Comment", "social"),
    REPLY("Comment Reply", "social"),
    MENTION("Mention", "social"),
    NEW_MESSAGE("Direct Message", "social"),
    CREATOR_UPLOAD("Creator Upload", "content"),
    DAILY_CHALLENGE("Daily Challenge", "challenges"),
    REWARDS("Vidoo Coins Reward", "rewards"),
    SUBSCRIPTION("Subscription Update", "offers"),
    SYSTEM_ANNOUNCEMENT("System Announcement", "system")
}

enum class CommentSortOrder(val label: String) {
    TOP("Top Comments"),
    NEWEST("Newest First")
}

data class FeatureFlags(
    val enableShorts: Boolean = true,
    val enableLongVideos: Boolean = true,
    val enableMessaging: Boolean = true,
    val enableRewards: Boolean = true,
    val enableSubscriptions: Boolean = true,
    val enableCreatorMonetization: Boolean = true,
    val enableAds: Boolean = true,
    val enableReferrals: Boolean = true,
    val enableStories: Boolean = false,
    val enableLiveStreaming: Boolean = false
)

data class RemoteAdConfig(
    val enableAds: Boolean = true,
    val bannerEnabled: Boolean = true,
    val nativeAdsEnabled: Boolean = true,
    val interstitialEnabled: Boolean = true,
    val rewardedAdsEnabled: Boolean = true,
    val adFrequencyEveryNPosts: Int = 4,
    val showInterstitialEveryNInteractions: Int = 12,
    // Official Google AdMob Test Unit IDs (Never hardcode production IDs)
    val testBannerUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val testNativeUnitId: String = "ca-app-pub-3940256099942544/2247696110",
    val testInterstitialUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    val testRewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917"
)

data class CreatorEligibilityConfig(
    val minFollowers: Int = 1000,
    val minViews: Int = 10000,
    val minWatchTimeMinutes: Int = 4000,
    val minAccountAgeDays: Int = 30,
    val requirePolicyCompliance: Boolean = true
)

data class AdminAppSettings(
    val maintenanceMode: Boolean = false,
    val systemAnnouncement: String = "Welcome to VIDOO 1.0! Complete today's creator challenge for +50 Vidoo Coins.",
    val maxShortVideoDurationSeconds: Int = 60,
    val maxUploadSizeMb: Int = 500,
    val xpPerLevelBase: Int = 100,
    val dailyLoginCoinReward: Int = 25,
    val rewardedAdCoinAmount: Int = 30,
    val referralBonusCoins: Int = 100
)

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val licenseCode: String,
    val attributionText: String,
    val bpm: Int,
    val genre: String
)

data class NotificationPreferences(
    val socialActivity: Boolean = true,
    val newContent: Boolean = true,
    val rewards: Boolean = true,
    val offers: Boolean = true,
    val dailyChallenge: Boolean = true
)

data class CreatorAnalyticsSummary(
    val totalViews: Long = 48_920L,
    val watchTimeMinutes: Long = 6_410L,
    val totalLikes: Int = 8_430,
    val totalComments: Int = 1_295,
    val totalShares: Int = 940,
    val followersGained30d: Int = 1_420,
    val eligibleMonetizedViews: Long = 39_100L,
    val estimatedEarningsUsd: Double = 142.85,
    val monetizationStatus: String = "ELIGIBLE_ACTIVE",
    val paymentStatus: String = "Threshold Met • Next Payout Cycle: 15th",
    val accountAgeDays: Int = 64,
    val policyCompliant: Boolean = true
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val coinReward: Int,
    val progress: Float,
    val isUnlocked: Boolean
)
