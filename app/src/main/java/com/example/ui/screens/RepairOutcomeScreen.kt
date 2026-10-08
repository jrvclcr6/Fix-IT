package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.Cause
import com.example.data.model.RepairOutcome
import com.example.ui.MainViewModel
import com.example.ui.theme.FixItSuccess
import com.example.ui.theme.FixItSuccessContainer
import com.example.ui.theme.FixItWarning
import com.example.ui.theme.FixItWarningContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairOutcomeScreen(
    issueId: String,
    causeId: String,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onTryNextCause: (String) -> Unit,
    onRestartDiagnosis: () -> Unit,
    onHome: () -> Unit,
    onHistory: () -> Unit
) {
    val issue = viewModel.repositoryData.getIssueById(issueId)
    val cause = viewModel.repositoryData.getCauseById(causeId)
    var selectedOutcomeName by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedOutcome = selectedOutcomeName?.let(RepairOutcome::valueOf)

    if (issue == null || cause == null) {
        MissingContentState(message = "Repair summary is unavailable", onBackClick = onBackClick)
        return
    }

    val nextCause = viewModel.getNextRecommendedCause(causeId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repair check", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .testTag("repair_outcome_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedOutcome == null) {
                OutcomeQuestion(
                    cause = cause,
                    onSelect = { outcome ->
                        selectedOutcomeName = outcome.name
                        viewModel.recordRepairOutcome(issueId, cause, outcome)
                    }
                )
            } else {
                OutcomeResult(
                    outcome = selectedOutcome,
                    cause = cause,
                    nextCause = nextCause,
                    onTryNextCause = onTryNextCause,
                    onRestartDiagnosis = onRestartDiagnosis,
                    onHome = onHome,
                    onHistory = onHistory
                )
            }
        }
    }
}

@Composable
private fun OutcomeQuestion(cause: Cause, onSelect: (RepairOutcome) -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(84.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(22.dp)
        )
    }
    Spacer(Modifier.height(20.dp))
    Text(
        text = "Did this fix solve the problem?",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = cause.title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(28.dp))

    OutcomeButton(
        label = "Yes, it's fixed",
        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
        colors = ButtonDefaults.buttonColors(containerColor = FixItSuccess),
        testTag = "outcome_fixed",
        onClick = { onSelect(RepairOutcome.FIXED) }
    )
    Spacer(Modifier.height(12.dp))
    OutcomeButton(
        label = "No, it still happens",
        icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null) },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        testTag = "outcome_not_fixed",
        onClick = { onSelect(RepairOutcome.NOT_FIXED) }
    )
    Spacer(Modifier.height(12.dp))
    OutlinedButton(
        onClick = { onSelect(RepairOutcome.SKIPPED) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp).testTag("outcome_skipped")
    ) {
        Icon(Icons.Default.SkipNext, contentDescription = null)
        Spacer(Modifier.size(8.dp))
        Text("I skipped this repair", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OutcomeButton(
    label: String,
    icon: @Composable () -> Unit,
    colors: androidx.compose.material3.ButtonColors,
    testTag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = colors,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp).testTag(testTag)
    ) {
        icon()
        Spacer(Modifier.size(8.dp))
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OutcomeResult(
    outcome: RepairOutcome,
    cause: Cause,
    nextCause: Cause?,
    onTryNextCause: (String) -> Unit,
    onRestartDiagnosis: () -> Unit,
    onHome: () -> Unit,
    onHistory: () -> Unit
) {
    val fixed = outcome == RepairOutcome.FIXED
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (fixed) FixItSuccessContainer else FixItWarningContainer,
        modifier = Modifier.size(84.dp)
    ) {
        Icon(
            imageVector = if (fixed) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = null,
            tint = if (fixed) FixItSuccess else FixItWarning,
            modifier = Modifier.padding(22.dp)
        )
    }
    Spacer(Modifier.height(20.dp))
    Text(
        text = if (fixed) "Great — repair completed" else if (outcome == RepairOutcome.SKIPPED) "Repair skipped" else "Let's try the next likely cause",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = if (fixed) "The outcome was saved to your troubleshooting history." else "Your result was saved. FixIT can continue without repeating the diagnosis.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(24.dp))

    if (!fixed && nextCause != null) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("NEXT RECOMMENDED FIX", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(nextCause.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(nextCause.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Text("${nextCause.difficulty.label} • About ${nextCause.estimatedMinutes} min", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = { onTryNextCause(nextCause.id) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("try_next_fix")
        ) {
            Text("Try this fix", fontWeight = FontWeight.Bold)
        }
    } else if (!fixed) {
        Card(
            colors = CardDefaults.cardColors(containerColor = FixItWarningContainer),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("No more ranked causes", fontWeight = FontWeight.Bold, color = Color(0xFF7A4B00))
                Spacer(Modifier.height(6.dp))
                Text(
                    cause.seekProfessionalHelpWhen?.let { "Get qualified help when: $it" }
                        ?: "Review the symptoms and run the diagnosis again if the computer's behavior has changed.",
                    color = Color(0xFF5A3A00)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(onClick = onRestartDiagnosis, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Review symptoms", fontWeight = FontWeight.Bold)
        }
    }

    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onHistory, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("History", fontWeight = FontWeight.Bold)
        }
        Button(onClick = onHome, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Home", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MissingContentState(message: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBackClick) { Text("Go back") }
    }
}
