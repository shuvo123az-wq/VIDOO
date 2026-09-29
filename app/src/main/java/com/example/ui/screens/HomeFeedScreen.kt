package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.FeedPostEntity
import com.example.data.local.UserEntity
import com.example.domain.model.FeedCategory
import com.example.domain.model.FeatureFlags
import com.example.domain.model.MainNavTab
import com.example.domain.model.RemoteAdConfig
import com.example.domain.model.SecondaryDestination
import com.example.domain.model.UiResultState
import com.example.ui.components.UiStateSectionBox
import com.example.ui.components.VerifiedCreatorBadge
import com.example.ui.components.VidooBrandHeader
import com.example.ui.components.VidooCoinPill
import com.example.ui.components.VidooInteractiveVideoPlayer
import com.example.ui.components.VidooMediaThumbnail
import com.example.ui.components.VidooNativeAdSlot
import com.example.ui.components.formatCompactCount
import com.example.ui.components.formatDuration
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.viewmodel.VidooViewModel

@Composable
fun HomeFeedScreen(
    viewModel: VidooViewModel,
    currentUser: UserEntity?,
    posts: List<FeedPostEntity>,
    longVideos: List<FeedPostEntity>,
    selectedCategory: FeedCategory,
    unreadNotificationsCount: Int,
    unreadMessagesCount: Int,
    featureFlags: FeatureFlags,
    adConfig: RemoteAdConfig,
    systemAnnouncement: String,
    onOpenDrawer: () -> Unit
) {
    val isPremium = currentUser?.subscriptionTier != "FREE"
    val uiState = if (posts.isEmpty()) {
        UiResultState.Empty(
            message = "No posts in ${selectedCategory.label} yet",
            subtitle = "Follow more creators or tap Create (+) to publish your own video or post."
        )
    } else {
        UiResultState.Success(posts)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar: Logo, Search, Messages, Notifications, Coin Pill, Profile Avatar
        Surface(
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("open_side_menu_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Open Vidoo Side Menu",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    VidooBrandHeader(showTagline = false)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (featureFlags.enableRewards) {
                        VidooCoinPill(
                            coins = currentUser?.coinBalance ?: 0,
                            onClick = { viewModel.navigateToSecondary(SecondaryDestination.REWARDS) }
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateToSecondary(SecondaryDestination.SEARCH) },
                        modifier = Modifier.testTag("top_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Vidoo",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (featureFlags.enableMessaging) {
                        IconButton(
                            onClick = { viewModel.navigateToSecondary(SecondaryDestination.MESSAGES) },
                            modifier = Modifier.testTag("top_messages_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadMessagesCount > 0) {
                                        Badge(containerColor = VidooCyan, contentColor = Color.Black) {
                                            Text(unreadMessagesCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Direct Messages",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { viewModel.selectMainTab(MainNavTab.NOTIFICATIONS) },
                        modifier = Modifier.testTag("top_notifications_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(containerColor = VidooPurple, contentColor = Color.White) {
                                        Text(unreadNotificationsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, VidooCyan, CircleShape)
                            .clickable { viewModel.selectMainTab(MainNavTab.PROFILE) }
                            .testTag("top_profile_avatar_btn")
                    ) {
                        VidooMediaThumbnail(
                            mediaResName = currentUser?.avatarResName ?: "img_avatar_creator_1790684817836",
                            customUri = currentUser?.customAvatarUri,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Feed Category Filter Chips: For You, Following, Trending, Recent, Shorts
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(FeedCategory.entries) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectFeedCategory(category) },
                    label = {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VidooCyan,
                        selectedLabelColor = Color(0xFF001F24),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("feed_chip_${category.name.lowercase()}")
                )
            }
        }

        // Feed Content
        UiStateSectionBox(
            state = uiState,
            onRetry = { viewModel.selectFeedCategory(FeedCategory.FOR_YOU) },
            modifier = Modifier.fillMaxSize()
        ) { feedList ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_feed_list"),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // System Announcement Banner if active
                if (systemAnnouncement.isNotBlank()) {
                    item(key = "announcement_banner") {
                        Surface(
                            color = VidooPurple.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clickable { viewModel.navigateToSecondary(SecondaryDestination.DAILY_CHALLENGE) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartDisplay,
                                    contentDescription = null,
                                    tint = VidooCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = systemAnnouncement,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Featured Long Videos Rail
                if (featureFlags.enableLongVideos && longVideos.isNotEmpty()) {
                    item(key = "featured_long_videos_rail") {
                        Column(modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Featured Long Videos",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                TextButton(
                                    onClick = { viewModel.navigateToSecondary(SecondaryDestination.LONG_VIDEOS) },
                                    modifier = Modifier.testTag("see_all_long_videos_btn")
                                ) {
                                    Text("Explore All", color = VidooCyan)
                                }
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(longVideos, key = { "rail_${it.id}" }) { video ->
                                    FeaturedLongVideoCard(
                                        post = video,
                                        onClick = { viewModel.openLongVideoPlayer(video) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Feed Posts + Centralized AdService Native Placements
                itemsIndexed(feedList, key = { _, item -> item.id }) { index, post ->
                    VidooFeedPostCard(
                        post = post,
                        onLike = { viewModel.toggleLikePost(post) },
                        onComment = { viewModel.openCommentsForPost(post) },
                        onShare = { viewModel.sharePost(post) },
                        onSave = { viewModel.toggleSavePost(post) },
                        onFollowToggle = { viewModel.toggleFollowCreator(post.authorId) },
                        onReport = {
                            viewModel.openReportDialog(
                                targetType = if (post.contentType.contains("VIDEO")) "VIDEO" else "POST",
                                targetId = post.id,
                                targetPreview = post.title.ifBlank { post.caption }
                            )
                        },
                        onNotInterested = { viewModel.markPostNotInterested(post) },
                        onOpenFullVideo = {
                            if (post.contentType == "SHORT_VIDEO") {
                                viewModel.selectMainTab(MainNavTab.SHORTS)
                            } else {
                                viewModel.openLongVideoPlayer(post)
                            }
                        }
                    )

                    if (viewModel.repository.adService.shouldShowNativeAdAtIndex(index, adConfig, isPremium)) {
                        VidooNativeAdSlot(
                            adConfig = adConfig,
                            placementLabel = "HOME FEED SPONSORED",
                            onWatchRewardedAd = {
                                viewModel.navigateToSecondary(SecondaryDestination.REWARDS)
                                viewModel.triggerWatchAdAndEarn()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedLongVideoCard(
    post: FeedPostEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(270.dp)
            .clickable(onClick = onClick)
            .testTag("featured_long_video_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
            ) {
                VidooMediaThumbnail(
                    mediaResName = post.mediaResName,
                    customUri = post.customMediaUri,
                    contentDescription = post.title,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = formatDuration(post.durationSeconds),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (post.resumePositionSeconds > 0) {
                    Surface(
                        color = VidooCyan.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Resume ${formatDuration(post.resumePositionSeconds)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${post.authorUsername}",
                        style = MaterialTheme.typography.labelSmall,
                        color = VidooCyan
                    )
                    if (post.isAuthorVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedCreatorBadge(size = 13.dp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${formatCompactCount(post.viewsCount)} views",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun VidooFeedPostCard(
    post: FeedPostEntity,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onFollowToggle: () -> Unit,
    onReport: () -> Unit,
    onNotInterested: () -> Unit,
    onOpenFullVideo: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("feed_post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, VidooCyan, CircleShape)
                    ) {
                        VidooMediaThumbnail(
                            mediaResName = post.authorAvatarRes,
                            customUri = null,
                            contentDescription = post.authorName,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.isAuthorVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedCreatorBadge(size = 15.dp)
                            }
                        }
                        Text(
                            text = "@${post.authorUsername} • ${post.category}${if (post.locationTag.isNotBlank()) " • ${post.locationTag}" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (post.isFollowingAuthor) MaterialTheme.colorScheme.surfaceVariant else VidooCyan.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clickable(onClick = onFollowToggle)
                            .testTag("follow_btn_${post.id}")
                    ) {
                        Text(
                            text = if (post.isFollowingAuthor) "Following" else "+ Follow",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (post.isFollowingAuthor) MaterialTheme.colorScheme.onSurfaceVariant else VidooCyan,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Post options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Not Interested") },
                                leadingIcon = { Icon(Icons.Filled.VisibilityOff, contentDescription = null) },
                                onClick = {
                                    showOverflowMenu = false
                                    onNotInterested()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Report Content") },
                                leadingIcon = { Icon(Icons.Filled.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showOverflowMenu = false
                                    onReport()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title & Caption
            if (post.title.isNotBlank()) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            if (post.hashtags.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.hashtags.replace(",", "  "),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = VidooCyan
                )
            }

            // Media Attachment (Photo, Short Video, or Long Video)
            if (post.contentType != "TEXT") {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (post.contentType == "SHORT_VIDEO") 280.dp else 210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onOpenFullVideo)
                ) {
                    if (post.contentType.contains("VIDEO")) {
                        VidooInteractiveVideoPlayer(
                            mediaResName = post.mediaResName,
                            customMediaUri = post.customMediaUri,
                            durationSeconds = post.durationSeconds,
                            initialResumeSeconds = post.resumePositionSeconds,
                            autoPlay = false,
                            showAdvancedControls = true,
                            onToggleFullscreen = onOpenFullVideo,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        VidooMediaThumbnail(
                            mediaResName = post.mediaResName,
                            customUri = post.customMediaUri,
                            contentDescription = post.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Licensed Music Attribution Credit Bar
            if (post.musicTrackTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = VidooCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.musicTrackTitle} • ${post.musicAttribution}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))

            // Post Action Bar: Like, Comment, Share, Save, Views
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = onLike)
                            .padding(vertical = 4.dp)
                            .testTag("like_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color(0xFFFF4757) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatCompactCount(post.likesCount.toLong()),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = onComment)
                            .padding(vertical = 4.dp)
                            .testTag("comment_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(21.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatCompactCount(post.commentsCount.toLong()),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = onShare)
                            .padding(vertical = 4.dp)
                            .testTag("share_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(21.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatCompactCount(post.sharesCount.toLong()),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${formatCompactCount(post.viewsCount)} views",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("save_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Save Post",
                            tint = if (post.isSaved) VidooCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section 7: Full Long-Video Player & Catalog Screen
 */
@Composable
fun LongVideoPlayerOverlay(
    post: FeedPostEntity,
    relatedVideos: List<FeedPostEntity>,
    onClose: (Int) -> Unit,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onFollow: () -> Unit,
    onSelectRelated: (FeedPostEntity) -> Unit
) {
    var trackedSeconds by remember(post.id) { mutableIntStateOf(post.resumePositionSeconds) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 16:9 Full-width Interactive Player with Speed, Quality & Resume
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                VidooInteractiveVideoPlayer(
                    mediaResName = post.mediaResName,
                    customMediaUri = post.customMediaUri,
                    durationSeconds = post.durationSeconds,
                    initialResumeSeconds = post.resumePositionSeconds,
                    autoPlay = true,
                    showAdvancedControls = true,
                    onProgressChanged = { trackedSeconds = it },
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { onClose(trackedSeconds) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .testTag("close_long_video_btn")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close player", tint = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${formatCompactCount(post.viewsCount)} views • Category: ${post.category} • Cloud Storage HLS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = post.caption,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, VidooCyan, CircleShape)
                            ) {
                                VidooMediaThumbnail(
                                    mediaResName = post.authorAvatarRes,
                                    customUri = null,
                                    contentDescription = post.authorName,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = post.authorName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (post.isAuthorVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VerifiedCreatorBadge()
                                    }
                                }
                                Text(
                                    text = "@${post.authorUsername}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VidooCyan
                                )
                            }
                        }

                        Button(
                            onClick = onFollow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (post.isFollowingAuthor) MaterialTheme.colorScheme.surfaceVariant else VidooCyan,
                                contentColor = if (post.isFollowingAuthor) MaterialTheme.colorScheme.onSurface else Color.Black
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(if (post.isFollowingAuthor) "Following" else "Follow Creator")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onLike) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = if (post.isLiked) Color(0xFFFF4757) else VidooCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${formatCompactCount(post.likesCount.toLong())} Likes")
                        }
                        TextButton(onClick = onComment) {
                            Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null, tint = VidooCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${post.commentsCount} Comments")
                        }
                        TextButton(onClick = onShare) {
                            Icon(Icons.Filled.Share, contentDescription = null, tint = VidooCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share")
                        }
                        TextButton(onClick = onSave) {
                            Icon(
                                imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null,
                                tint = VidooCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (post.isSaved) "Saved" else "Save")
                        }
                    }
                    HorizontalDivider()
                }

                item {
                    Text(
                        text = "Up Next in Long Videos",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(relatedVideos.filter { it.id != post.id }, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRelated(item) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(68.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            ) {
                                VidooMediaThumbnail(
                                    mediaResName = item.mediaResName,
                                    customUri = item.customMediaUri,
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Icon(
                                    imageVector = Icons.Filled.PlayCircleFilled,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "@${item.authorUsername} • ${formatCompactCount(item.viewsCount)} views",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
