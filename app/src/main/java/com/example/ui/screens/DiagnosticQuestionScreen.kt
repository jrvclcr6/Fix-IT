package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Option
import com.example.ui.MainViewModel
import com.example.ui.theme.FixItBlueContainer
import com.example.ui.theme.FixItBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticQuestionScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onTriggerSafetyWarning: () -> Unit,
    onSeeResultClick: () -> Unit
) {
    val activeIssue by viewModel.activeIssue.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedOptions by viewModel.selectedOptions.collectAsStateWithLifecycle()

    val issue = activeIssue
    if (issue == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active issue")
        }
        return
    }

    val totalQuestions = issue.questions.size
    val currentQuestion = issue.questions.getOrNull(currentQuestionIndex)
    val selectedOptionId = currentQuestion?.let { selectedOptions[it.id] }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val moved = viewModel.goToPreviousQuestion()
                        if (!moved) onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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
                .padding(20.dp)
                .testTag("diagnostic_question_screen"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Step Progress Indicator
                val progress = (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (currentQuestion != null) {
                    Text(
                        text = currentQuestion.text,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Option Cards
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentQuestion.options) { option ->
                            val isSelected = selectedOptionId == option.id
                            OptionCard(
                                option = option,
                                isSelected = isSelected,
                                onSelect = {
                                    viewModel.selectOption(currentQuestion.id, option.id)
                                    if (option.isDangerous) {
                                        onTriggerSafetyWarning()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Button
            val isLastQuestion = currentQuestionIndex == totalQuestions - 1
            Button(
                onClick = {
                    if (selectedOptionId == null) return@Button
                    if (isLastQuestion) {
                        viewModel.evaluateResult()
                        onSeeResultClick()
                    } else {
                        viewModel.goToNextQuestion()
                    }
                },
                enabled = selectedOptionId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("next_or_result_button")
            ) {
                Text(
                    text = if (isLastQuestion) "See Result" else "Next Question",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedOptionId != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OptionCard(
    option: Option,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val letterBgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val letterTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("option_${option.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = letterBgColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = option.letter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = letterTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = option.text,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
