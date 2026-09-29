package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.CommentEntity
import com.example.data.local.ConversationEntity
import com.example.data.local.FeedPostEntity
import com.example.data.local.MessageEntity
import com.example.domain.model.CommentSortOrder
import com.example.ui.components.VerifiedCreatorBadge
import com.example.ui.components.VidooMediaThumbnail
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.theme.VidooSuccess
import com.example.ui.viewmodel.VidooViewModel

/**
 * Section 11: Comments & Replies Overlay with Pagination & Sorting
 */
@Composable
fun CommentsOverlaySheet(
    viewModel: VidooViewModel,
    post: FeedPostEntity,
    comments: List<CommentEntity>,
    sortOrder: CommentSortOrder,
    currentUserId: String,
    onClose: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<CommentEntity?>(null) }

    val topLevelComments = remember(comments) {
        comments.filter { it.parentCommentId == null }
    }
    val repliesByParent = remember(comments) {
        comments.filter { it.parentCommentId != null }.groupBy { it.parentCommentId }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header + Sort Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comments (${post.commentsCount})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = post.title.ifBlank { post.caption },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_comments_btn")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close comments")
                }
            }

            // Sorting Chips: Top vs Newest
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommentSortOrder.entries.forEach { order ->
                    FilterChip(
                        selected = sortOrder == order,
                        onClick = { viewModel.setCommentSortOrder(order) },
                        label = { Text(order.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VidooCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Paginated Comment Thread List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topLevelComments, key = { it.id }) { comment ->
                    Column {
                        CommentItemCard(
                            comment = comment,
                            isOwnComment = comment.authorId == currentUserId,
                            isReply = false,
                            onLike = { viewModel.toggleLikeComment(comment) },
                            onReply = { replyingTo = comment },
                            onDelete = { viewModel.deleteOwnComment(comment) },
                            onReport = {
                                viewModel.openReportDialog("COMMENT", comment.id, comment.content)
                            },
                            onBlock = {
                                viewModel.toggleBlockUser(comment.authorId, comment.authorUsername)
                            }
                        )

                        // Render Threaded Replies
                        val childReplies = repliesByParent[comment.id].orEmpty()
                        childReplies.forEach { reply ->
                            Spacer(modifier = Modifier.height(6.dp))
                            CommentItemCard(
                                comment = reply,
                                isOwnComment = reply.authorId == currentUserId,
                                isReply = true,
                                onLike = { viewModel.toggleLikeComment(reply) },
                                onReply = { replyingTo = comment },
                                onDelete = { viewModel.deleteOwnComment(reply) },
                                onReport = {
                                    viewModel.openReportDialog("COMMENT", reply.id, reply.content)
                                },
                                onBlock = {
                                    viewModel.toggleBlockUser(reply.authorId, reply.authorUsername)
                                },
                                modifier = Modifier.padding(start = 28.dp)
                            )
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { viewModel.loadMoreComments() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("load_more_comments_btn")
                    ) {
                        Text("Load More Comments (Firestore Cursor Pagination)")
                    }
                }
            }

            // Reply indicator + Input Composer
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (replyingTo != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Replying to @${replyingTo!!.authorUsername}",
                                style = MaterialTheme.typography.labelSmall,
                                color = VidooCyan
                            )
                            TextButton(onClick = { replyingTo = null }) {
                                Text("Cancel", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text(
                                    if (replyingTo != null) "Write a reply…" else "Add a comment…"
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    viewModel.submitComment(
                                        postId = post.id,
                                        text = commentText,
                                        parentCommentId = replyingTo?.id
                                    )
                                    commentText = ""
                                    replyingTo = null
                                }
                            },
                            modifier = Modifier
                                .background(VidooCyan, CircleShape)
                                .size(46.dp)
                                .testTag("send_comment_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send comment",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItemCard(
    comment: CommentEntity,
    isOwnComment: Boolean,
    isReply: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isReply) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${comment.authorUsername}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = VidooCyan
                    )
                    if (comment.isAuthorVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedCreatorBadge(size = 14.dp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (comment.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like comment",
                            tint = if (comment.isLiked) Color(0xFFFF4757) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = comment.likesCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onReply)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = "Reply",
                        tint = VidooCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reply", style = MaterialTheme.typography.labelSmall, color = VidooCyan)
                }

                if (isOwnComment) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onDelete)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete comment",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onReport)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Flag,
                            contentDescription = "Report comment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Report", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onBlock)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = "Block user",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Block", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * Section 12: Private Messaging Screen (1-to-1 Chat, Image/Video sharing, Online indicator, Read state)
 */
@Composable
fun MessagingScreen(
    viewModel: VidooViewModel,
    conversations: List<ConversationEntity>,
    activeConversation: ConversationEntity?,
    messages: List<MessageEntity>,
    currentUserId: String,
    onBack: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.sendChatMessage("Shared a photo", "IMAGE", uri.toString())
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.sendChatMessage("Shared a video clip", "VIDEO", uri.toString())
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (activeConversation == null) {
            // Conversation Inbox List
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
                            text = "Vidoo Messages",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Private 1-to-1 & Group-ready encrypted chat",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(conversations, key = { it.id }) { conv ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openConversation(conv) }
                                .testTag("conversation_item_${conv.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    ) {
                                        VidooMediaThumbnail(
                                            mediaResName = conv.peerAvatarRes,
                                            customUri = null,
                                            contentDescription = conv.peerName,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    // Online / Offline indicator
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(if (conv.isPeerOnline) VidooSuccess else Color.Gray)
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = conv.peerName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (conv.isPeerOnline) "• Online" else "• Offline",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (conv.isPeerOnline) VidooSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = conv.lastMessagePreview,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }

                                if (conv.unreadCount > 0) {
                                    Surface(
                                        color = VidooCyan,
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = conv.unreadCount.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Active 1-to-1 Chat Thread
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.closeConversation() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to inbox")
                        }
                        Column {
                            Text(
                                text = activeConversation.peerName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "@${activeConversation.peerUsername} • ${if (activeConversation.isPeerOnline) "Online now" else "Offline"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (activeConversation.isPeerOnline) VidooSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                viewModel.toggleBlockUser(activeConversation.peerUserId, activeConversation.peerUsername)
                            }
                        ) {
                            Icon(Icons.Filled.Block, contentDescription = "Block User")
                        }
                        IconButton(
                            onClick = {
                                viewModel.openReportDialog(
                                    "CONVERSATION",
                                    activeConversation.id,
                                    "Chat with @${activeConversation.peerUsername}"
                                )
                            }
                        ) {
                            Icon(Icons.Filled.Flag, contentDescription = "Report Conversation", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isMe = msg.senderId == currentUserId
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                color = if (isMe) VidooPurple else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (msg.mediaType != "NONE") {
                                        Box(
                                            modifier = Modifier
                                                .width(200.dp)
                                                .height(120.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        ) {
                                            VidooMediaThumbnail(
                                                mediaResName = msg.mediaUriOrRes,
                                                customUri = if (msg.mediaUriOrRes.startsWith("content://")) msg.mediaUriOrRes else null,
                                                contentDescription = "Chat media",
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    Text(
                                        text = msg.textContent,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DoneAll,
                                            contentDescription = "Read",
                                            tint = VidooCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (msg.isRead) "Read" else "Sent",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Composer with Image & Video Sharing
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            modifier = Modifier.testTag("chat_share_image_btn")
                        ) {
                            Icon(Icons.Filled.Image, contentDescription = "Share Image", tint = VidooCyan)
                        }
                        IconButton(
                            onClick = {
                                videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                            },
                            modifier = Modifier.testTag("chat_share_video_btn")
                        ) {
                            Icon(Icons.Filled.Videocam, contentDescription = "Share Video", tint = VidooPurple)
                        }
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Message @${activeConversation.peerUsername}…") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendChatMessage(messageInput)
                                    messageInput = ""
                                }
                            },
                            modifier = Modifier
                                .background(VidooCyan, CircleShape)
                                .size(44.dp)
                                .testTag("chat_send_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
