package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.FeedPostEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.UserEntity
import com.example.domain.model.NotificationPreferences
import com.example.ui.components.VerifiedCreatorBadge
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.VidooCoinGold
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.viewmodel.VidooViewModel

/**
 * Section 13: Notifications Screen with FCM Categories & Settings
 */
@Composable
fun NotificationsScreen(
    viewModel: VidooViewModel,
    notifications: List<NotificationEntity>,
    prefs: NotificationPreferences
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showPrefsDialog by remember { mutableStateOf(false) }

    val filtered = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "SOCIAL" -> notifications.filter {
                it.type in listOf("NEW_FOLLOWER", "FRIEND_REQUEST", "ACCEPTED_REQUEST", "LIKE", "COMMENT", "REPLY", "MENTION", "NEW_MESSAGE")
            }
            "REWARDS" -> notifications.filter { it.type in listOf("REWARDS", "DAILY_CHALLENGE", "SUBSCRIPTION") }
            "SYSTEM" -> notifications.filter { it.type in listOf("CREATOR_UPLOAD", "SYSTEM_ANNOUNCEMENT") }
            else -> notifications
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Firebase Cloud Messaging (FCM) real-time alerts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { showPrefsDialog = true },
                modifier = Modifier.testTag("notification_prefs_btn")
            ) {
                Icon(Icons.Filled.Tune, contentDescription = "Notification Preferences", tint = VidooCyan)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL" to "All", "SOCIAL" to "Social", "REWARDS" to "Rewards", "SYSTEM" to "Updates").forEach { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VidooCyan,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            tint = if (item.type == "REWARDS") VidooCoinGold else VidooCyan,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPrefsDialog) {
        AlertDialog(
            onDismissRequest = { showPrefsDialog = false },
            title = { Text("Notification Categories") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NotifSwitchRow("Social Activity (Likes, Comments, Follows)", prefs.socialActivity) {
                        viewModel.updateNotificationPrefs(prefs.copy(socialActivity = it))
                    }
                    NotifSwitchRow("New Creator Content & Uploads", prefs.newContent) {
                        viewModel.updateNotificationPrefs(prefs.copy(newContent = it))
                    }
                    NotifSwitchRow("Vidoo Coins & Rewards", prefs.rewards) {
                        viewModel.updateNotificationPrefs(prefs.copy(rewards = it))
                    }
                    NotifSwitchRow("Daily Challenges & Streak Alerts", prefs.dailyChallenge) {
                        viewModel.updateNotificationPrefs(prefs.copy(dailyChallenge = it))
                    }
                    NotifSwitchRow("Subscription & Promotional Offers", prefs.offers) {
                        viewModel.updateNotificationPrefs(prefs.copy(offers = it))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrefsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VidooCyan, contentColor = Color.Black)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun NotifSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/**
 * Section 22: Global Search Screen (Users, Creators, Videos, Posts, Hashtags)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalSearchScreen(
    viewModel: VidooViewModel,
    query: String,
    recentSearches: List<String>,
    trendingSearches: List<String>,
    users: List<UserEntity>,
    posts: List<FeedPostEntity>,
    onBack: () -> Unit
) {
    var selectedScope by remember { mutableStateOf("ALL") }

    val matchingUsers = remember(query, users) {
        if (query.isBlank()) users
        else users.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.username.contains(query, ignoreCase = true) ||
                it.bio.contains(query, ignoreCase = true)
        }
    }

    val matchingPosts = remember(query, posts, selectedScope) {
        posts.filter { post ->
            val qMatch = query.isBlank() ||
                post.title.contains(query, ignoreCase = true) ||
                post.caption.contains(query, ignoreCase = true) ||
                post.hashtags.contains(query, ignoreCase = true) ||
                post.authorUsername.contains(query, ignoreCase = true)
            val scopeMatch = when (selectedScope) {
                "VIDEOS" -> post.contentType.contains("VIDEO")
                "POSTS" -> !post.contentType.contains("VIDEO")
                "HASHTAGS" -> post.hashtags.isNotBlank()
                else -> true
            }
            qMatch && scopeMatch
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search users, creators, videos, #hashtags…") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = VidooCyan) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("global_search_input")
                )
            }

            // Search Scopes: All, Users & Creators, Videos, Posts, Hashtags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All",
                    "CREATORS" to "Creators",
                    "VIDEOS" to "Videos",
                    "POSTS" to "Posts",
                    "HASHTAGS" to "Hashtags"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedScope == key,
                        onClick = { selectedScope = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VidooCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (query.isBlank()) {
                    // Recent Searches
                    if (recentSearches.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.History, contentDescription = null, tint = VidooCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Recent Searches", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                TextButton(onClick = { viewModel.clearRecentSearches() }) {
                                    Text("Clear", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                recentSearches.forEach { term ->
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.updateSearchQuery(term)
                                            viewModel.commitSearchQuery(term)
                                        },
                                        label = { Text(term) }
                                    )
                                }
                            }
                        }
                    }

                    // Trending Searches
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Whatshot, contentDescription = null, tint = VidooPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trending on VIDOO", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            trendingSearches.forEach { tag ->
                                SuggestionChip(
                                    onClick = {
                                        viewModel.updateSearchQuery(tag)
                                        viewModel.commitSearchQuery(tag)
                                    },
                                    label = { Text(tag, color = VidooCyan) }
                                )
                            }
                        }
                    }
                }

                // Suggested / Matching Creators
                if (selectedScope in listOf("ALL", "CREATORS")) {
                    item {
                        Text(
                            text = if (query.isBlank()) "Suggested Creators" else "Matching Users & Creators (${matchingUsers.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    items(matchingUsers, key = { "search_user_${it.id}" }) { creator ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = creator.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (creator.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            VerifiedCreatorBadge(size = 14.dp)
                                        }
                                    }
                                    Text(
                                        text = "@${creator.username} • ${formatCompactCount(creator.followersCount.toLong())} followers",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VidooCyan
                                    )
                                    Text(
                                        text = creator.bio,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                                Button(
                                    onClick = { viewModel.toggleFollowCreator(creator.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (creator.isFollowing) MaterialTheme.colorScheme.surfaceVariant else VidooCyan,
                                        contentColor = if (creator.isFollowing) MaterialTheme.colorScheme.onSurface else Color.Black
                                    ),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(if (creator.isFollowing) "Following" else "Follow")
                                }
                            }
                        }
                    }
                }

                // Matching Videos & Posts
                if (selectedScope != "CREATORS") {
                    item {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Matching Videos & Posts (${matchingPosts.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    items(matchingPosts, key = { "search_post_${it.id}" }) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.commitSearchQuery(query)
                                    viewModel.openLongVideoPlayer(post)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${post.contentType.replace('_', ' ')} • @${post.authorUsername}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VidooCyan
                                )
                                Text(
                                    text = post.title.ifBlank { post.caption },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = post.hashtags.replace(",", " "),
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
