package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val username: String,
    val email: String,
    val phone: String = "",
    val bio: String,
    val avatarResName: String = "img_avatar_creator_1790684817836",
    val customAvatarUri: String? = null,
    val coverResName: String = "img_cover_cyber_studio_1790684775496",
    val customCoverUri: String? = null,
    val isVerified: Boolean = false,
    val isEmailVerified: Boolean = true,
    val level: Int = 4,
    val currentXp: Int = 65,
    val xpForNextLevel: Int = 100,
    val followersCount: Int = 1480,
    val followingCount: Int = 312,
    val friendsCount: Int = 86,
    val postsCount: Int = 18,
    val videosCount: Int = 12,
    val totalViewsCount: Long = 48920L,
    val coinBalance: Int = 420,
    val streakDays: Int = 5,
    val referralCode: String = "VIDOO77X",
    val invitedCount: Int = 7,
    val eligibleReferrals: Int = 4,
    val subscriptionTier: String = "FREE",
    val isFollowing: Boolean = false,
    val isFriend: Boolean = false,
    val isBlocked: Boolean = false,
    val isMuted: Boolean = false,
    val accountStatus: String = "ACTIVE", // ACTIVE, SUSPENDED, BANNED
    val isMonetizationEnabled: Boolean = true,
    val twoFactorEnabled: Boolean = false
)

@Entity(tableName = "feed_posts")
data class FeedPostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarRes: String,
    val isAuthorVerified: Boolean,
    val contentType: String, // TEXT, PHOTO, VIDEO, SHORT_VIDEO, LONG_VIDEO
    val title: String = "",
    val caption: String,
    val hashtags: String, // comma-separated
    val mentions: String = "",
    val mediaResName: String = "",
    val customMediaUri: String? = null,
    val durationSeconds: Int = 0,
    val aspectRatio: String = "16:9", // 9:16 for Shorts, 16:9 for Long Videos
    val resolutionOptions: String = "360p,720p,1080p HD,4K",
    val visibility: String = "PUBLIC", // PUBLIC, FOLLOWERS, PRIVATE
    val locationTag: String = "",
    val musicTrackTitle: String = "",
    val musicAttribution: String = "",
    val category: String = "Tech & Creativity",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val savesCount: Int = 0,
    val viewsCount: Long = 0L,
    val watchTimeSeconds: Long = 0L,
    val completionRate: Float = 0.78f,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowingAuthor: Boolean = false,
    val isDraft: Boolean = false,
    val isNotInterested: Boolean = false,
    val isRemovedByMod: Boolean = false,
    val resumePositionSeconds: Int = 0,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val parentCommentId: String? = null, // non-null for replies
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val isAuthorVerified: Boolean = false,
    val content: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val peerUserId: String,
    val peerName: String,
    val peerUsername: String,
    val peerAvatarRes: String,
    val isPeerOnline: Boolean,
    val lastMessagePreview: String,
    val unreadCount: Int = 0,
    val isBlocked: Boolean = false,
    val updatedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val textContent: String,
    val mediaType: String = "NONE", // NONE, IMAGE, VIDEO
    val mediaUriOrRes: String = "",
    val isRead: Boolean = true,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String, // NotificationType name
    val title: String,
    val body: String,
    val actorUsername: String = "",
    val targetId: String = "",
    val isRead: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reward_transactions")
data class RewardTransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val activityType: String, // DAILY_LOGIN, DAILY_CHALLENGE, REWARDED_AD, REFERRAL, ACHIEVEMENT
    val coinsDelta: Int,
    val xpDelta: Int,
    val serverVerificationToken: String,
    val status: String = "VERIFIED_SERVER_SIDE",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val coinReward: Int,
    val xpReward: Int,
    val isClaimed: Boolean = false
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val targetType: String, // POST, VIDEO, COMMENT, USER, CONVERSATION
    val targetId: String,
    val targetPreview: String,
    val reporterId: String,
    val category: String, // ReportCategory
    val notes: String,
    val status: String = "PENDING_REVIEW", // PENDING_REVIEW, REMOVED, RESTORED
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val fromUserId: String,
    val fromName: String,
    val fromUsername: String,
    val fromAvatarRes: String,
    val mutualFriendsCount: Int,
    val status: String = "PENDING", // PENDING, ACCEPTED, DECLINED
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
