package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val issueCount: Int
)

data class Issue(
    val id: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val iconName: String,
    val questions: List<Question>,
    val searchTerms: List<String> = emptyList()
)

data class Question(
    val id: String,
    val text: String,
    val options: List<Option>
)

data class Option(
    val id: String,
    val letter: String, // "A", "B", "C", "D"
    val text: String,
    val isDangerous: Boolean = false,
    val dangerWarning: String? = null,
    val causeWeights: Map<String, Int> = emptyMap() // causeId -> weight
)

data class Cause(
    val id: String,
    val issueId: String,
    val title: String,
    val description: String,
    val solutions: List<SolutionStep>,
    val difficulty: RepairDifficulty = RepairDifficulty.BEGINNER,
    val estimatedMinutes: Int = 15,
    val toolsRequired: List<String> = emptyList(),
    val seekProfessionalHelpWhen: String? = null
)

enum class RepairDifficulty(val label: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

data class SolutionStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val imageDrawableName: String? = null,
    val tipOrWarning: String? = null
)

data class DiagnosticResult(
    val issue: Issue,
    val mostLikelyCause: Cause,
    val confidencePercentage: Int,
    val alternativeCauses: List<Cause>,
    val userAnswers: Map<String, String>, // questionId -> optionText
    val triggeredDangerWarning: String? = null
)
