package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.domain.model.MusicTrack

@Database(
    entities = [
        UserEntity::class,
        FeedPostEntity::class,
        CommentEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        RewardTransactionEntity::class,
        DailyChallengeEntity::class,
        ReportEntity::class,
        FriendRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VidooDatabase : RoomDatabase() {
    abstract fun vidooDao(): VidooDao

    companion object {
        @Volatile
        private var INSTANCE: VidooDatabase? = null

        fun getDatabase(context: Context): VidooDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VidooDatabase::class.java,
                    "vidoo_platform_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object VidooSeedData {
    const val CURRENT_USER_ID = "user_current_vidoo"

    fun initialUsers(): List<UserEntity> = listOf(
        UserEntity(
            id = CURRENT_USER_ID,
            name = "Alex Rivera",
            username = "alexrivera",
            email = "alex.rivera@vidoo.app",
            phone = "+1 (555) 234-8910",
            bio = "Visual Storyteller & Filmmaker | Exploring sci-fi lighting, neon soundscapes & mobile cinematography. Watch. Share. Connect.",
            avatarResName = "img_avatar_creator_1790684817836",
            coverResName = "img_cover_cyber_studio_1790684775496",
            isVerified = true,
            isEmailVerified = true,
            level = 5,
            currentXp = 75,
            xpForNextLevel = 100,
            followersCount = 14820,
            followingCount = 342,
            friendsCount = 128,
            postsCount = 24,
            videosCount = 16,
            totalViewsCount = 284900L,
            coinBalance = 540,
            streakDays = 7,
            referralCode = "VIDOO77X",
            invitedCount = 9,
            eligibleReferrals = 6,
            subscriptionTier = "CREATOR"
        ),
        UserEntity(
            id = "creator_nova",
            name = "Nova Chen",
            username = "novachen_fx",
            email = "nova@vidoo.app",
            bio = "3D Motion Designer & Cyberpunk Choreographer • New Short every Tuesday & Friday",
            avatarResName = "img_avatar_creator_1790684817836",
            coverResName = "img_cover_cyber_studio_1790684775496",
            isVerified = true,
            level = 12,
            currentXp = 90,
            followersCount = 92400,
            followingCount = 215,
            friendsCount = 64,
            postsCount = 89,
            videosCount = 74,
            totalViewsCount = 1420000L,
            isFollowing = true,
            isFriend = true
        ),
        UserEntity(
            id = "creator_kael",
            name = "Kaelen Vance",
            username = "kaeltech",
            email = "kael@vidoo.app",
            bio = "Audio Engineer & Studio Gear Reviewer • Testing cameras, synths & mobile workflows",
            avatarResName = "img_avatar_creator_1790684817836",
            coverResName = "img_video_tech_review_1790684807149",
            isVerified = true,
            level = 9,
            currentXp = 45,
            followersCount = 54100,
            followingCount = 180,
            friendsCount = 42,
            postsCount = 61,
            videosCount = 48,
            totalViewsCount = 810500L,
            isFollowing = true,
            isFriend = false
        ),
        UserEntity(
            id = "creator_sora",
            name = "Sora Takahashi",
            username = "sora_visuals",
            email = "sora@vidoo.app",
            bio = "Tokyo Night Street Photography & Anamorphic Shorts • Color Grading presets",
            avatarResName = "img_avatar_creator_1790684817836",
            coverResName = "img_cover_cyber_studio_1790684775496",
            isVerified = false,
            level = 6,
            currentXp = 30,
            followersCount = 19300,
            followingCount = 410,
            friendsCount = 55,
            postsCount = 37,
            videosCount = 29,
            totalViewsCount = 310200L,
            isFollowing = false,
            isFriend = false
        ),
        UserEntity(
            id = "creator_mira",
            name = "Mira Solis",
            username = "mirasounds",
            email = "mira@vidoo.app",
            bio = "Royalty-Cleared Synthwave Producer • Official Vidoo Music Library Partner",
            avatarResName = "img_avatar_creator_1790684817836",
            coverResName = "img_video_tech_review_1790684807149",
            isVerified = true,
            level = 8,
            currentXp = 80,
            followersCount = 41800,
            followingCount = 290,
            friendsCount = 73,
            postsCount = 52,
            videosCount = 35,
            totalViewsCount = 625000L,
            isFollowing = true,
            isFriend = true
        )
    )

    fun initialPosts(): List<FeedPostEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            FeedPostEntity(
                id = "post_long_1",
                authorId = "creator_kael",
                authorName = "Kaelen Vance",
                authorUsername = "kaeltech",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = true,
                contentType = "LONG_VIDEO",
                title = "Building the Ultimate 2026 Dark-Mode Creator Desk & Hybrid Cinema Rig",
                caption = "Full breakdown of our dual-monitor acoustic desk, cyan/violet DMX tube lighting, and low-latency 4K60 workflow for Vidoo creators.",
                hashtags = "#CreatorStudio,#TechSetup,#Filmmaking,#VidooOriginals",
                mentions = "@alexrivera",
                mediaResName = "img_video_tech_review_1790684807149",
                durationSeconds = 864, // 14:24
                aspectRatio = "16:9",
                locationTag = "Seattle Soundstage",
                musicTrackTitle = "Midnight Telemetry (Instrumental)",
                musicAttribution = "Licensed via Vidoo Audio Clear • CC-BY Mira Solis",
                category = "Tech & Gear",
                likesCount = 4820,
                commentsCount = 342,
                sharesCount = 615,
                savesCount = 1290,
                viewsCount = 68400L,
                watchTimeSeconds = 412000L,
                completionRate = 0.84f,
                isLiked = true,
                isSaved = true,
                isFollowingAuthor = true,
                resumePositionSeconds = 195,
                createdAtTimestamp = now - 3600_000L * 3
            ),
            FeedPostEntity(
                id = "post_short_1",
                authorId = "creator_nova",
                authorName = "Nova Chen",
                authorUsername = "novachen_fx",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = true,
                contentType = "SHORT_VIDEO",
                title = "Neon Rain Freestyle Choreography in 60fps",
                caption = "Captured on anamorphic 35mm lens in downtown rain reflections! Wait for the cyan light drop at 0:18.",
                hashtags = "#VidooShorts,#NeonDance,#CyberpunkVibes,#Choreography",
                mediaResName = "img_short_neon_dancer_1790684791969",
                durationSeconds = 34,
                aspectRatio = "9:16",
                locationTag = "Neo District",
                musicTrackTitle = "Cybernetic Pulse — 128 BPM",
                musicAttribution = "Royalty-Cleared • Vidoo Sound #VS-902",
                category = "Dance & Performance",
                likesCount = 19400,
                commentsCount = 876,
                sharesCount = 2310,
                savesCount = 4120,
                viewsCount = 245000L,
                watchTimeSeconds = 710000L,
                completionRate = 0.93f,
                isLiked = true,
                isSaved = false,
                isFollowingAuthor = true,
                createdAtTimestamp = now - 3600_000L * 5
            ),
            FeedPostEntity(
                id = "post_photo_1",
                authorId = CURRENT_USER_ID,
                authorName = "Alex Rivera",
                authorUsername = "alexrivera",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = true,
                contentType = "PHOTO",
                title = "Behind the Scenes: Gimbal Calibration Night",
                caption = "Dialing in the dual-axis stabilizer with #00E5FF key lights and deep indigo shadows before shooting tomorrow's documentary episode.",
                hashtags = "#Cinematography,#BehindTheScenes,#VidooCreator,#LightingDesign",
                mediaResName = "img_cover_cyber_studio_1790684775496",
                durationSeconds = 0,
                aspectRatio = "16:9",
                locationTag = "Studio B • Los Angeles",
                category = "Filmmaking",
                likesCount = 1640,
                commentsCount = 128,
                sharesCount = 94,
                savesCount = 310,
                viewsCount = 14200L,
                completionRate = 0.89f,
                isLiked = false,
                isSaved = true,
                isFollowingAuthor = true,
                createdAtTimestamp = now - 3600_000L * 8
            ),
            FeedPostEntity(
                id = "post_short_2",
                authorId = "creator_sora",
                authorName = "Sora Takahashi",
                authorUsername = "sora_visuals",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = false,
                contentType = "SHORT_VIDEO",
                title = "3 Camera Transitions You Can Shoot Handheld",
                caption = "No plugins needed! Match whip-pan speed with a 180-degree shutter rule for seamless cuts.",
                hashtags = "#EditingTips,#CameraTricks,#VidooShorts,#VisualFX",
                mediaResName = "img_cover_cyber_studio_1790684775496",
                durationSeconds = 48,
                aspectRatio = "9:16",
                locationTag = "Shibuya Crossing",
                musicTrackTitle = "Horizon Drift — LoFi Synth",
                musicAttribution = "Royalty-Cleared • Vidoo Sound #VS-418",
                category = "Tutorials",
                likesCount = 8730,
                commentsCount = 412,
                sharesCount = 1150,
                savesCount = 2890,
                viewsCount = 112400L,
                watchTimeSeconds = 420000L,
                completionRate = 0.88f,
                isLiked = false,
                isSaved = false,
                isFollowingAuthor = false,
                createdAtTimestamp = now - 3600_000L * 11
            ),
            FeedPostEntity(
                id = "post_long_2",
                authorId = CURRENT_USER_ID,
                authorName = "Alex Rivera",
                authorUsername = "alexrivera",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = true,
                contentType = "LONG_VIDEO",
                title = "Color Grading Neon Night Footage: Complete LUT & Scopes Masterclass",
                caption = "Learn how to balance skin tones under harsh cyan and magenta LED signage while preserving shadow detail on mobile OLED displays.",
                hashtags = "#ColorGrading,#Filmmaker,#VidooAcademy,#Tutorial",
                mediaResName = "img_cover_cyber_studio_1790684775496",
                durationSeconds = 1285, // 21:25
                aspectRatio = "16:9",
                locationTag = "Los Angeles, CA",
                musicTrackTitle = "Subzero Atmosphere",
                musicAttribution = "Licensed via Vidoo Audio Clear • CC-BY Mira Solis",
                category = "Education",
                likesCount = 6290,
                commentsCount = 519,
                sharesCount = 840,
                savesCount = 2150,
                viewsCount = 94300L,
                watchTimeSeconds = 690000L,
                completionRate = 0.81f,
                isLiked = true,
                isSaved = true,
                isFollowingAuthor = true,
                resumePositionSeconds = 420,
                createdAtTimestamp = now - 3600_000L * 16
            ),
            FeedPostEntity(
                id = "post_text_1",
                authorId = "creator_mira",
                authorName = "Mira Solis",
                authorUsername = "mirasounds",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = true,
                contentType = "TEXT",
                title = "New Royalty-Cleared Synthwave Pack Live in Vidoo Music Library!",
                caption = "Just uploaded 12 brand-new stems and full tracks cleared for monetized Vidoo Shorts & Long Videos. Tag #MiraSounds when you use them so I can share your edits!",
                hashtags = "#RoyaltyFreeMusic,#Synthwave,#Creators,#VidooMusic",
                mentions = "@novachen_fx,@kaeltech",
                mediaResName = "",
                durationSeconds = 0,
                aspectRatio = "16:9",
                locationTag = "Berlin Audio Lab",
                category = "Music & Audio",
                likesCount = 2190,
                commentsCount = 195,
                sharesCount = 310,
                savesCount = 480,
                viewsCount = 21500L,
                completionRate = 0.91f,
                isLiked = true,
                isSaved = false,
                isFollowingAuthor = true,
                createdAtTimestamp = now - 3600_000L * 20
            ),
            FeedPostEntity(
                id = "post_short_3",
                authorId = "creator_kael",
                authorName = "Kaelen Vance",
                authorUsername = "kaeltech",
                authorAvatarRes = "img_avatar_creator_1790684817836",
                isAuthorVerified = true,
                contentType = "SHORT_VIDEO",
                title = "60-Second Acoustic Treatment Test: Clap Echo Before & After",
                caption = "Hear the difference 6 bass traps and acoustic slats make in an untreated concrete studio room.",
                hashtags = "#AudioEngineering,#StudioBuild,#VidooShorts,#SoundTest",
                mediaResName = "img_video_tech_review_1790684807149",
                durationSeconds = 52,
                aspectRatio = "9:16",
                locationTag = "Seattle Soundstage",
                musicTrackTitle = "Pure Acoustic Reference",
                musicAttribution = "Original Studio Recording • Kaelen Vance",
                category = "Tech & Gear",
                likesCount = 11300,
                commentsCount = 530,
                sharesCount = 890,
                savesCount = 1940,
                viewsCount = 158000L,
                watchTimeSeconds = 540000L,
                completionRate = 0.87f,
                isLiked = false,
                isSaved = true,
                isFollowingAuthor = true,
                createdAtTimestamp = now - 3600_000L * 26
            )
        )
    }

    fun initialComments(): List<CommentEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            CommentEntity(
                id = "cmt_1",
                postId = "post_long_1",
                authorId = "creator_nova",
                authorName = "Nova Chen",
                authorUsername = "novachen_fx",
                isAuthorVerified = true,
                content = "The cable management behind that ultrawide monitor is surgical! What bias lighting Kelvin temperature are you running?",
                likesCount = 148,
                isLiked = true,
                createdAtTimestamp = now - 3600_000L * 2
            ),
            CommentEntity(
                id = "cmt_2",
                postId = "post_long_1",
                parentCommentId = "cmt_1",
                authorId = "creator_kael",
                authorName = "Kaelen Vance",
                authorUsername = "kaeltech",
                isAuthorVerified = true,
                content = "@novachen_fx Running 6500K D65 behind the main grading monitor and #00E5FF cyan accent tubes on the side walls!",
                likesCount = 64,
                isLiked = false,
                createdAtTimestamp = now - 3600_000L * 1
            ),
            CommentEntity(
                id = "cmt_3",
                postId = "post_long_1",
                authorId = "creator_sora",
                authorName = "Sora Takahashi",
                authorUsername = "sora_visuals",
                isAuthorVerified = false,
                content = "Switched to 1080p HD at 1.25x speed in the Vidoo player—butter smooth playback and super helpful audio routing diagram at 08:15.",
                likesCount = 42,
                isLiked = false,
                createdAtTimestamp = now - 1800_000L
            ),
            CommentEntity(
                id = "cmt_4",
                postId = "post_short_1",
                authorId = "creator_mira",
                authorName = "Mira Solis",
                authorUsername = "mirasounds",
                isAuthorVerified = true,
                content = "That beat sync at 0:18 gave me chills! So hyped you used Cybernetic Pulse for this routine.",
                likesCount = 312,
                isLiked = true,
                createdAtTimestamp = now - 7200_000L
            )
        )
    }

    fun initialConversations(): List<ConversationEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ConversationEntity(
                id = "conv_nova",
                peerUserId = "creator_nova",
                peerName = "Nova Chen",
                peerUsername = "novachen_fx",
                peerAvatarRes = "img_avatar_creator_1790684817836",
                isPeerOnline = true,
                lastMessagePreview = "Sent over the 4K LUT file for our collab Short!",
                unreadCount = 2,
                updatedAtTimestamp = now - 900_000L
            ),
            ConversationEntity(
                id = "conv_kael",
                peerUserId = "creator_kael",
                peerName = "Kaelen Vance",
                peerUsername = "kaeltech",
                peerAvatarRes = "img_avatar_creator_1790684817836",
                isPeerOnline = true,
                lastMessagePreview = "Let's test the dual mic setup on Vidoo Live soon.",
                unreadCount = 0,
                updatedAtTimestamp = now - 5400_000L
            ),
            ConversationEntity(
                id = "conv_mira",
                peerUserId = "creator_mira",
                peerName = "Mira Solis",
                peerUsername = "mirasounds",
                peerAvatarRes = "img_avatar_creator_1790684817836",
                isPeerOnline = false,
                lastMessagePreview = "Cleared the stems for your documentary intro!",
                unreadCount = 1,
                updatedAtTimestamp = now - 14400_000L
            )
        )
    }

    fun initialMessages(): List<MessageEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MessageEntity(
                id = "msg_1",
                conversationId = "conv_nova",
                senderId = "creator_nova",
                senderName = "Nova Chen",
                textContent = "Hey Alex! Loved the lighting breakdown on your latest long video.",
                isRead = true,
                createdAtTimestamp = now - 1800_000L
            ),
            MessageEntity(
                id = "msg_2",
                conversationId = "conv_nova",
                senderId = CURRENT_USER_ID,
                senderName = "Alex Rivera",
                textContent = "Thanks Nova! Ready to shoot our neon transition collab this weekend?",
                isRead = true,
                createdAtTimestamp = now - 1200_000L
            ),
            MessageEntity(
                id = "msg_3",
                conversationId = "conv_nova",
                senderId = "creator_nova",
                senderName = "Nova Chen",
                textContent = "Sent over the 4K LUT file for our collab Short!",
                mediaType = "IMAGE",
                mediaUriOrRes = "img_short_neon_dancer_1790684791969",
                isRead = false,
                createdAtTimestamp = now - 900_000L
            )
        )
    }

    fun initialNotifications(): List<NotificationEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            NotificationEntity(
                id = "notif_1",
                type = "REWARDS",
                title = "Server-Verified Daily Streak Bonus",
                body = "+25 VIDOO COINS & +30 XP credited for your 7-day creator streak!",
                actorUsername = "vidoo_rewards",
                isRead = false,
                createdAtTimestamp = now - 600_000L
            ),
            NotificationEntity(
                id = "notif_2",
                type = "COMMENT",
                title = "Nova Chen commented on your video",
                body = "@novachen_fx: \"The cable management behind that ultrawide monitor is surgical!\"",
                actorUsername = "novachen_fx",
                targetId = "post_long_1",
                isRead = false,
                createdAtTimestamp = now - 1800_000L
            ),
            NotificationEntity(
                id = "notif_3",
                type = "NEW_FOLLOWER",
                title = "Sora Takahashi started following you",
                body = "@sora_visuals and 18 others followed your creator profile today.",
                actorUsername = "sora_visuals",
                isRead = false,
                createdAtTimestamp = now - 3600_000L
            ),
            NotificationEntity(
                id = "notif_4",
                type = "CREATOR_UPLOAD",
                title = "Kaelen Vance uploaded a new Long Video",
                body = "Watch \"Building the Ultimate 2026 Dark-Mode Creator Desk\" in 4K.",
                actorUsername = "kaeltech",
                targetId = "post_long_1",
                isRead = true,
                createdAtTimestamp = now - 7200_000L
            ),
            NotificationEntity(
                id = "notif_5",
                type = "SYSTEM_ANNOUNCEMENT",
                title = "Vidoo Creator Studio Eligibility Active",
                body = "Your channel meets all policy, watch-time, and follower thresholds.",
                actorUsername = "vidoo_official",
                isRead = true,
                createdAtTimestamp = now - 14400_000L
            )
        )
    }

    fun initialRewardTransactions(): List<RewardTransactionEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            RewardTransactionEntity(
                id = "tx_101",
                title = "7-Day Login Streak Milestone",
                activityType = "DAILY_LOGIN",
                coinsDelta = 50,
                xpDelta = 40,
                serverVerificationToken = "srv_sig_99482a1f_streak7",
                createdAtTimestamp = now - 3600_000L * 2
            ),
            RewardTransactionEntity(
                id = "tx_102",
                title = "Verified Rewarded Ad Completion",
                activityType = "REWARDED_AD",
                coinsDelta = 30,
                xpDelta = 15,
                serverVerificationToken = "admob_ssv_88310c4e_verified",
                createdAtTimestamp = now - 3600_000L * 8
            ),
            RewardTransactionEntity(
                id = "tx_103",
                title = "Eligible Creator Referral (@sora_visuals)",
                activityType = "REFERRAL",
                coinsDelta = 100,
                xpDelta = 50,
                serverVerificationToken = "ref_srv_77219b0d_VIDOO77X",
                createdAtTimestamp = now - 3600_000L * 24
            ),
            RewardTransactionEntity(
                id = "tx_104",
                title = "Daily Challenge: Engage with 3 Shorts",
                activityType = "DAILY_CHALLENGE",
                coinsDelta = 40,
                xpDelta = 25,
                serverVerificationToken = "chg_srv_66104f2a_daily",
                createdAtTimestamp = now - 3600_000L * 36
            )
        )
    }

    fun initialDailyChallenges(): List<DailyChallengeEntity> = listOf(
        DailyChallengeEntity(
            id = "dc_watch_shorts",
            title = "Short-Form Explorer",
            description = "Watch & engage with 3 Vidoo Shorts today",
            targetCount = 3,
            currentCount = 2,
            coinReward = 35,
            xpReward = 25,
            isClaimed = false
        ),
        DailyChallengeEntity(
            id = "dc_post_comment",
            title = "Community Connector",
            description = "Leave a constructive comment or reply on any creator post",
            targetCount = 2,
            currentCount = 2,
            coinReward = 40,
            xpReward = 30,
            isClaimed = false
        ),
        DailyChallengeEntity(
            id = "dc_upload_content",
            title = "Creator Spotlight",
            description = "Publish a Post, Photo, Short, or Long Video",
            targetCount = 1,
            currentCount = 1,
            coinReward = 60,
            xpReward = 50,
            isClaimed = false
        )
    )

    fun initialReports(): List<ReportEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ReportEntity(
                id = "rep_1",
                targetType = "COMMENT",
                targetId = "cmt_spam_99",
                targetPreview = "\"Free crypto giveaway click my bio link now!!\"",
                reporterId = "creator_nova",
                category = "SCAM_FRAUD",
                notes = "Automated phishing link comment under tech video.",
                status = "PENDING_REVIEW",
                createdAtTimestamp = now - 3600_000L * 4
            ),
            ReportEntity(
                id = "rep_2",
                targetType = "VIDEO",
                targetId = "vid_reupload_42",
                targetPreview = "Uncredited re-upload of 3D neon breakdown reel",
                reporterId = "creator_kael",
                category = "COPYRIGHT",
                notes = "Original work belongs to @novachen_fx.",
                status = "PENDING_REVIEW",
                createdAtTimestamp = now - 3600_000L * 9
            )
        )
    }

    fun initialFriendRequests(): List<FriendRequestEntity> = listOf(
        FriendRequestEntity(
            id = "freq_1",
            fromUserId = "creator_sora",
            fromName = "Sora Takahashi",
            fromUsername = "sora_visuals",
            fromAvatarRes = "img_avatar_creator_1790684817836",
            mutualFriendsCount = 14
        )
    )

    fun licensedMusicLibrary(): List<MusicTrack> = listOf(
        MusicTrack(
            id = "mus_1",
            title = "Cybernetic Pulse",
            artist = "Mira Solis",
            durationSeconds = 60,
            licenseCode = "VIDOO-CLR-902",
            attributionText = "Licensed under Vidoo Royalty-Cleared Creator License • Mira Solis",
            bpm = 128,
            genre = "Synthwave"
        ),
        MusicTrack(
            id = "mus_2",
            title = "Midnight Telemetry",
            artist = "Aether Labs",
            durationSeconds = 145,
            licenseCode = "VIDOO-CLR-418",
            attributionText = "Licensed under Vidoo Royalty-Cleared Creator License • Aether Labs",
            bpm = 110,
            genre = "Ambient Electronic"
        ),
        MusicTrack(
            id = "mus_3",
            title = "Horizon Drift (Lo-Fi Tape)",
            artist = "Koto & Rain",
            durationSeconds = 90,
            licenseCode = "VIDOO-CLR-511",
            attributionText = "Licensed under Vidoo Royalty-Cleared Creator License • Koto & Rain",
            bpm = 86,
            genre = "Lo-Fi Chill"
        ),
        MusicTrack(
            id = "mus_4",
            title = "Kinetic Subbass",
            artist = "Vector Nine",
            durationSeconds = 45,
            licenseCode = "VIDOO-CLR-774",
            attributionText = "Licensed under Vidoo Royalty-Cleared Creator License • Vector Nine",
            bpm = 140,
            genre = "Future Garage"
        )
    )
}
