package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VidooDao {

    // Users & Social
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id != :currentUserId ORDER BY followersCount DESC")
    fun observeOtherUsers(currentUserId: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUsers(users: List<UserEntity>)

    // Feed Posts, Shorts & Long Videos
    @Query("SELECT * FROM feed_posts WHERE isDraft = 0 AND isNotInterested = 0 AND isRemovedByMod = 0 ORDER BY createdAtTimestamp DESC")
    fun observeAllPublishedPosts(): Flow<List<FeedPostEntity>>

    @Query("SELECT * FROM feed_posts WHERE isDraft = 1 ORDER BY createdAtTimestamp DESC")
    fun observeDraftPosts(): Flow<List<FeedPostEntity>>

    @Query("SELECT * FROM feed_posts WHERE isSaved = 1 AND isRemovedByMod = 0 ORDER BY createdAtTimestamp DESC")
    fun observeSavedPosts(): Flow<List<FeedPostEntity>>

    @Query("SELECT * FROM feed_posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: String): FeedPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPost(post: FeedPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosts(posts: List<FeedPostEntity>)

    @Update
    suspend fun updatePost(post: FeedPostEntity)

    @Query("DELETE FROM feed_posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    // Comments with Pagination & Sorting
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY likesCount DESC, createdAtTimestamp DESC LIMIT :limit")
    fun observeCommentsTop(postId: String, limit: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAtTimestamp DESC LIMIT :limit")
    fun observeCommentsNewest(postId: String, limit: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("SELECT * FROM comments WHERE id = :commentId LIMIT 1")
    suspend fun getCommentById(commentId: String): CommentEntity?

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)

    // Messaging & Conversations
    @Query("SELECT * FROM conversations ORDER BY updatedAtTimestamp DESC")
    fun observeConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAtTimestamp ASC")
    fun observeMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversations(conversations: List<ConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY createdAtTimestamp DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // Rewards & Daily Challenges
    @Query("SELECT * FROM reward_transactions ORDER BY createdAtTimestamp DESC")
    fun observeRewardTransactions(): Flow<List<RewardTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardTransaction(tx: RewardTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardTransactions(txs: List<RewardTransactionEntity>)

    @Query("SELECT * FROM daily_challenges")
    fun observeDailyChallenges(): Flow<List<DailyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyChallenges(challenges: List<DailyChallengeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyChallenge(challenge: DailyChallengeEntity)

    // Moderation & Reports
    @Query("SELECT * FROM reports ORDER BY createdAtTimestamp DESC")
    fun observeReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReport(report: ReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReports(reports: List<ReportEntity>)

    // Friend Requests
    @Query("SELECT * FROM friend_requests WHERE status = 'PENDING' ORDER BY createdAtTimestamp DESC")
    fun observePendingFriendRequests(): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFriendRequests(requests: List<FriendRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFriendRequest(request: FriendRequestEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    // Account Deletion (Play Store compliance)
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserAccount(userId: String)

    @Query("DELETE FROM feed_posts WHERE authorId = :userId")
    suspend fun deletePostsByAuthor(userId: String)
}
