package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object CategoryIssues : Screen("category_issues/{categoryId}") {
        fun createRoute(categoryId: String) = "category_issues/$categoryId"
    }
    object IssueDetail : Screen("issue_detail/{issueId}") {
        fun createRoute(issueId: String) = "issue_detail/$issueId"
    }
    object QuestionFlow : Screen("question_flow/{issueId}") {
        fun createRoute(issueId: String) = "question_flow/$issueId"
    }
    object SafetyWarning : Screen("safety_warning/{issueId}") {
        fun createRoute(issueId: String) = "safety_warning/$issueId"
    }
    object ResultPossibleCause : Screen("result_cause/{issueId}") {
        fun createRoute(issueId: String) = "result_cause/$issueId"
    }
    object FixOverview : Screen("fix_overview/{issueId}/{causeId}") {
        fun createRoute(issueId: String, causeId: String) = "fix_overview/$issueId/$causeId"
    }
    object StepByStepGuide : Screen("step_by_step_guide/{issueId}/{causeId}") {
        fun createRoute(issueId: String, causeId: String) = "step_by_step_guide/$issueId/$causeId"
    }
    object RepairOutcome : Screen("repair_outcome/{issueId}/{causeId}") {
        fun createRoute(issueId: String, causeId: String) = "repair_outcome/$issueId/$causeId"
    }
    object Bookmarks : Screen("bookmarks")
    object History : Screen("history")
    object RepairJournal : Screen("repair_journal")
    object RepairJournalEditor : Screen("repair_journal_editor/{entryId}") {
        fun createRoute(entryId: Int) = "repair_journal_editor/$entryId"
    }
    object About : Screen("about")
}
