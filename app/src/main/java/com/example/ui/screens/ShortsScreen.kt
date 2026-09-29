package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.FeedPostEntity
import com.example.domain.model.UiResultState
import com.example.ui.components.UiStateSectionBox
import com.example.ui.components.VerifiedCreatorBadge
import com.example.ui.components.VidooInteractiveVideoPlayer
import com.example.ui.components.VidooMediaThumbnail
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.viewmodel.VidooViewModel

@Composable
fun ShortsScreen(
    viewModel: VidooViewModel,
    shortsList: List<FeedPostEntity>,
    maxShortDurationSeconds: Int
) {
    val uiState = if (shortsList.isEmpty()) {
        UiResultState.Empty(
            message = "No Shorts available right now",
            subtitle = "Tap Create (+) to upload your first 9:16 vertical Short!"
        )
    } else {
        UiResultState.Success(shortsList)
    }

    UiStateSectionBox(
        state = uiState,
        onRetry = {},
        modifier = Modifier.fillMaxSize()
    ) { items ->
        val pagerState = rememberPagerState(pageCount = { items.size })

        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("shorts_vertical_pager")
        ) { pageIndex ->
            val shortPost = items[pageIndex]
            val isCurrentPage = pagerState.currentPage == pageIndex

            Box(modifier = Modifier.fillMaxSize()) {
                // Full-screen 9:16 vertical player
                VidooInteractiveVideoPlayer(
                    mediaResName = shortPost.mediaResName,
                    customMediaUri = shortPost.customMediaUri,
                    durationSeconds = shortPost.durationSeconds.coerceAtMost(maxShortDurationSeconds),
                    autoPlay = isCurrentPage,
                    showAdvancedControls = false,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Header Overlay: Vidoo Shorts badge + live view count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "VIDOO SHORTS",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = VidooCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• 9:16 HD",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RemoveRedEye,
                                contentDescription = "Views",
                                tint = VidooCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${formatCompactCount(shortPost.viewsCount)} views",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }

                // Right-side vertical interaction rail
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 52.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ShortActionButton(
                        icon = if (shortPost.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        label = formatCompactCount(shortPost.likesCount.toLong()),
                        tint = if (shortPost.isLiked) Color(0xFFFF4757) else Color.White,
                        onClick = { viewModel.toggleLikePost(shortPost) },
                        testTag = "short_like_${shortPost.id}"
                    )

                    ShortActionButton(
                        icon = Icons.AutoMirrored.Filled.Comment,
                        label = formatCompactCount(shortPost.commentsCount.toLong()),
                        tint = Color.White,
                        onClick = { viewModel.openCommentsForPost(shortPost) },
                        testTag = "short_comment_${shortPost.id}"
                    )

                    ShortActionButton(
                        icon = if (shortPost.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        label = formatCompactCount(shortPost.savesCount.toLong()),
                        tint = if (shortPost.isSaved) VidooCyan else Color.White,
                        onClick = { viewModel.toggleSavePost(shortPost) },
                        testTag = "short_save_${shortPost.id}"
                    )

                    ShortActionButton(
                        icon = Icons.Filled.Share,
                        label = formatCompactCount(shortPost.sharesCount.toLong()),
                        tint = Color.White,
                        onClick = { viewModel.sharePost(shortPost) },
                        testTag = "short_share_${shortPost.id}"
                    )

                    ShortActionButton(
                        icon = Icons.Filled.VisibilityOff,
                        label = "Hide",
                        tint = Color.White.copy(alpha = 0.85f),
                        onClick = { viewModel.markPostNotInterested(shortPost) },
                        testTag = "short_not_interested_${shortPost.id}"
                    )

                    ShortActionButton(
                        icon = Icons.Filled.Flag,
                        label = "Report",
                        tint = Color.White.copy(alpha = 0.85f),
                        onClick = {
                            viewModel.openReportDialog(
                                targetType = "VIDEO",
                                targetId = shortPost.id,
                                targetPreview = shortPost.title
                            )
                        },
                        testTag = "short_report_${shortPost.id}"
                    )
                }

                // Bottom-left Creator Metadata, Follow Button, Caption, Hashtags & Licensed Music
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.78f)
                        .padding(start = 16.dp, bottom = 36.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(2.dp, VidooCyan, CircleShape)
                        ) {
                            VidooMediaThumbnail(
                                mediaResName = shortPost.authorAvatarRes,
                                customUri = null,
                                contentDescription = shortPost.authorName,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "@${shortPost.authorUsername}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                if (shortPost.isAuthorVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedCreatorBadge(size = 15.dp)
                                }
                            }
                            Text(
                                text = shortPost.authorName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            color = if (shortPost.isFollowingAuthor) Color.White.copy(alpha = 0.2f) else VidooCyan,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .clickable { viewModel.toggleFollowCreator(shortPost.authorId) }
                                .testTag("short_follow_${shortPost.id}")
                        ) {
                            Text(
                                text = if (shortPost.isFollowingAuthor) "Following" else "Follow",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (shortPost.isFollowingAuthor) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = shortPost.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = shortPost.caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = shortPost.hashtags.replace(",", " "),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VidooCyan
                    )

                    if (shortPost.musicTrackTitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MusicNote,
                                    contentDescription = "Licensed Audio",
                                    tint = VidooPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${shortPost.musicTrackTitle} • ${shortPost.musicAttribution}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.48f),
            shape = CircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )
    }
}
