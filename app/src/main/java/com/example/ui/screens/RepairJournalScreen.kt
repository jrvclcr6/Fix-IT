package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.RepairJournalEntity
import com.example.ui.MainViewModel
import com.example.ui.components.FixItBottomBar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class JournalFilter {
    ALL,
    IN_PROGRESS,
    RESOLVED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairJournalScreen(
    viewModel: MainViewModel,
    onAddEntry: () -> Unit,
    onEditEntry: (Int) -> Unit,
    onNavigate: (String) -> Unit,
    currentRoute: String
) {
    val entries by viewModel.journalEntries.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(JournalFilter.ALL) }
    var expandedEntryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingDelete by remember { mutableStateOf<RepairJournalEntity?>(null) }

    val filteredEntries = remember(entries, query, selectedFilter) {
        val normalizedQuery = query.trim()
        entries.filter { entry ->
            val matchesStatus = when (selectedFilter) {
                JournalFilter.ALL -> true
                JournalFilter.IN_PROGRESS -> !entry.isResolved
                JournalFilter.RESOLVED -> entry.isResolved
            }
            val matchesQuery = normalizedQuery.isBlank() || listOf(
                entry.title,
                entry.issueDetails,
                entry.actionsTaken,
                entry.solution
            ).any { it.contains(normalizedQuery, ignoreCase = true) }
            matchesStatus && matchesQuery
        }
    }
    val resolvedCount = entries.count { it.isResolved }
    val inProgressCount = entries.size - resolvedCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Repair Journal", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            if (entries.isEmpty()) "Your own issues, fixes, and solutions" else "${entries.size} saved ${if (entries.size == 1) "entry" else "entries"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { FixItBottomBar(currentRoute = currentRoute, onNavigate = onNavigate) },
        floatingActionButton = {
            if (entries.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAddEntry,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_journal_entry")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add journal entry")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (entries.isEmpty()) {
            EmptyJournal(
                onAddEntry = onAddEntry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("journal_screen")
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("journal_screen"),
                contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Search your journal") },
                            placeholder = { Text("Issue, fix, or solution") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = if (query.isNotEmpty()) ({
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear journal search")
                                }
                            }) else null,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("journal_search")
                        )
                        Text(
                            "Filter by repair status",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("journal_filters")
                    ) {
                        item {
                            JournalFilterChip(
                                label = "All (${entries.size})",
                                selected = selectedFilter == JournalFilter.ALL,
                                onClick = { selectedFilter = JournalFilter.ALL },
                                testTag = "journal_filter_all"
                            )
                        }
                        item {
                            JournalFilterChip(
                                label = "In progress ($inProgressCount)",
                                selected = selectedFilter == JournalFilter.IN_PROGRESS,
                                onClick = { selectedFilter = JournalFilter.IN_PROGRESS },
                                testTag = "journal_filter_in_progress"
                            )
                        }
                        item {
                            JournalFilterChip(
                                label = "Resolved ($resolvedCount)",
                                selected = selectedFilter == JournalFilter.RESOLVED,
                                onClick = { selectedFilter = JournalFilter.RESOLVED },
                                testTag = "journal_filter_resolved"
                            )
                        }
                    }
                }

                if (filteredEntries.isEmpty()) {
                    item {
                        NoJournalMatches(
                            onClear = {
                                query = ""
                                selectedFilter = JournalFilter.ALL
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(filteredEntries, key = { it.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            expanded = expandedEntryId == entry.id,
                            onToggleExpanded = {
                                expandedEntryId = if (expandedEntryId == entry.id) null else entry.id
                            },
                            onEdit = { onEditEntry(entry.id) },
                            onDelete = { pendingDelete = entry },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete this journal entry?") },
            text = { Text("\"${entry.title}\" and its attached photo will be removed from this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteJournalEntry(entry)
                        pendingDelete = null
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun JournalFilterChip(label: String, selected: Boolean, onClick: () -> Unit, testTag: String) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, maxLines = 1) },
        modifier = Modifier.testTag(testTag)
    )
}

@Composable
private fun EmptyJournal(onAddEntry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(28.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(88.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Start your repair journal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Document an issue, what you tried, and the solution so you can find it again later.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            Button(onClick = onAddEntry, modifier = Modifier.testTag("add_first_journal_entry")) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Create entry")
            }
        }
    }
}

@Composable
private fun NoJournalMatches(onClear: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth().heightIn(min = 220.dp).testTag("journal_no_matches")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(12.dp))
            Text("No matching entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Try another search or show every repair status.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onClear) { Text("Clear search and filters") }
        }
    }
}

@Composable
private fun JournalEntryCard(
    entry: RepairJournalEntity,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(entry.updatedAt) {
        SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(entry.updatedAt))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onToggleExpanded)
            .testTag("journal_entry_${entry.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!entry.photoPath.isNullOrBlank()) {
                AsyncImage(
                    model = File(entry.photoPath),
                    contentDescription = "Photo for ${entry.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (expanded) 220.dp else 140.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.height(14.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (entry.isResolved) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        if (entry.isResolved) "Resolved" else "In progress",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (entry.isResolved) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Text(formattedDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))
            Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            JournalSection(label = "Issue", value = entry.issueDetails, maxLines = if (expanded) Int.MAX_VALUE else 3)

            if (expanded && entry.actionsTaken.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                JournalSection(label = "What I tried", value = entry.actionsTaken)
            }
            if (expanded && entry.solution.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                JournalSection(label = if (entry.isResolved) "Solution" else "Next step", value = entry.solution)
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onToggleExpanded, modifier = Modifier.testTag("journal_toggle_${entry.id}")) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(if (expanded) "Less" else "Details")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit ${entry.title}")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete ${entry.title}", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun JournalSection(label: String, value: String, maxLines: Int = Int.MAX_VALUE) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}
