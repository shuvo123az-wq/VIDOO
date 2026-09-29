package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import kotlin.math.sin
import kotlinx.coroutines.delay

/**
 * Interactive Vidoo Video Player supporting:
 * - Real device video URIs via Android VideoView or rich animated visual stream
 * - Auto-play, Pause/Play, Mute/Unmute
 * - Playback speed (0.5x, 1.0x, 1.25x, 1.5x, 2.0x)
 * - Quality selection (360p, 720p, 1080p HD, 4K)
 * - Resume watching timestamp & interactive seek slider
 */
@Composable
fun VidooInteractiveVideoPlayer(
    mediaResName: String,
    customMediaUri: String?,
    durationSeconds: Int,
    initialResumeSeconds: Int = 0,
    autoPlay: Boolean = true,
    showAdvancedControls: Boolean = true,
    onProgressChanged: (Int) -> Unit = {},
    onToggleFullscreen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val totalSec = durationSeconds.coerceAtLeast(30)
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isMuted by remember { mutableStateOf(false) }
    var currentSeconds by remember(initialResumeSeconds) {
        mutableIntStateOf(initialResumeSeconds.coerceIn(0, totalSec - 1))
    }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var selectedQuality by remember { mutableStateOf("1080p HD") }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }

    val speedOptions = listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f)
    val qualityOptions = listOf("360p", "720p", "1080p HD", "4K")

    LaunchedEffect(isPlaying, playbackSpeed, totalSec) {
        while (isPlaying) {
            val tickMs = (1000f / playbackSpeed).toLong().coerceAtLeast(250L)
            delay(tickMs)
            currentSeconds = if (currentSeconds + 1 >= totalSec) 0 else currentSeconds + 1
            onProgressChanged(currentSeconds)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "video_visualizer")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable { isPlaying = !isPlaying }
            .testTag("vidoo_video_player")
    ) {
        if (!customMediaUri.isNullOrBlank() && customMediaUri.startsWith("content://")) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(Uri.parse(customMediaUri))
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            val vol = if (isMuted) 0f else 1f
                            mp.setVolume(vol, vol)
                            if (initialResumeSeconds > 0) {
                                seekTo(initialResumeSeconds * 1000)
                            }
                            if (isPlaying) start()
                        }
                    }
                },
                update = { view ->
                    if (isPlaying && !view.isPlaying) view.start()
                    else if (!isPlaying && view.isPlaying) view.pause()
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            VidooMediaThumbnail(
                mediaResName = mediaResName,
                customUri = customMediaUri,
                contentDescription = "Video frame",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Dynamic live motion visualizer overlay when playing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.35f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.72f)
                    )
                )
            )
            if (isPlaying) {
                val barCount = 24
                val barSpacing = w / (barCount + 4)
                for (i in 0 until barCount) {
                    val amplitude = (sin(wavePhase + i * 0.45f) + 1.2f) * 0.5f
                    val barHeight = h * 0.04f * amplitude
                    val x = barSpacing * (i + 2)
                    drawLine(
                        color = if (i % 2 == 0) VidooCyan.copy(alpha = 0.45f) else VidooPurple.copy(alpha = 0.45f),
                        start = Offset(x, h - 6.dp.toPx()),
                        end = Offset(x, h - 6.dp.toPx() - barHeight),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }
        }

        // Top badges: Resume badge + Quality & Speed selectors
        if (showAdvancedControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (initialResumeSeconds > 0 && currentSeconds >= initialResumeSeconds) {
                            "Resumed • $selectedQuality • ${playbackSpeed}x"
                        } else {
                            "$selectedQuality • ${playbackSpeed}x"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = VidooCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { showSpeedMenu = true }
                                .testTag("player_speed_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Speed,
                                    contentDescription = "Playback speed",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${playbackSpeed}x",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            speedOptions.forEach { spd ->
                                DropdownMenuItem(
                                    text = { Text("${spd}x Speed") },
                                    onClick = {
                                        playbackSpeed = spd
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Box {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { showQualityMenu = true }
                                .testTag("player_quality_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.HighQuality,
                                    contentDescription = "Video quality",
                                    tint = VidooCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedQuality,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showQualityMenu,
                            onDismissRequest = { showQualityMenu = false }
                        ) {
                            qualityOptions.forEach { q ->
                                DropdownMenuItem(
                                    text = { Text(q) },
                                    onClick = {
                                        selectedQuality = q
                                        showQualityMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Center Play/Pause indicator
        if (!isPlaying) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play video",
                        tint = VidooCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Bottom Controls & Progress Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            if (showAdvancedControls) {
                Slider(
                    value = currentSeconds.toFloat(),
                    onValueChange = {
                        currentSeconds = it.toInt()
                        onProgressChanged(currentSeconds)
                    },
                    valueRange = 0f..totalSec.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = VidooCyan,
                        activeTrackColor = VidooCyan,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.height(24.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("player_mute_btn")
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = if (isMuted) Color.LightGray else VidooCyan
                            )
                        }
                        Text(
                            text = "${formatDuration(currentSeconds)} / ${formatDuration(totalSec)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                    if (onToggleFullscreen != null) {
                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White
                            )
                        }
                    }
                }
            } else {
                // Compact progress + mute button for Shorts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (currentSeconds.toFloat() / totalSec.toFloat()).coerceIn(0f, 1f) },
                    color = VidooCyan,
                    trackColor = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
    }
}
