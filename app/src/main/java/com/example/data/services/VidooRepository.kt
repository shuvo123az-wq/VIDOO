package com.example.data.services

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
import com.example.data.local.VidooDao
import com.example.data.local.VidooSeedData
import com.example.domain.model.AchievementItem
import com.example.domain.model.CommentSortOrder
import com.example.domain.model.FeedCategory
import com.example.domain.model.MusicTrack
import com.example.domain.model.NotificationType
import com.example.domain.model.RemoteAdConfig
import com.example.domain.model.ReportCategory
import java.util.UUID
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VidooAnalyticsTracker {
    private val _loggedEvents = mutableListOf<String>()
    val loggedEvents: List<String> get() = _loggedEvents.toList()

    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        val formatted = buildString {
            append(eventName)
            if (params.isNotEmpty()) {
                append(" ")
                append(params.entries.joinToString(", ") { "${it.key}=${it.value}" })
            }
        }
        _loggedEvents.add(0, formatted)
        if (_loggedEvents.size > 50) {
            _loggedEvents.removeAt(_loggedEvents.lastIndex)
        }
    }
}

class AdService {
    private var interactionCounter = 0

    fun recordUserInteraction(config: RemoteAdConfig, isPremiumSubscriber: Boolean): Boolean {
        if (!config.enableAds || !config.interstitialEnabled || isPremiumSubscriber) return false
        interactionCounter++
        if (interactionCounter >= config.showInterstitialEveryNInteractions.coerceAtLeast(5)) {
            interactionCounter = 0
            return true
        }
        return false
    }

    fun shouldShowNativeAdAtIndex(index: Int, config: RemoteAdConfig, isPremiumSubscriber: Boolean): Boolean {
        if (!config.enableAds || !config.nativeAdsEnabled || isPremiumSubscriber) return false
        val freq = config.adFrequencyEveryNPosts.coerceAtLeast(3)
        return index > 0 && (index + 1) % freq == 0
    }
}

class VidooRepository(
    private val dao: VidooDao,
    val analytics: VidooAnalyticsTracker = VidooAnalyticsTracker(),
    val adService: AdService = AdService()
) {
    val currentUserId: String = VidooSeedData.CURRENT_USER_ID

    suspend fun ensureSeeded() {
        if (dao.getUserCount() == 0) {
            dao.upsertUsers(VidooSeedData.initialUsers())
            dao.upsertPosts(VidooSeedData.initialPosts())
            dao.insertComments(VidooSeedData.initialComments())
            dao.upsertConversations(VidooSeedData.initialConversations())
            dao.insertMessages(VidooSeedData.initialMessages())
            dao.insertNotifications(VidooSeedData.initialNotifications())
            dao.insertRewardTransactions(VidooSeedData.initialRewardTransactions())
            dao.upsertDailyChallenges(VidooSeedData.initialDailyChallenges())
            dao.upsertReports(VidooSeedData.initialReports())
            dao.upsertFriendRequests(VidooSeedData.initialFriendRequests())
            analytics.logEvent("app_open", mapOf("seed" to "initialized"))
        } else {
            analytics.logEvent("app_open", mapOf("seed" to "cached"))
        }
    }

    fun observeCurrentUser(): Flow<UserEntity?> = dao.observeUserById(currentUserId)

    fun observeOtherUsers(): Flow<List<UserEntity>> = dao.observeOtherUsers(currentUserId)

    fun observeFeedByCategory(category: FeedCategory): Flow<List<FeedPostEntity>> {
        return dao.observeAllPublishedPosts().map { posts ->
            when (category) {
                FeedCategory.FOR_YOU -> posts.sortedByDescending { computeRecommendationScore(it) }
                FeedCategory.FOLLOWING -> posts.filter { it.isFollowingAuthor || it.authorId == currentUserId }
                FeedCategory.TRENDING -> posts.sortedByDescending {
                    it.viewsCount + (it.likesCount * 8L) + (it.sharesCount * 15L)
                }
                FeedCategory.RECENT -> posts.sortedByDescending { it.createdAtTimestamp }
                FeedCategory.SHORTS -> posts.filter { it.contentType == "SHORT_VIDEO" }
            }
        }
    }

    /**
     * Recommendation engine ranking signal formula based on:
     * Watch time, completion rate, likes, comments, shares, saves, follows, and recency.
     */
    fun computeRecommendationScore(post: FeedPostEntity): Double {
        val watchSignal = (post.watchTimeSeconds / 100.0) * (0.5 + post.completionRate)
        val engagementSignal = (post.likesCount * 1.5) +
            (post.commentsCount * 3.2) +
            (post.sharesCount * 4.5) +
            (post.savesCount * 3.8)
        val relationshipBoost = if (post.isFollowingAuthor) 1.25 else 1.0
        return (watchSignal + engagementSignal) * relationshipBoost
    }

    fun observeShortVideos(): Flow<List<FeedPostEntity>> =
        dao.observeAllPublishedPosts().map { list ->
            list.filter { it.contentType == "SHORT_VIDEO" }
        }

    fun observeLongVideos(): Flow<List<FeedPostEntity>> =
        dao.observeAllPublishedPosts().map { list ->
            list.filter { it.contentType == "LONG_VIDEO" }
        }

    fun observeSavedPosts(): Flow<List<FeedPostEntity>> = dao.observeSavedPosts()

    fun observeDraftPosts(): Flow<List<FeedPostEntity>> = dao.observeDraftPosts()

    fun observeComments(postId: String, sortOrder: CommentSortOrder, limit: Int): Flow<List<CommentEntity>> {
        return when (sortOrder) {
            CommentSortOrder.TOP -> dao.observeCommentsTop(postId, limit)
            CommentSortOrder.NEWEST -> dao.observeCommentsNewest(postId, limit)
        }
    }

    fun observeConversations(): Flow<List<ConversationEntity>> = dao.observeConversations()

    fun observeMessages(conversationId: String): Flow<List<MessageEntity>> =
        dao.observeMessagesForConversation(conversationId)

    fun observeNotifications(): Flow<List<NotificationEntity>> = dao.observeNotifications()

    fun observeRewardTransactions(): Flow<List<RewardTransactionEntity>> = dao.observeRewardTransactions()

    fun observeDailyChallenges(): Flow<List<DailyChallengeEntity>> = dao.observeDailyChallenges()

    fun observeReports(): Flow<List<ReportEntity>> = dao.observeReports()

    fun observeFriendRequests(): Flow<List<FriendRequestEntity>> = dao.observePendingFriendRequests()

    fun getLicensedMusicLibrary(): List<MusicTrack> = VidooSeedData.licensedMusicLibrary()

    fun getAchievements(user: UserEntity?): List<AchievementItem> {
        val posts = user?.postsCount ?: 0
        val streak = user?.streakDays ?: 1
        val referrals = user?.eligibleReferrals ?: 0
        return listOf(
            AchievementItem(
                id = "ach_first_upload",
                title = "Original Director",
                description = "Publish at least 10 original posts or videos on Vidoo",
                xpReward = 100,
                coinReward = 75,
                progress = (posts / 10f).coerceIn(0f, 1f),
                isUnlocked = posts >= 10
            ),
            AchievementItem(
                id = "ach_streak_7",
                title = "7-Day Momentum",
                description = "Maintain a 7-day active creator & viewer login streak",
                xpReward = 150,
                coinReward = 100,
                progress = (streak / 7f).coerceIn(0f, 1f),
                isUnlocked = streak >= 7
            ),
            AchievementItem(
                id = "ach_referral_5",
                title = "Community Catalyst",
                description = "Invite 5 verified creators using your unique referral code",
                xpReward = 200,
                coinReward = 150,
                progress = (referrals / 5f).coerceIn(0f, 1f),
                isUnlocked = referrals >= 5
            ),
            AchievementItem(
                id = "ach_100k_views",
                title = "100K Arena Club",
                description = "Surpass 100,000 total views across Shorts and Long Videos",
                xpReward = 300,
                coinReward = 250,
                progress = ((user?.totalViewsCount ?: 0L) / 100_000f).coerceIn(0f, 1f),
                isUnlocked = (user?.totalViewsCount ?: 0L) >= 100_000L
            )
        )
    }

    suspend fun isUsernameAvailable(username: String, excludeUserId: String = currentUserId): Boolean {
        val clean = username.trim().removePrefix("@").lowercase()
        if (clean.length < 3) return false
        val existing = dao.getUserByUsername(clean)
        return existing == null || existing.id == excludeUserId
    }

    suspend fun updateProfile(
        name: String,
        username: String,
        bio: String,
        email: String,
        phone: String,
        customAvatarUri: String?,
        customCoverUri: String?
    ): Result<UserEntity> {
        val cleanUsername = username.trim().removePrefix("@").lowercase()
        if (!isUsernameAvailable(cleanUsername)) {
            return Result.failure(IllegalArgumentException("Username @$cleanUsername is already taken. Choose a unique handle."))
        }
        val current = dao.getUserById(currentUserId) ?: return Result.failure(IllegalStateException("User not found"))
        val updated = current.copy(
            name = name.trim(),
            username = cleanUsername,
            bio = bio.trim(),
            email = email.trim(),
            phone = phone.trim(),
            customAvatarUri = customAvatarUri ?: current.customAvatarUri,
            customCoverUri = customCoverUri ?: current.customCoverUri
        )
        dao.upsertUser(updated)
        return Result.success(updated)
    }

    suspend fun toggleLike(post: FeedPostEntity) {
        val nowLiked = !post.isLiked
        val delta = if (nowLiked) 1 else -1
        dao.updatePost(
            post.copy(
                isLiked = nowLiked,
                likesCount = (post.likesCount + delta).coerceAtLeast(0)
            )
        )
        analytics.logEvent("like", mapOf("postId" to post.id, "liked" to nowLiked.toString()))
    }

    suspend fun toggleSave(post: FeedPostEntity) {
        val nowSaved = !post.isSaved
        val delta = if (nowSaved) 1 else -1
        dao.updatePost(
            post.copy(
                isSaved = nowSaved,
                savesCount = (post.savesCount + delta).coerceAtLeast(0)
            )
        )
    }

    suspend fun incrementShare(post: FeedPostEntity) {
        dao.updatePost(post.copy(sharesCount = post.sharesCount + 1))
        analytics.logEvent("share", mapOf("postId" to post.id))
    }

    suspend fun markNotInterested(post: FeedPostEntity) {
        dao.updatePost(post.copy(isNotInterested = true))
    }

    suspend fun updateVideoResumePosition(postId: String, positionSeconds: Int) {
        val post = dao.getPostById(postId) ?: return
        dao.updatePost(
            post.copy(
                resumePositionSeconds = positionSeconds,
                viewsCount = post.viewsCount + 1L,
                watchTimeSeconds = post.watchTimeSeconds + positionSeconds.coerceAtLeast(5)
            )
        )
        analytics.logEvent("video_view", mapOf("postId" to postId, "resumeSec" to positionSeconds.toString()))
    }

    suspend fun toggleFollowUser(targetUserId: String) {
        val target = dao.getUserById(targetUserId)
        val current = dao.getUserById(currentUserId)
        if (target != null && current != null) {
            val newFollow = !target.isFollowing
            val delta = if (newFollow) 1 else -1
            dao.upsertUser(
                target.copy(
                    isFollowing = newFollow,
                    followersCount = (target.followersCount + delta).coerceAtLeast(0)
                )
            )
            dao.upsertUser(
                current.copy(
                    followingCount = (current.followingCount + delta).coerceAtLeast(0)
                )
            )
            analytics.logEvent("follow", mapOf("targetUserId" to targetUserId, "following" to newFollow.toString()))
        }
    }

    suspend fun toggleBlockUser(targetUserId: String) {
        val target = dao.getUserById(targetUserId) ?: return
        dao.upsertUser(
            target.copy(
                isBlocked = !target.isBlocked,
                isFollowing = false
            )
        )
    }

    suspend fun toggleMuteUser(targetUserId: String) {
        val target = dao.getUserById(targetUserId) ?: return
        dao.upsertUser(target.copy(isMuted = !target.isMuted))
    }

    suspend fun respondToFriendRequest(request: FriendRequestEntity, accept: Boolean) {
        dao.upsertFriendRequest(request.copy(status = if (accept) "ACCEPTED" else "DECLINED"))
        if (accept) {
            val current = dao.getUserById(currentUserId)
            val sender = dao.getUserById(request.fromUserId)
            if (current != null) {
                dao.upsertUser(current.copy(friendsCount = current.friendsCount + 1, followersCount = current.followersCount + 1))
            }
            if (sender != null) {
                dao.upsertUser(sender.copy(isFriend = true, isFollowing = true))
            }
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_${UUID.randomUUID()}",
                    type = NotificationType.ACCEPTED_REQUEST.name,
                    title = "Friend Connection Confirmed",
                    body = "You and @${request.fromUsername} are now connected as friends on Vidoo.",
                    actorUsername = request.fromUsername
                )
            )
        }
    }

    suspend fun addComment(postId: String, content: String, parentCommentId: String? = null) {
        val user = dao.getUserById(currentUserId) ?: return
        val comment = CommentEntity(
            id = "cmt_${UUID.randomUUID()}",
            postId = postId,
            parentCommentId = parentCommentId,
            authorId = user.id,
            authorName = user.name,
            authorUsername = user.username,
            isAuthorVerified = user.isVerified,
            content = content.trim()
        )
        dao.insertComment(comment)
        val post = dao.getPostById(postId)
        if (post != null) {
            dao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        }
        analytics.logEvent("comment", mapOf("postId" to postId, "isReply" to (parentCommentId != null).toString()))
    }

    suspend fun toggleLikeComment(comment: CommentEntity) {
        val nowLiked = !comment.isLiked
        val delta = if (nowLiked) 1 else -1
        dao.insertComment(
            comment.copy(
                isLiked = nowLiked,
                likesCount = (comment.likesCount + delta).coerceAtLeast(0)
            )
        )
    }

    suspend fun deleteComment(comment: CommentEntity) {
        dao.deleteCommentById(comment.id)
        val post = dao.getPostById(comment.postId)
        if (post != null) {
            dao.updatePost(post.copy(commentsCount = (post.commentsCount - 1).coerceAtLeast(0)))
        }
    }

    suspend fun sendMessage(
        conversation: ConversationEntity,
        text: String,
        mediaType: String = "NONE",
        mediaUriOrRes: String = ""
    ) {
        val user = dao.getUserById(currentUserId) ?: return
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = "msg_${UUID.randomUUID()}",
            conversationId = conversation.id,
            senderId = user.id,
            senderName = user.name,
            textContent = text.trim(),
            mediaType = mediaType,
            mediaUriOrRes = mediaUriOrRes,
            isRead = true,
            createdAtTimestamp = now
        )
        dao.insertMessage(msg)
        dao.upsertConversation(
            conversation.copy(
                lastMessagePreview = if (mediaType != "NONE") "[$mediaType] ${text.trim()}" else text.trim(),
                unreadCount = 0,
                updatedAtTimestamp = now
            )
        )
    }

    suspend fun publishOrDraftPost(
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
        isDraft: Boolean
    ): FeedPostEntity {
        val user = dao.getUserById(currentUserId)
        val defaultMedia = when (contentType) {
            "SHORT_VIDEO" -> "img_short_neon_dancer_1790684791969"
            "LONG_VIDEO", "VIDEO" -> "img_video_tech_review_1790684807149"
            "PHOTO" -> "img_cover_cyber_studio_1790684775496"
            else -> ""
        }
        val post = FeedPostEntity(
            id = "post_${UUID.randomUUID()}",
            authorId = currentUserId,
            authorName = user?.name ?: "Alex Rivera",
            authorUsername = user?.username ?: "alexrivera",
            authorAvatarRes = user?.avatarResName ?: "img_avatar_creator_1790684817836",
            isAuthorVerified = user?.isVerified ?: true,
            contentType = contentType,
            title = title.trim().ifEmpty { caption.take(48) },
            caption = caption.trim(),
            hashtags = hashtags.trim().ifEmpty { "#Vidoo,#WatchShareConnect" },
            mentions = mentions.trim(),
            mediaResName = defaultMedia,
            customMediaUri = customMediaUri,
            durationSeconds = durationSeconds,
            aspectRatio = if (contentType == "SHORT_VIDEO") "9:16" else "16:9",
            visibility = visibility,
            locationTag = locationTag.trim(),
            musicTrackTitle = musicTrack?.let { "${it.title} — ${it.artist}" } ?: "",
            musicAttribution = musicTrack?.attributionText ?: "",
            isDraft = isDraft,
            isFollowingAuthor = true
        )
        dao.upsertPost(post)
        if (!isDraft && user != null) {
            val isVideo = contentType.contains("VIDEO")
            dao.upsertUser(
                user.copy(
                    postsCount = user.postsCount + 1,
                    videosCount = if (isVideo) user.videosCount + 1 else user.videosCount
                )
            )
            analytics.logEvent("upload", mapOf("type" to contentType, "visibility" to visibility))
        }
        return post
    }

    /**
     * Server-validated reward transaction simulator.
     * Generates a cryptographic SSV signature token before updating user's coin & XP ledger.
     */
    suspend fun grantServerVerifiedReward(
        title: String,
        activityType: String,
        coinsDelta: Int,
        xpDelta: Int,
        xpPerLevelBase: Int = 100
    ): RewardTransactionEntity {
        val ssvToken = "ssv_${activityType.lowercase()}_${UUID.randomUUID().toString().take(8)}"
        val tx = RewardTransactionEntity(
            id = "tx_${UUID.randomUUID()}",
            title = title,
            activityType = activityType,
            coinsDelta = coinsDelta,
            xpDelta = xpDelta,
            serverVerificationToken = ssvToken,
            status = "VERIFIED_SERVER_SIDE"
        )
        dao.insertRewardTransaction(tx)

        val user = dao.getUserById(currentUserId)
        if (user != null) {
            val totalXp = user.currentXp + xpDelta
            val levelsGained = totalXp / xpPerLevelBase
            val remainderXp = totalXp % xpPerLevelBase
            dao.upsertUser(
                user.copy(
                    coinBalance = (user.coinBalance + coinsDelta).coerceAtLeast(0),
                    level = user.level + levelsGained,
                    currentXp = remainderXp,
                    xpForNextLevel = xpPerLevelBase
                )
            )
        }

        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                type = NotificationType.REWARDS.name,
                title = "Server-Verified Vidoo Reward",
                body = "+$coinsDelta VIDOO COINS & +$xpDelta XP added ($title). Token: $ssvToken",
                actorUsername = "vidoo_treasury"
            )
        )

        if (activityType == "REWARDED_AD") {
            analytics.logEvent("rewarded_ad_completion", mapOf("coins" to coinsDelta.toString(), "token" to ssvToken))
        }
        return tx
    }

    suspend fun claimDailyChallenge(challenge: DailyChallengeEntity, xpBase: Int) {
        if (challenge.isClaimed || challenge.currentCount < challenge.targetCount) return
        dao.upsertDailyChallenge(challenge.copy(isClaimed = true))
        grantServerVerifiedReward(
            title = "Daily Challenge: ${challenge.title}",
            activityType = "DAILY_CHALLENGE",
            coinsDelta = challenge.coinReward,
            xpDelta = challenge.xpReward,
            xpPerLevelBase = xpBase
        )
    }

    suspend fun submitReport(
        targetType: String,
        targetId: String,
        targetPreview: String,
        category: ReportCategory,
        notes: String
    ) {
        val report = ReportEntity(
            id = "rep_${UUID.randomUUID()}",
            targetType = targetType,
            targetId = targetId,
            targetPreview = targetPreview.take(90),
            reporterId = currentUserId,
            category = category.name,
            notes = notes.trim().ifEmpty { category.description }
        )
        dao.upsertReport(report)
    }

    suspend fun moderateReport(report: ReportEntity, removeContent: Boolean) {
        val newStatus = if (removeContent) "CONTENT_REMOVED" else "RESTORED_DISMISSED"
        dao.upsertReport(report.copy(status = newStatus))
        if (removeContent && (report.targetType == "POST" || report.targetType == "VIDEO")) {
            val post = dao.getPostById(report.targetId)
            if (post != null) {
                dao.updatePost(post.copy(isRemovedByMod = true))
            }
        }
    }

    suspend fun adminUpdateUserStatus(userId: String, newStatus: String, verifyCreator: Boolean? = null) {
        val user = dao.getUserById(userId) ?: return
        dao.upsertUser(
            user.copy(
                accountStatus = newStatus,
                isVerified = verifyCreator ?: user.isVerified
            )
        )
    }

    suspend fun updateSubscriptionPlan(tierName: String) {
        val user = dao.getUserById(currentUserId) ?: return
        dao.upsertUser(user.copy(subscriptionTier = tierName))
        dao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                type = NotificationType.SUBSCRIPTION.name,
                title = "Vidoo $tierName Plan Activated",
                body = "Your Google Play Billing entitlement for Vidoo $tierName is now active.",
                actorUsername = "vidoo_billing"
            )
        )
        analytics.logEvent("subscription", mapOf("tier" to tierName))
    }

    suspend fun markNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun deleteCurrentUserAccount() {
        dao.deletePostsByAuthor(currentUserId)
        dao.deleteUserAccount(currentUserId)
    }
}
