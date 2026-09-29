package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.domain.model.RemoteAdConfig
import com.example.domain.model.UiResultState
import com.example.ui.theme.VidooBackgroundDark
import com.example.ui.theme.VidooCoinGold
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.theme.VidooRewardOrange
import com.example.ui.theme.VidooTurquoise

/**
 * Original modern VIDOO LogoMark:
 * A sleek geometric V-wing fused with a cyan/purple play prism.
 */
@Composable
fun VidooLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Deep rounded container
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF141426), Color(0xFF07070D)),
                start = Offset.Zero,
                end = Offset(w, h)
            ),
            cornerRadius = CornerRadius(w * 0.28f, h * 0.28f)
        )

        // Subtle glowing border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(VidooCyan.copy(alpha = 0.7f), VidooPurple.copy(alpha = 0.7f))
            ),
            cornerRadius = CornerRadius(w * 0.28f, h * 0.28f),
            style = Stroke(width = w * 0.05f)
        )

        // Geometric V + Play Prism path
        val playPrism = Path().apply {
            moveTo(w * 0.26f, h * 0.24f)
            lineTo(w * 0.48f, h * 0.76f)
            lineTo(w * 0.78f, h * 0.48f)
            close()
        }
        drawPath(
            path = playPrism,
            brush = Brush.linearGradient(
                colors = listOf(VidooCyan, VidooTurquoise, VidooPurple),
                start = Offset(w * 0.2f, h * 0.2f),
                end = Offset(w * 0.8f, h * 0.8f)
            )
        )

        // Accent V-wing highlight
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(w * 0.26f, h * 0.24f),
            end = Offset(w * 0.48f, h * 0.76f),
            strokeWidth = w * 0.085f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun VidooBrandHeader(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VidooLogoMark(size = 34.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "VIDOO",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (showTagline) {
                Text(
                    text = "WATCH. SHARE. CONNECT.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = VidooCyan
                )
            }
        }
    }
}

@Composable
fun VidooSplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF17162E), VidooBackgroundDark),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VidooLogoMark(size = (88 * pulseScale).dp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "VIDOO",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "WATCH. SHARE. CONNECT.",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 2.4.sp
                ),
                color = VidooCyan
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = VidooCyan,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun VerifiedCreatorBadge(modifier: Modifier = Modifier, size: Dp = 16.dp) {
    Icon(
        imageVector = Icons.Filled.CheckCircle,
        contentDescription = "Verified Vidoo Creator",
        tint = VidooCyan,
        modifier = modifier.size(size)
    )
}

@Composable
fun VidooCoinPill(
    coins: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag("vidoo_coin_pill"),
        color = VidooCoinGold.copy(alpha = 0.16f),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, VidooCoinGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Stars,
                contentDescription = "Vidoo Coins",
                tint = VidooCoinGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$coins",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = VidooCoinGold
            )
        }
    }
}

@Composable
fun VidooMediaThumbnail(
    mediaResName: String,
    customUri: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (!customUri.isNullOrBlank()) {
        AsyncImage(
            model = customUri,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        val resId = resolveDrawableRes(mediaResName)
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

fun resolveDrawableRes(name: String): Int {
    return when {
        name.contains("short_neon_dancer") -> R.drawable.img_short_neon_dancer_1790684791969
        name.contains("video_tech_review") -> R.drawable.img_video_tech_review_1790684807149
        name.contains("avatar_creator") -> R.drawable.img_avatar_creator_1790684817836
        name.contains("vidoo_launcher") -> R.drawable.img_vidoo_launcher_icon_1790684762208
        else -> R.drawable.img_cover_cyber_studio_1790684775496
    }
}

/**
 * Centralized AdService Banner / Native Ad component using Google AdMob Test Unit IDs.
 */
@Composable
fun VidooNativeAdSlot(
    adConfig: RemoteAdConfig,
    placementLabel: String,
    onWatchRewardedAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!adConfig.enableAds || !adConfig.nativeAdsEnabled) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = VidooPurple.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "AD • $placementLabel",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = VidooCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "Test ID: ${adConfig.testNativeUnitId.takeLast(10)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upgrade to Vidoo Creator Studio Pro or Watch & Earn Vidoo Coins",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Centralized AdService • Policy-compliant frequency cap (every ${adConfig.adFrequencyEveryNPosts} items).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (adConfig.rewardedAdsEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onWatchRewardedAd,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VidooRewardOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("native_ad_watch_earn_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Watch Ad & Earn +30 Vidoo Coins")
                }
            }
        }
    }
}

/**
 * Universal Section 39 Error Handling Box supporting Loading, Empty, Error, and Retry states.
 */
@Composable
fun <T> UiStateSectionBox(
    state: UiResultState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier) {
        when (state) {
            is UiResultState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = VidooCyan)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading Vidoo stream…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is UiResultState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inbox,
                        contentDescription = "Empty state",
                        tint = VidooCyan,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    if (state.subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            is UiResultState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = "Error state",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = VidooCyan, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            is UiResultState.Success -> content(state.data)
        }
    }
}

fun formatCompactCount(count: Long): String {
    return when {
        count >= 1_000_000L -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000L -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return "0:00"
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", mins, secs)
}
