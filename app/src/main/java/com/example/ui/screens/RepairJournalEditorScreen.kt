package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.JournalPhotoStorage
import com.example.data.local.RepairJournalEntity
import com.example.ui.MainViewModel
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairJournalEditorScreen(
    entryId: Int,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val entries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val existingEntry = entries.firstOrNull { it.id == entryId }

    var initialized by rememberSaveable(entryId) { mutableStateOf(entryId == NEW_JOURNAL_ENTRY_ID) }
    var title by rememberSaveable(entryId) { mutableStateOf("") }
    var issueDetails by rememberSaveable(entryId) { mutableStateOf("") }
    var actionsTaken by rememberSaveable(entryId) { mutableStateOf("") }
    var solution by rememberSaveable(entryId) { mutableStateOf("") }
    var isResolved by rememberSaveable(entryId) { mutableStateOf(false) }
    var photoPath by rememberSaveable(entryId) { mutableStateOf<String?>(null) }
    var originalPhotoPath by rememberSaveable(entryId) { mutableStateOf<String?>(null) }
    var capturePath by rememberSaveable { mutableStateOf<String?>(null) }
    var photoError by rememberSaveable { mutableStateOf<String?>(null) }
    var saveAttempted by rememberSaveable { mutableStateOf(false) }
    var showDiscardConfirmation by rememberSaveable { mutableStateOf(false) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(existingEntry?.id) {
        if (!initialized && existingEntry != null) {
            title = existingEntry.title
            issueDetails = existingEntry.issueDetails
            actionsTaken = existingEntry.actionsTaken
            solution = existingEntry.solution
            isResolved = existingEntry.isResolved
            photoPath = existingEntry.photoPath
            originalPhotoPath = existingEntry.photoPath
            initialized = true
        }
    }

    fun replaceDraftPhoto(newPath: String?) {
        if (photoPath != originalPhotoPath) {
            JournalPhotoStorage.delete(photoPath)
        }
        photoPath = newPath
        photoError = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val completedPath = capturePath
        if (success && completedPath != null && File(completedPath).length() > 0L) {
            replaceDraftPhoto(completedPath)
        } else {
            JournalPhotoStorage.delete(completedPath)
            if (completedPath != null) photoError = "The photo was not saved. Please try again."
        }
        capturePath = null
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                val importedPath = withContext(Dispatchers.IO) {
                    JournalPhotoStorage.importPhoto(context, uri)
                }
                if (importedPath != null) {
                    replaceDraftPhoto(importedPath)
                } else {
                    photoError = "That image could not be added. Try another photo."
                }
            }
        }
    }

    val latestPhotoPath by rememberUpdatedState(photoPath)
    val latestOriginalPhotoPath by rememberUpdatedState(originalPhotoPath)
    val latestCapturePath by rememberUpdatedState(capturePath)
    val latestSaved by rememberUpdatedState(saved)
    DisposableEffect(entryId) {
        onDispose {
            if (!latestSaved && latestPhotoPath != latestOriginalPhotoPath) {
                JournalPhotoStorage.delete(latestPhotoPath)
            }
            JournalPhotoStorage.delete(latestCapturePath)
        }
    }

    val hasUnsavedChanges = initialized && !saved && (
        title != existingEntry?.title.orEmpty() ||
            issueDetails != existingEntry?.issueDetails.orEmpty() ||
            actionsTaken != existingEntry?.actionsTaken.orEmpty() ||
            solution != existingEntry?.solution.orEmpty() ||
            isResolved != (existingEntry?.isResolved ?: false) ||
            photoPath != existingEntry?.photoPath
        )

    fun requestBack() {
        if (hasUnsavedChanges) {
            showDiscardConfirmation = true
        } else {
            onBackClick()
        }
    }

    fun saveEntry() {
        saveAttempted = true
        if (title.isBlank() || issueDetails.isBlank() || (isResolved && solution.isBlank())) return
        isSaving = true
        val now = System.currentTimeMillis()
        viewModel.saveJournalEntry(
            entry = RepairJournalEntity(
                id = existingEntry?.id ?: 0,
                title = title.trim(),
                issueDetails = issueDetails.trim(),
                actionsTaken = actionsTaken.trim(),
                solution = solution.trim(),
                isResolved = isResolved,
                photoPath = photoPath,
                createdAt = existingEntry?.createdAt ?: now,
                updatedAt = now
            ),
            previousPhotoPath = originalPhotoPath,
            onSaved = {
                saved = true
                isSaving = false
                onSaved()
            }
        )
    }

    BackHandler(enabled = hasUnsavedChanges) { showDiscardConfirmation = true }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = ::requestBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        if (entryId == NEW_JOURNAL_ENTRY_ID) "New journal entry" else "Edit journal entry",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (initialized) {
                Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Button(
                        onClick = ::saveEntry,
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(52.dp)
                            .testTag("save_journal_entry"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text(if (entryId == NEW_JOURNAL_ENTRY_ID) "Save entry" else "Save changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (!initialized) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Loading entry…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("journal_editor_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Record enough detail to recognize the problem and repeat the repair later. Everything stays on this device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Entry title") },
                    placeholder = { Text("Example: Laptop would not charge") },
                    singleLine = true,
                    isError = saveAttempted && title.isBlank(),
                    supportingText = if (saveAttempted && title.isBlank()) ({ Text("Add a short title.") }) else null,
                    modifier = Modifier.fillMaxWidth().testTag("journal_title")
                )

                OutlinedTextField(
                    value = issueDetails,
                    onValueChange = { issueDetails = it },
                    label = { Text("What was the issue?") },
                    placeholder = { Text("Describe symptoms, error messages, and when it started") },
                    minLines = 3,
                    isError = saveAttempted && issueDetails.isBlank(),
                    supportingText = if (saveAttempted && issueDetails.isBlank()) ({ Text("Describe the issue.") }) else null,
                    modifier = Modifier.fillMaxWidth().testTag("journal_issue")
                )

                OutlinedTextField(
                    value = actionsTaken,
                    onValueChange = { actionsTaken = it },
                    label = { Text("Fixes or steps tried") },
                    placeholder = { Text("List checks, parts, settings, or steps you tried") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("journal_actions")
                )

                OutlinedTextField(
                    value = solution,
                    onValueChange = { solution = it },
                    label = { Text("Solution or next step") },
                    placeholder = { Text("Write what worked, or what you plan to try next") },
                    minLines = 3,
                    isError = saveAttempted && isResolved && solution.isBlank(),
                    supportingText = if (saveAttempted && isResolved && solution.isBlank()) ({
                        Text("Add the solution before marking this repair resolved.")
                    }) else null,
                    modifier = Modifier.fillMaxWidth().testTag("journal_solution")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Repair status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = !isResolved,
                            onClick = { isResolved = false },
                            label = { Text("In progress") },
                            leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.testTag("journal_status_in_progress")
                        )
                        FilterChip(
                            selected = isResolved,
                            onClick = { isResolved = true },
                            label = { Text("Resolved") },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            modifier = Modifier.testTag("journal_status_resolved")
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Photo (optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    photoPath?.let { path ->
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Attached repair photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .testTag("journal_photo_preview")
                        )
                    }
                    JournalPhotoActions(
                        hasPhoto = photoPath != null,
                        onTakePhoto = {
                            photoError = null
                            val target = runCatching { JournalPhotoStorage.createCaptureTarget(context) }
                                .onFailure { photoError = "The camera could not be opened on this device." }
                                .getOrNull()
                            if (target != null) {
                                capturePath = target.path
                                runCatching { cameraLauncher.launch(target.uri) }
                                    .onFailure {
                                        JournalPhotoStorage.delete(target.path)
                                        capturePath = null
                                        photoError = "The camera could not be opened on this device."
                                    }
                            }
                        },
                        onChoosePhoto = {
                            photoError = null
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                    if (photoPath != null) {
                        TextButton(onClick = { replaceDraftPhoto(null) }) {
                            Text("Remove photo", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    photoError?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(6.dp))
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text("Discard unsaved changes?") },
            text = { Text("Your journal edits and any newly attached photo have not been saved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardConfirmation = false
                        onBackClick()
                    }
                ) { Text("Discard", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmation = false }) { Text("Keep editing") }
            }
        )
    }
}

@Composable
private fun JournalPhotoActions(
    hasPhoto: Boolean,
    onTakePhoto: () -> Unit,
    onChoosePhoto: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val compact = maxWidth < 360.dp
        val takeButton: @Composable (Modifier) -> Unit = { modifier ->
            OutlinedButton(
                onClick = onTakePhoto,
                modifier = modifier.testTag("take_journal_photo"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(Modifier.size(7.dp))
                Text(if (hasPhoto) "Retake" else "Take photo")
            }
        }
        val chooseButton: @Composable (Modifier) -> Unit = { modifier ->
            OutlinedButton(
                onClick = onChoosePhoto,
                modifier = modifier.testTag("choose_journal_photo"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.size(7.dp))
                Text("Choose photo")
            }
        }

        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                takeButton(Modifier.fillMaxWidth())
                chooseButton(Modifier.fillMaxWidth())
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                takeButton(Modifier.weight(1f))
                chooseButton(Modifier.weight(1f))
            }
        }
    }
}

const val NEW_JOURNAL_ENTRY_ID = -1
