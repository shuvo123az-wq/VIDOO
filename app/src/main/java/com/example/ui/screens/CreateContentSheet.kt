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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import com.example.domain.model.MusicTrack
import com.example.domain.model.PostVisibility
import com.example.domain.model.UploadLifecycleState
import com.example.domain.model.UploadStep
import com.example.ui.components.VidooMediaThumbnail
import com.example.ui.theme.VidooCyan
import com.example.ui.theme.VidooPurple
import com.example.ui.theme.VidooSuccess
import com.example.ui.viewmodel.VidooViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateContentSheet(
    viewModel: VidooViewModel,
    initialCreateType: String,
    uploadState: UploadLifecycleState,
    currentStep: UploadStep,
    uploadProgress: Float,
    maxShortDurationSec: Int,
    onDismiss: () -> Unit
) {
    var selectedType by remember(initialCreateType) { mutableStateOf(initialCreateType) }
    var title by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var hashtags by remember { mutableStateOf("#Vidoo,#WatchShareConnect,#Creators") }
    var mentions by remember { mutableStateOf("@novachen_fx") }
    var locationTag by remember { mutableStateOf("Los Angeles Studio") }
    var selectedVisibility by remember { mutableStateOf(PostVisibility.PUBLIC) }
    var pickedMediaUri by remember { mutableStateOf<String?>(null) }
    var durationSeconds by remember { mutableIntStateOf(45) }

    val musicTracks = remember { viewModel.repository.getLicensedMusicLibrary() }
    var selectedMusic by remember { mutableStateOf<MusicTrack?>(musicTracks.firstOrNull()) }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedMediaUri = uri.toString()
        }
    }

    val createOptions = listOf(
        Triple("SHORT_VIDEO", "Upload Short", Icons.Filled.SmartDisplay),
        Triple("LONG_VIDEO", "Upload Long Video", Icons.Filled.Movie),
        Triple("PHOTO", "Upload Photo", Icons.Filled.Image),
        Triple("TEXT", "Create Post", Icons.Filled.TextFields)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create & Upload",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Publish original Posts, Photos, 9:16 Shorts, or 16:9 Long Videos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_create_sheet_btn")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close composer")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Creation Mode Buttons (CREATE button menu)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                createOptions.forEach { (typeKey, label, icon) ->
                    val isSelected = selectedType == typeKey
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedType = typeKey
                            durationSeconds = if (typeKey == "SHORT_VIDEO") 45 else if (typeKey == "LONG_VIDEO") 720 else 0
                        },
                        label = { Text(label) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VidooCyan,
                            selectedLabelColor = Color.Black,
                            selectedLeadingIconColor = Color.Black
                        ),
                        modifier = Modifier.testTag("create_mode_${typeKey.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 1: Zero-Permission Media Selector & Preview
            if (selectedType != "TEXT") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val req = if (selectedType.contains("VIDEO")) {
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            } else {
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            }
                            mediaPickerLauncher.launch(req)
                        }
                        .testTag("select_media_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, VidooCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        ) {
                            VidooMediaThumbnail(
                                mediaResName = if (selectedType == "SHORT_VIDEO") {
                                    "img_short_neon_dancer_1790684791969"
                                } else {
                                    "img_video_tech_review_1790684807149"
                                },
                                customUri = pickedMediaUri,
                                contentDescription = "Selected media preview",
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.72f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.VideoFile,
                                        contentDescription = null,
                                        tint = VidooCyan
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (pickedMediaUri != null) {
                                            "Device Media Selected • Tap to Change"
                                        } else {
                                            "Tap to Select Media from Device (or use Studio Master)"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedType == "SHORT_VIDEO") {
                                "Format: 9:16 Vertical • Admin Max Duration: ${maxShortDurationSec}s • Adaptive Compression Enabled"
                            } else if (selectedType == "LONG_VIDEO") {
                                "Format: 16:9 Widescreen • Multi-Resolution (360p–4K) • Cloud Storage Direct Upload"
                            } else {
                                "High-Resolution Photo • Thumbnail & WebP Cache Enabled"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = VidooCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Title & Caption
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Headline") },
                placeholder = { Text("Enter a compelling title for your video or post…") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("composer_title_input")
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Description / Post Text") },
                placeholder = { Text("Share the story, gear list, timestamps, or thoughts…") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("composer_caption_input")
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = hashtags,
                    onValueChange = { hashtags = it },
                    label = { Text("Hashtags") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("composer_hashtags_input")
                )
                OutlinedTextField(
                    value = mentions,
                    onValueChange = { mentions = it },
                    label = { Text("Mentions") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("composer_mentions_input")
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = locationTag,
                onValueChange = { locationTag = it },
                label = { Text("Optional Location") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = VidooCyan) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Visibility Selector: Public, Followers, Private
            Text(
                text = "Visibility",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostVisibility.entries.forEach { vis ->
                    FilterChip(
                        selected = selectedVisibility == vis,
                        onClick = { selectedVisibility = vis },
                        label = { Text(vis.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VidooPurple,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("visibility_${vis.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 32: Legal Royalty-Cleared Music Library Selector
            Text(
                text = "Licensed Royalty-Cleared Music Library",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "All tracks are cleared for monetized creators with automatic credit attribution.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                musicTracks.forEach { track ->
                    val isSelected = selectedMusic?.id == track.id
                    Surface(
                        color = if (isSelected) VidooCyan.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isSelected) VidooCyan else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMusic = track }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Filled.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSelected) VidooCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${track.title} — ${track.artist} (${track.bpm} BPM)",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = track.attributionText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = track.licenseCode,
                                style = MaterialTheme.typography.labelSmall,
                                color = VidooCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 23: 7-Step Professional Video Upload Pipeline Status Card
            if (uploadState != UploadLifecycleState.IDLE) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Upload Pipeline: ${uploadState.label}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (uploadState) {
                                    UploadLifecycleState.FAILED -> MaterialTheme.colorScheme.error
                                    UploadLifecycleState.PUBLISHED -> VidooSuccess
                                    else -> VidooCyan
                                }
                            )
                            Text(
                                text = "${(uploadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            color = if (uploadState == UploadLifecycleState.FAILED) MaterialTheme.colorScheme.error else VidooCyan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentStep.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uploadState == UploadLifecycleState.FAILED) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.startUploadPipeline(
                                        contentType = selectedType,
                                        title = title.ifBlank { "New Vidoo Creation" },
                                        caption = caption.ifBlank { "Published via Vidoo Studio" },
                                        hashtags = hashtags,
                                        mentions = mentions,
                                        visibility = selectedVisibility.name,
                                        locationTag = locationTag,
                                        musicTrack = selectedMusic,
                                        customMediaUri = pickedMediaUri,
                                        durationSeconds = durationSeconds,
                                        isDraft = false,
                                        simulateFailureOnce = false
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VidooCyan, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry Upload")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // Save Draft & Publish Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.startUploadPipeline(
                            contentType = selectedType,
                            title = title.ifBlank { "Untitled Draft" },
                            caption = caption.ifBlank { "Draft saved in Vidoo Composer" },
                            hashtags = hashtags,
                            mentions = mentions,
                            visibility = selectedVisibility.name,
                            locationTag = locationTag,
                            musicTrack = selectedMusic,
                            customMediaUri = pickedMediaUri,
                            durationSeconds = durationSeconds,
                            isDraft = true
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("save_draft_btn")
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Draft")
                }

                Button(
                    onClick = {
                        viewModel.startUploadPipeline(
                            contentType = selectedType,
                            title = title.ifBlank { "Original Vidoo ${selectedType.replace('_', ' ')}" },
                            caption = caption.ifBlank { "Sharing new visual ideas on VIDOO! Watch. Share. Connect." },
                            hashtags = hashtags,
                            mentions = mentions,
                            visibility = selectedVisibility.name,
                            locationTag = locationTag,
                            musicTrack = selectedMusic,
                            customMediaUri = pickedMediaUri,
                            durationSeconds = durationSeconds,
                            isDraft = false
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VidooCyan,
                        contentColor = Color(0xFF001F24)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp)
                        .testTag("publish_post_btn")
                ) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Now",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
