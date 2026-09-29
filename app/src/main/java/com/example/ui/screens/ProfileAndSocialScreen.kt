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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.FeedPostEntity
import com.example.data.local.FriendRequestEntity
import com.example.data.local.UserEntity
import com.example.domain.model.SecondaryDestination
import com.example.ui.components.VerifiedCreatorBadge
import com.example.ui.components.VidooMediaThumbnail
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.VidooCoinGold
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.viewmodel.VidooViewModel

@Composable
fun ProfileAndSocialScreen(
    viewModel: VidooViewModel,
    user: UserEntity?,
    allPosts: List<FeedPostEntity>,
    savedPosts: List<FeedPostEntity>,
    otherUsers: List<UserEntity>,
    friendRequests: List<FriendRequestEntity>
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Posts, 1: Videos, 2: Media, 3: Saved
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSocialNetworkSheet by remember { mutableStateOf(false) }

    val myPosts = remember(allPosts, user?.id) {
        allPosts.filter { it.authorId == (user?.id ?: "user_current_vidoo") }
    }
    val myVideos = remember(allPosts) {
        allPosts.filter { it.contentType.contains("VIDEO") }
    }
    val myMedia = remember(allPosts) {
        allPosts.filter { it.contentType == "PHOTO" || it.contentType.contains("VIDEO") }
    }

    val displayedList = when (selectedTabIndex) {
        0 -> myPosts.ifEmpty { allPosts.take(3) }
        1 -> myVideos
        2 -> myMedia
        else -> savedPosts
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen_list"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Cover Photo + Profile Photo + Header Info
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Cover Photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                ) {
                    VidooMediaThumbnail(
                        mediaResName = user?.coverResName ?: "img_cover_cyber_studio_1790684775496",
                        customUri = user?.customCoverUri,
                        contentDescription = "Profile Cover Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                                )
                            )
                    )
                    IconButton(
                        onClick = { viewModel.navigateToSecondary(SecondaryDestination.SETTINGS) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("profile_settings_btn")
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }

                // Avatar + Level Badge
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp, start = 16.dp, end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, VidooCyan, CircleShape)
                            ) {
                                VidooMediaThumbnail(
                                    mediaResName = user?.avatarResName ?: "img_avatar_creator_1790684817836",
                                    customUri = user?.customAvatarUri,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Surface(
                                color = VidooPurple,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(y = 4.dp)
                            ) {
                                Text(
                                    text = "LVL ${user?.level ?: 5}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Action Buttons: Edit Profile, Share Profile, Message, Network
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showEditProfileDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VidooCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("edit_profile_btn")
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            }

                            IconButton(
                                onClick = { viewModel.showBanner("Profile link copied: https://vidoo.app/@${user?.username ?: "alexrivera"}") },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .size(40.dp)
                                    .testTag("share_profile_btn")
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = "Share Profile", tint = VidooCyan)
                            }

                            IconButton(
                                onClick = { viewModel.navigateToSecondary(SecondaryDestination.MESSAGES) },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name, Username, Verification Badge, Subscription Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user?.name ?: "Alex Rivera",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (user?.isVerified == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedCreatorBadge(size = 18.dp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = VidooCoinGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = user?.subscriptionTier ?: "CREATOR",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = VidooCoinGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "@${user?.username ?: "alexrivera"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VidooCyan
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user?.bio ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Level & XP Progress Bar (Section 9 & 30)
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Creator Level ${user?.level ?: 5} • Streak: ${user?.streakDays ?: 7} Days",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = VidooCyan
                                )
                                Text(
                                    text = "${user?.currentXp ?: 75} / ${user?.xpForNextLevel ?: 100} XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = {
                                    val cur = (user?.currentXp ?: 75).toFloat()
                                    val max = (user?.xpForNextLevel ?: 100).coerceAtLeast(1).toFloat()
                                    (cur / max).coerceIn(0f, 1f)
                                },
                                color = VidooCyan,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6 Profile Statistics: Followers, Following, Friends, Posts, Videos, Views
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSocialNetworkSheet = !showSocialNetworkSheet }
                            .testTag("profile_stats_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem("Followers", formatCompactCount((user?.followersCount ?: 14820).toLong()))
                            ProfileStatItem("Following", formatCompactCount((user?.followingCount ?: 342).toLong()))
                            ProfileStatItem("Friends", (user?.friendsCount ?: 128).toString())
                            ProfileStatItem("Posts", (user?.postsCount ?: 24).toString())
                            ProfileStatItem("Videos", (user?.videosCount ?: 16).toString())
                            ProfileStatItem("Views", formatCompactCount(user?.totalViewsCount ?: 284900L))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Links to Creator Studio, Rewards & Social Connections
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showSocialNetworkSheet = !showSocialNetworkSheet },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("manage_connections_btn")
                        ) {
                            Icon(Icons.Filled.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Friends & Requests (${friendRequests.size})")
                        }
                        OutlinedButton(
                            onClick = { viewModel.navigateToSecondary(SecondaryDestination.CREATOR_STUDIO) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_creator_studio_btn")
                        ) {
                            Text("Creator Studio", color = VidooCyan)
                        }
                    }
                }
            }
        }

        // Expandable Social Connections & Friend Requests Section (Section 10)
        if (showSocialNetworkSheet) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Social Graph: Friend Requests & Creators",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (friendRequests.isNotEmpty()) {
                            friendRequests.forEach { req ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${req.fromName} (@${req.fromUsername})",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${req.mutualFriendsCount} mutual friends",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(
                                            onClick = { viewModel.respondFriendRequest(req, true) },
                                            modifier = Modifier
                                                .background(VidooCyan, CircleShape)
                                                .size(34.dp)
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = "Accept", tint = Color.Black)
                                        }
                                        IconButton(
                                            onClick = { viewModel.respondFriendRequest(req, false) },
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                                                .size(34.dp)
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = "Decline")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        otherUsers.forEach { peer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = peer.name,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (peer.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            VerifiedCreatorBadge(size = 14.dp)
                                        }
                                    }
                                    Text(
                                        text = "@${peer.username} • ${formatCompactCount(peer.followersCount.toLong())} followers",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    TextButton(onClick = { viewModel.toggleFollowCreator(peer.id) }) {
                                        Text(
                                            text = if (peer.isFollowing) "Unfollow" else "Follow",
                                            color = VidooCyan
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleMuteUser(peer.id, peer.username) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.VolumeOff,
                                            contentDescription = "Mute user",
                                            tint = if (peer.isMuted) VidooCoinGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleBlockUser(peer.id, peer.username) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Block,
                                            contentDescription = "Block user",
                                            tint = if (peer.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.openReportDialog("USER", peer.id, "@${peer.username}")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Flag,
                                            contentDescription = "Report user",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Profile Tabs: Posts, Videos, Media, Saved
        item {
            Spacer(modifier = Modifier.height(12.dp))
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = VidooCyan,
                modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp))
            ) {
                listOf("Posts", "Videos", "Media", "Saved").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTabIndex == idx,
                        onClick = { selectedTabIndex = idx },
                        text = { Text(title) },
                        modifier = Modifier.testTag("profile_tab_${title.lowercase()}")
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(displayedList, key = { "prof_${selectedTabIndex}_${it.id}" }) { post ->
            VidooFeedPostCard(
                post = post,
                onLike = { viewModel.toggleLikePost(post) },
                onComment = { viewModel.openCommentsForPost(post) },
                onShare = { viewModel.sharePost(post) },
                onSave = { viewModel.toggleSavePost(post) },
                onFollowToggle = { viewModel.toggleFollowCreator(post.authorId) },
                onReport = {
                    viewModel.openReportDialog("POST", post.id, post.title)
                },
                onNotInterested = { viewModel.markPostNotInterested(post) },
                onOpenFullVideo = { viewModel.openLongVideoPlayer(post) }
            )
        }
    }

    if (showEditProfileDialog && user != null) {
        EditProfileModal(
            user = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, uname, bio, email, phone, avatarUri, coverUri, onError ->
                viewModel.saveProfileEdits(name, uname, bio, email, phone, avatarUri, coverUri) { result ->
                    result.onSuccess { showEditProfileDialog = false }
                    result.onFailure { onError(it.message ?: "Unable to update profile") }
                }
            }
        )
    }
}

@Composable
private fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EditProfileModal(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String?, String?, (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var avatarUri by remember { mutableStateOf(user.customAvatarUri) }
    var coverUri by remember { mutableStateOf(user.customCoverUri) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) avatarUri = uri.toString() }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) coverUri = uri.toString() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Vidoo Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            avatarPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Avatar")
                    }
                    OutlinedButton(
                        onClick = {
                            coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cover")
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.lowercase().replace(" ", "_") },
                    label = { Text("Unique Username") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    maxLines = 3
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, username, bio, email, phone, avatarUri, coverUri) { err ->
                        errorMessage = err
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = VidooCyan, contentColor = Color.Black)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
