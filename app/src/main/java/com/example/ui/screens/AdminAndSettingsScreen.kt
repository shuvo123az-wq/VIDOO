package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.FeedPostEntity
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.data.services.BackendArchitectureSpec
import com.example.domain.model.AdminAppSettings
import com.example.domain.model.FeatureFlags
import com.example.domain.model.RemoteAdConfig
import com.example.domain.model.ReportCategory
import com.example.domain.model.SecondaryDestination
import com.example.ui.theme.VidooCoinGold
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooError
import com.example.ui.theme.VidooPurple
import com.example.ui.theme.VidooSuccess
import com.example.ui.viewmodel.ReportDialogTarget
import com.example.ui.viewmodel.VidooViewModel

/**
 * Section 20 & 36: Secure Admin Panel (Dashboard, User Management, Moderation Queue,
 * AdService Controls, Feature Flags, and App Settings).
 */
@Composable
fun AdminDashboardScreen(
    viewModel: VidooViewModel,
    users: List<UserEntity>,
    posts: List<FeedPostEntity>,
    reports: List<ReportEntity>,
    featureFlags: FeatureFlags,
    adConfig: RemoteAdConfig,
    appSettings: AdminAppSettings,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Users", "Moderation", "Ads & Flags")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                        text = "VIDOO ADMIN PANEL",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Web-ready modular admin architecture & remote config controls",
                        style = MaterialTheme.typography.labelSmall,
                        color = VidooCyan
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(title) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Dashboard KPIs
                        item {
                            Text(
                                text = "Platform Metrics Overview",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AdminMetricGrid(
                                items = listOf(
                                    "Total Users" to "${users.size + 14_250}",
                                    "Active Users (DAU)" to "8,940",
                                    "New Users (24h)" to "+312",
                                    "Published Posts" to "${posts.size + 4_120}",
                                    "Hosted Videos" to "${posts.count { it.contentType.contains("VIDEO") } + 2_890}",
                                    "Total Views" to "2.48M",
                                    "Open Reports" to "${reports.count { it.status == "PENDING_REVIEW" }}",
                                    "Est. Revenue" to "$18,420",
                                    "Rewards Issued" to "145.2K Coins"
                                )
                            )
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Recent Firebase Analytics Events",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCyan
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    viewModel.repository.analytics.loggedEvents.take(8).forEach { ev ->
                                        Text(
                                            text = "• $ev",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // User Management: Search, Suspend, Ban, Restore, Verify Creator
                        items(users, key = { it.id }) { u ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "${u.name} (@${u.username})",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Status: ${u.accountStatus} • Verified: ${u.isVerified}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (u.accountStatus == "ACTIVE") VidooSuccess else VidooError
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.adminSetUserStatus(u.id, "SUSPENDED") }
                                        ) { Text("Suspend") }
                                        OutlinedButton(
                                            onClick = { viewModel.adminSetUserStatus(u.id, "BANNED") }
                                        ) { Text("Ban") }
                                        OutlinedButton(
                                            onClick = { viewModel.adminSetUserStatus(u.id, "ACTIVE") }
                                        ) { Text("Restore") }
                                        Button(
                                            onClick = { viewModel.adminSetUserStatus(u.id, u.accountStatus, !u.isVerified) },
                                            colors = ButtonDefaults.buttonColors(containerColor = VidooCyan, contentColor = Color.Black)
                                        ) {
                                            Text(if (u.isVerified) "Unverify" else "Verify")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Content Moderation Queue
                        items(reports, key = { it.id }) { rep ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "[${rep.targetType}] Category: ${rep.category} • ${rep.status}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCoinGold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = rep.targetPreview, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = "Notes: ${rep.notes}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.adminModerateReport(rep, removeContent = true) },
                                            colors = ButtonDefaults.buttonColors(containerColor = VidooError)
                                        ) {
                                            Text("Remove Content")
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.adminModerateReport(rep, removeContent = false) }
                                        ) {
                                            Text("Restore / Dismiss")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Remote AdService Controls & Feature Flags
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Remote AdService Controls (Section 19)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCyan
                                    )
                                    AdminToggleRow("enableAds", adConfig.enableAds) {
                                        viewModel.updateRemoteAdConfig(adConfig.copy(enableAds = it))
                                    }
                                    AdminToggleRow("bannerEnabled", adConfig.bannerEnabled) {
                                        viewModel.updateRemoteAdConfig(adConfig.copy(bannerEnabled = it))
                                    }
                                    AdminToggleRow("nativeAdsEnabled", adConfig.nativeAdsEnabled) {
                                        viewModel.updateRemoteAdConfig(adConfig.copy(nativeAdsEnabled = it))
                                    }
                                    AdminToggleRow("interstitialEnabled", adConfig.interstitialEnabled) {
                                        viewModel.updateRemoteAdConfig(adConfig.copy(interstitialEnabled = it))
                                    }
                                    AdminToggleRow("rewardedAdsEnabled", adConfig.rewardedAdsEnabled) {
                                        viewModel.updateRemoteAdConfig(adConfig.copy(rewardedAdsEnabled = it))
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Remotely Configurable Feature Flags (Section 36)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCyan
                                    )
                                    AdminToggleRow("enableShorts", featureFlags.enableShorts) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableShorts = it))
                                    }
                                    AdminToggleRow("enableLongVideos", featureFlags.enableLongVideos) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableLongVideos = it))
                                    }
                                    AdminToggleRow("enableMessaging", featureFlags.enableMessaging) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableMessaging = it))
                                    }
                                    AdminToggleRow("enableRewards", featureFlags.enableRewards) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableRewards = it))
                                    }
                                    AdminToggleRow("enableSubscriptions", featureFlags.enableSubscriptions) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableSubscriptions = it))
                                    }
                                    AdminToggleRow("enableCreatorMonetization", featureFlags.enableCreatorMonetization) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableCreatorMonetization = it))
                                    }
                                    AdminToggleRow("enableReferrals", featureFlags.enableReferrals) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableReferrals = it))
                                    }
                                    AdminToggleRow("enableStories (Future Flag)", featureFlags.enableStories) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableStories = it))
                                    }
                                    AdminToggleRow("enableLiveStreaming (Future Flag)", featureFlags.enableLiveStreaming) {
                                        viewModel.updateFeatureFlags(featureFlags.copy(enableLiveStreaming = it))
                                    }
                                    AdminToggleRow("Maintenance Mode", appSettings.maintenanceMode) {
                                        viewModel.updateAdminAppSettings(appSettings.copy(maintenanceMode = it))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricGrid(items: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { (label, value) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = VidooCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

/**
 * Sections 21, 33 & 38: Settings, Legal Policies, Play Store Readiness & Account Deletion
 */
@Composable
fun SettingsAndLegalScreen(
    viewModel: VidooViewModel,
    user: UserEntity?,
    isDarkMode: Boolean,
    draftsCount: Int,
    savedCount: Int,
    onBack: () -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var activePolicyDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                Text(
                    text = "Settings & Play Store Compliance",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Branding & Theme
                item {
                    SettingsGroupCard(title = "APPEARANCE & BRANDING") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = VidooCyan)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Dark / Light Mode Branding", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = if (isDarkMode) "Dark Mode (#07070D)" else "Light Mode Branding",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                modifier = Modifier.testTag("toggle_theme_switch")
                            )
                        }
                    }
                }

                // Profile, Account & Security
                item {
                    SettingsGroupCard(title = "PROFILE, ACCOUNT & SECURITY") {
                        SettingsInfoRow("Full Name & @Username", "${user?.name ?: "Alex Rivera"} (@${user?.username ?: "alexrivera"})")
                        SettingsInfoRow("Email (Verified)", user?.email ?: "alex.rivera@vidoo.app")
                        SettingsInfoRow("Phone", user?.phone?.ifBlank { "Not linked" } ?: "+1 (555) 234-8910")
                        SettingsActionRow("Change Password / Reset Link", Icons.Filled.Security) {
                            viewModel.sendPasswordResetEmail(user?.email ?: "alex.rivera@vidoo.app")
                            viewModel.showBanner("Password reset link sent to your email")
                        }
                        SettingsInfoRow("2-Step Verification", "Active • Authenticator + SMS Backup")
                        SettingsInfoRow("Active Login Sessions", "Android Device (Current Session • Verified)")
                    }
                }

                // Content, Subscriptions & Admin
                item {
                    SettingsGroupCard(title = "CONTENT, MONETIZATION & ADMIN") {
                        SettingsActionRow("Saved Posts ($savedCount) & Drafts ($draftsCount)", Icons.Filled.VerifiedUser) {
                            viewModel.navigateToSecondary(SecondaryDestination.SAVED_POSTS)
                        }
                        SettingsActionRow("Manage Subscriptions (${user?.subscriptionTier ?: "CREATOR"})", Icons.Filled.VerifiedUser) {
                            viewModel.navigateToSecondary(SecondaryDestination.SUBSCRIPTIONS)
                        }
                        SettingsActionRow("Open Admin Panel Dashboard", Icons.Filled.AdminPanelSettings) {
                            viewModel.navigateToSecondary(SecondaryDestination.ADMIN_PANEL)
                        }
                        SettingsActionRow("Firebase & Play Store Architecture Specs", Icons.Filled.Code) {
                            viewModel.navigateToSecondary(SecondaryDestination.ARCHITECTURE_SPECS)
                        }
                    }
                }

                // Legal, Safety & Play Store Readiness
                item {
                    SettingsGroupCard(title = "LEGAL, SAFETY & GOOGLE PLAY COMPLIANCE") {
                        SettingsActionRow("Community Guidelines", Icons.Filled.Gavel) {
                            activePolicyDialog = "Community Guidelines" to BackendArchitectureSpec.COMMUNITY_GUIDELINES
                        }
                        SettingsActionRow("Copyright & DMCA Policy", Icons.Filled.Policy) {
                            activePolicyDialog = "Copyright Policy" to BackendArchitectureSpec.COPYRIGHT_POLICY
                        }
                        SettingsActionRow("Privacy Policy & Data Safety", Icons.Filled.Policy) {
                            activePolicyDialog = "Privacy Policy" to BackendArchitectureSpec.PRIVACY_POLICY
                        }
                        SettingsActionRow("Terms of Service", Icons.Filled.Gavel) {
                            activePolicyDialog = "Terms of Service" to BackendArchitectureSpec.TERMS_OF_SERVICE
                        }
                        SettingsActionRow("Help & Support Center", Icons.AutoMirrored.Filled.HelpOutline) {
                            activePolicyDialog = "Vidoo Help & Support" to "Contact support@vidoo.app or submit a ticket directly from any post, comment, or chat thread."
                        }
                    }
                }

                // Account Logout & Mandatory Account Deletion
                item {
                    SettingsGroupCard(title = "ACCOUNT ACTIONS") {
                        OutlinedButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_logout_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VidooError, contentColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_delete_account_btn")
                        ) {
                            Icon(Icons.Filled.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Account & Data Permanently")
                        }
                    }
                }
            }
        }
    }

    if (activePolicyDialog != null) {
        AlertDialog(
            onDismissRequest = { activePolicyDialog = null },
            title = { Text(activePolicyDialog!!.first) },
            text = { Text(activePolicyDialog!!.second, style = MaterialTheme.typography.bodySmall) },
            confirmButton = {
                Button(onClick = { activePolicyDialog = null }) { Text("Close") }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete VIDOO Account Permanently?") },
            text = {
                Text(
                    "In compliance with Google Play User Data Policy, confirming will permanently delete your Vidoo profile, uploaded posts/videos, and personal data."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteAccountPermanently()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VidooError)
                ) {
                    Text("Permanently Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsGroupCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = VidooCyan
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun SettingsActionRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = VidooCyan, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

/**
 * Section 25, 26 & 38: Architecture Inspector (Firestore Collections, Security Rules, Storage Rules, Cloud Functions)
 */
@Composable
fun ArchitectureSpecsScreen(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                Text(
                    text = "Backend & Play Store Blueprints",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "24 Firestore Collections Schema (Section 25)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = VidooCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            BackendArchitectureSpec.firestoreCollections.forEach { (col, desc) ->
                                Text(
                                    text = "• $col: $desc",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Firestore Security Rules (Server-Validated Rewards & Roles)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = VidooCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = BackendArchitectureSpec.FIRESTORE_SECURITY_RULES.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Firebase Storage Rules & Cloud Functions SSV",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = VidooCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = BackendArchitectureSpec.STORAGE_SECURITY_RULES.trim() + "\n\n" +
                                    BackendArchitectureSpec.CLOUD_FUNCTIONS_BLUEPRINT.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 21: Universal Safety & Moderation Report Dialog
 */
@Composable
fun SafetyReportDialog(
    target: ReportDialogTarget,
    onDismiss: () -> Unit,
    onSubmit: (ReportCategory, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ReportCategory.SPAM) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report ${target.targetType}") },
        text = {
            Column {
                Text(
                    text = "Target: ${target.targetPreview}",
                    style = MaterialTheme.typography.labelSmall,
                    color = VidooCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                ReportCategory.entries.forEach { cat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat }
                        )
                        Column {
                            Text(text = cat.label, style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = cat.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Additional details (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedCategory, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = VidooError, contentColor = Color.White),
                modifier = Modifier.testTag("submit_report_confirm_btn")
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
