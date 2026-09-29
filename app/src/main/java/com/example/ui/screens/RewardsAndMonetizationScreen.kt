package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.DailyChallengeEntity
import com.example.data.local.RewardTransactionEntity
import com.example.data.local.UserEntity
import com.example.domain.model.CreatorAnalyticsSummary
import com.example.domain.model.CreatorEligibilityConfig
import com.example.domain.model.SubscriptionTier
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.VidooCoinGold
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.theme.VidooRewardOrange
import com.example.ui.theme.VidooSuccess
import com.example.ui.viewmodel.RewardedAdFlowState
import com.example.ui.viewmodel.VidooViewModel

/**
 * Sections 14, 16, 30 & 31:
 * VIDOO REWARDS, "Watch Ad & Earn" SSV Flow, Gamification (XP/Levels/Streak/Challenges/Achievements),
 * and Referral System.
 */
@Composable
fun VidooRewardsScreen(
    viewModel: VidooViewModel,
    user: UserEntity?,
    challenges: List<DailyChallengeEntity>,
    transactions: List<RewardTransactionEntity>,
    adFlowState: RewardedAdFlowState,
    onBack: () -> Unit
) {
    val achievements = remember(user) { viewModel.repository.getAchievements(user) }
    var referralInputHandle by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                        text = "VIDOO REWARDS & COINS",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Server-validated virtual points, streaks, challenges & referrals",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Balance & Virtual Points Disclaimer Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            VidooCoinGold.copy(alpha = 0.22f),
                                            VidooPurple.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Stars,
                                            contentDescription = null,
                                            tint = VidooCoinGold,
                                            modifier = Modifier.size(34.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${user?.coinBalance ?: 540} VIDOO COINS",
                                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                                color = VidooCoinGold
                                            )
                                            Text(
                                                text = "Level ${user?.level ?: 5} • ${user?.currentXp ?: 75}/${user?.xpForNextLevel ?: 100} XP",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = VidooCyan
                                            )
                                        }
                                    }

                                    Surface(
                                        color = VidooRewardOrange.copy(alpha = 0.22f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.LocalFireDepartment,
                                                contentDescription = "Streak",
                                                tint = VidooRewardOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${user?.streakDays ?: 7}-Day Streak",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = VidooRewardOrange
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = stringResource(id = R.string.coins_disclaimer),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.claimDailyLoginBonus() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VidooCoinGold,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("claim_daily_login_btn")
                                ) {
                                    Text(
                                        text = "Claim Daily Login Check-In (+25 Vidoo Coins)",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 16: "Watch Ad & Earn" 5-Step Server-Validated Flow
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.PlayCircleFilled, contentDescription = null, tint = VidooCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Watch Ad & Earn (Server-Validated SSV)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Choose to watch -> 2. Ad displayed -> 3. Completion verified -> 4. Server SSV validation -> 5. Balance credited.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = adFlowState.statusText,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (adFlowState == RewardedAdFlowState.REWARD_GRANTED) VidooSuccess else VidooCyan
                                    )
                                    if (adFlowState != RewardedAdFlowState.IDLE) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { adFlowState.stepIndex / 4f },
                                            color = VidooCyan,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.triggerWatchAdAndEarn() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VidooCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("watch_ad_and_earn_btn")
                            ) {
                                Text(
                                    text = "Watch Rewarded Ad & Earn +30 VIDOO COINS",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                // Daily Challenges Section
                item {
                    Text(
                        text = "Daily Challenges",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(challenges, key = { it.id }) { challenge ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = challenge.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = challenge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = VidooCoinGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "+${challenge.coinReward} Coins • +${challenge.xpReward} XP",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCoinGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = {
                                    (challenge.currentCount.toFloat() / challenge.targetCount.coerceAtLeast(1).toFloat())
                                        .coerceIn(0f, 1f)
                                },
                                color = VidooCyan,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progress: ${challenge.currentCount} / ${challenge.targetCount}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Button(
                                    onClick = { viewModel.claimChallengeReward(challenge) },
                                    enabled = !challenge.isClaimed && challenge.currentCount >= challenge.targetCount,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VidooCyan,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(if (challenge.isClaimed) "Claimed" else "Claim Reward")
                                }
                            }
                        }
                    }
                }

                // Section 31: Referral System Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Referral Program",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Your Unique Code: ${user?.referralCode ?: "VIDOO77X"}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCyan
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        viewModel.showBanner("Copied referral invite: Join VIDOO with code ${user?.referralCode ?: "VIDOO77X"}!")
                                    }
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share Code")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Invitations Sent: ${user?.invitedCount ?: 9} • Eligible Verified Referrals: ${user?.eligibleReferrals ?: 6}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = referralInputHandle,
                                    onValueChange = { referralInputHandle = it },
                                    placeholder = { Text("Enter invited creator @username…") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.applyReferralInviteBonus(referralInputHandle)
                                        referralInputHandle = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VidooPurple)
                                ) {
                                    Text("Verify")
                                }
                            }
                        }
                    }
                }

                // Achievements List
                item {
                    Text(
                        text = "Creator Achievements",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(achievements, key = { it.id }) { ach ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = if (ach.isUnlocked) VidooCoinGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ach.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = ach.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = if (ach.isUnlocked) "Unlocked" else "${(ach.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (ach.isUnlocked) VidooSuccess else VidooCyan
                            )
                        }
                    }
                }

                // Server-Validated Transaction History
                item {
                    Text(
                        text = "Server-Verified Coin Ledger",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(transactions, key = { it.id }) { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "SSV Token: ${tx.serverVerificationToken} • ${tx.status}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "+${tx.coinsDelta} Coins",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = VidooCoinGold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 17: VIDOO CREATOR STUDIO (Analytics, Monetization Status & Configurable Eligibility)
 */
@Composable
fun CreatorStudioScreen(
    analytics: CreatorAnalyticsSummary,
    eligibilityConfig: CreatorEligibilityConfig,
    user: UserEntity?,
    onBack: () -> Unit
) {
    val followers = user?.followersCount ?: 14820
    val meetsFollowers = followers >= eligibilityConfig.minFollowers
    val meetsViews = analytics.totalViews >= eligibilityConfig.minViews
    val meetsWatchTime = analytics.watchTimeMinutes >= eligibilityConfig.minWatchTimeMinutes
    val meetsAge = analytics.accountAgeDays >= eligibilityConfig.minAccountAgeDays

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
                        text = "VIDOO CREATOR STUDIO",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Monetization analytics, watch time & payout status",
                        style = MaterialTheme.typography.labelSmall,
                        color = VidooCyan
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Monetization & Payment Status Banner
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = VidooSuccess)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Monetization: ${analytics.monetizationStatus}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = VidooSuccess
                                    )
                                }
                                Text(
                                    text = "$${String.format("%.2f", analytics.estimatedEarningsUsd)}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VidooCoinGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Payment Status: ${analytics.paymentStatus}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 8 Core Creator Studio KPI Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiMetricCard("Total Views", formatCompactCount(analytics.totalViews), Modifier.weight(1f))
                        KpiMetricCard("Watch Time", "${formatCompactCount(analytics.watchTimeMinutes)} min", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiMetricCard("Eligible Views", formatCompactCount(analytics.eligibleMonetizedViews), Modifier.weight(1f))
                        KpiMetricCard("Followers +30d", "+${formatCompactCount(analytics.followersGained30d.toLong())}", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiMetricCard("Total Likes", formatCompactCount(analytics.totalLikes.toLong()), Modifier.weight(1f))
                        KpiMetricCard("Comments & Shares", "${analytics.totalComments + analytics.totalShares}", Modifier.weight(1f))
                    }
                }

                // Configurable Creator Eligibility Requirements
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Configurable Creator Eligibility Requirements",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Thresholds are remotely managed via Vidoo Admin Panel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            EligibilityCheckRow(
                                label = "Minimum Followers (${eligibilityConfig.minFollowers} required)",
                                value = "$followers",
                                passed = meetsFollowers
                            )
                            EligibilityCheckRow(
                                label = "Minimum Total Views (${eligibilityConfig.minViews} required)",
                                value = "${analytics.totalViews}",
                                passed = meetsViews
                            )
                            EligibilityCheckRow(
                                label = "Minimum Watch Time (${eligibilityConfig.minWatchTimeMinutes} min required)",
                                value = "${analytics.watchTimeMinutes} min",
                                passed = meetsWatchTime
                            )
                            EligibilityCheckRow(
                                label = "Account Age (${eligibilityConfig.minAccountAgeDays} days required)",
                                value = "${analytics.accountAgeDays} days",
                                passed = meetsAge
                            )
                            EligibilityCheckRow(
                                label = "Community & Copyright Policy Compliance",
                                value = "Verified Clean",
                                passed = analytics.policyCompliant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiMetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = VidooCyan
            )
        }
    }
}

@Composable
private fun EligibilityCheckRow(label: String, value: String, passed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = if (passed) VidooSuccess else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (passed) VidooCyan else MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Section 18: SUBSCRIPTIONS (FREE, PREMIUM, CREATOR via Google Play Billing Architecture)
 */
@Composable
fun SubscriptionsScreen(
    viewModel: VidooViewModel,
    currentTierName: String,
    onBack: () -> Unit
) {
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
                        text = "VIDOO SUBSCRIPTIONS",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Google Play Billing integration • Server purchase token validation",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(SubscriptionTier.entries, key = { it.name }) { tier ->
                    val isCurrent = currentTierName.equals(tier.name, ignoreCase = true)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) VidooCyan else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (tier == SubscriptionTier.CREATOR) Icons.Filled.Verified else Icons.Filled.WorkspacePremium,
                                        contentDescription = null,
                                        tint = VidooCyan
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "VIDOO ${tier.title}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Surface(
                                    color = VidooPurple.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = tier.priceDisplay,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = VidooCyan,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Google Play SKU: ${tier.skuId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            tier.features.forEach { feat ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = VidooCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = feat, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.selectSubscriptionPlan(tier.name) },
                                enabled = !isCurrent,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VidooCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("select_plan_${tier.name.lowercase()}")
                            ) {
                                Text(
                                    text = if (isCurrent) "Current Active Plan" else "Select ${tier.title} via Play Billing",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
