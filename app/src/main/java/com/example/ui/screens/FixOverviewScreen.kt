package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SolutionStep
import com.example.data.model.Cause
import com.example.ui.MainViewModel
import com.example.ui.theme.FixItBlueContainer
import com.example.ui.theme.FixItBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixOverviewScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onViewStepByStepGuideClick: (String) -> Unit
) {
    val resultState by viewModel.diagnosticResult.collectAsStateWithLifecycle()
    val result = resultState
    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No diagnostic result available")
        }
        return
    }

    val cause = result.mostLikelyCause

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fix It! (Solutions)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF001D36)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF001D36)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF3F6FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .testTag("fix_overview_screen"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Follow these steps to fix the problem:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001D36)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item { RepairReadinessCard(cause) }
                    items(cause.solutions) { step ->
                        SolutionStepOverviewCard(step = step)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.setGuideCause(cause)
                    onViewStepByStepGuideClick(cause.id)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005CBB)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("view_step_by_step_guide_button")
            ) {
                Text(
                    text = "View Step-by-Step Guide",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RepairReadinessCard(cause: Cause) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD3E4FF)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().testTag("repair_readiness_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Before you start", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF001D36))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${cause.difficulty.label} • About ${cause.estimatedMinutes} min • ${cause.solutions.size} steps",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF005CBB)
            )
            if (cause.toolsRequired.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tools: ${cause.toolsRequired.joinToString()}", fontSize = 13.sp, color = Color(0xFF001D36))
            }
            cause.seekProfessionalHelpWhen?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Get help when: $it", fontSize = 13.sp, color = Color(0xFF7A1D1D), lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun SolutionStepOverviewCard(step: SolutionStep) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD3E4FF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF005CBB),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${step.stepNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF001D36)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.description,
                    fontSize = 13.sp,
                    color = Color(0xFF43474E),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
