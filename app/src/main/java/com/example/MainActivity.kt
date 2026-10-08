package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.UnfinishedSessionEntity
import com.example.ui.MainViewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CategoryIssuesScreen
import com.example.ui.screens.DiagnosticQuestionScreen
import com.example.ui.screens.FixOverviewScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IssueDetailScreen
import com.example.ui.screens.ResultPossibleCauseScreen
import com.example.ui.screens.RepairOutcomeScreen
import com.example.ui.screens.RepairJournalEditorScreen
import com.example.ui.screens.RepairJournalScreen
import com.example.ui.screens.SafetyWarningScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StepByStepGuideScreen
import com.example.ui.screens.NEW_JOURNAL_ENTRY_ID
import com.example.ui.theme.FixITTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixITTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FixItApp()
                }
            }
        }
    }
}

@Composable
fun FixItApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onCategoryClick = { categoryId: String ->
                    navController.navigate(Screen.CategoryIssues.createRoute(categoryId))
                },
                onIssueClick = { issueId: String ->
                    navController.navigate(Screen.IssueDetail.createRoute(issueId))
                },
                onResumeSessionClick = { session: UnfinishedSessionEntity ->
                    viewModel.resumeUnfinishedSession(session)
                    navController.navigate(Screen.QuestionFlow.createRoute(session.issueId))
                },
                onNavigate = { route: String ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                },
                currentRoute = currentRoute
            )
        }

        // Category Issues
        composable(
            route = Screen.CategoryIssues.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            CategoryIssuesScreen(
                categoryId = categoryId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onIssueClick = { issueId ->
                    navController.navigate(Screen.IssueDetail.createRoute(issueId))
                }
            )
        }

        // Issue Detail
        composable(
            route = Screen.IssueDetail.route,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            IssueDetailScreen(
                issueId = issueId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onStartTroubleshootingClick = {
                    navController.navigate(Screen.QuestionFlow.createRoute(issueId))
                }
            )
        }

        // Diagnostic Question Flow
        composable(
            route = Screen.QuestionFlow.route,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            DiagnosticQuestionScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onTriggerSafetyWarning = {
                    navController.navigate(Screen.SafetyWarning.createRoute(issueId))
                },
                onSeeResultClick = {
                    navController.navigate(Screen.ResultPossibleCause.createRoute(issueId))
                }
            )
        }

        // Safety Warning
        composable(
            route = Screen.SafetyWarning.route,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            SafetyWarningScreen(
                viewModel = viewModel,
                onProceedAnyway = {
                    navController.popBackStack()
                },
                onReturnHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Diagnostic Result - Possible Cause
        composable(
            route = Screen.ResultPossibleCause.route,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            ResultPossibleCauseScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onViewFixClick = { causeId ->
                    navController.navigate(Screen.FixOverview.createRoute(issueId, causeId))
                }
            )
        }

        // Fix Overview (Solutions list)
        composable(
            route = Screen.FixOverview.route,
            arguments = listOf(
                navArgument("issueId") { type = NavType.StringType },
                navArgument("causeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            val causeId = backStackEntry.arguments?.getString("causeId") ?: ""
            FixOverviewScreen(
                causeId = causeId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onViewStepByStepGuideClick = { causeId ->
                    navController.navigate(Screen.StepByStepGuide.createRoute(issueId, causeId))
                }
            )
        }

        // Step-by-Step Interactive Guide
        composable(
            route = Screen.StepByStepGuide.route,
            arguments = listOf(
                navArgument("issueId") { type = NavType.StringType },
                navArgument("causeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val causeId = backStackEntry.arguments?.getString("causeId") ?: ""
            StepByStepGuideScreen(
                causeId = causeId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onFinishGuideClick = {
                    val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
                    navController.navigate(Screen.RepairOutcome.createRoute(issueId, causeId))
                }
            )
        }

        composable(
            route = Screen.RepairOutcome.route,
            arguments = listOf(
                navArgument("issueId") { type = NavType.StringType },
                navArgument("causeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId") ?: ""
            val causeId = backStackEntry.arguments?.getString("causeId") ?: ""
            RepairOutcomeScreen(
                issueId = issueId,
                causeId = causeId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onTryNextCause = { nextCauseId ->
                    navController.navigate(Screen.FixOverview.createRoute(issueId, nextCauseId))
                },
                onRestartDiagnosis = {
                    viewModel.startTroubleshooting(issueId)
                    navController.navigate(Screen.QuestionFlow.createRoute(issueId))
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onHistory = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                }
            )
        }

        // Bookmarks
        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                viewModel = viewModel,
                onIssueClick = { issueId ->
                    viewModel.startTroubleshooting(issueId)
                    navController.navigate(Screen.IssueDetail.createRoute(issueId))
                },
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                },
                currentRoute = currentRoute
            )
        }

        // History
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onResumeSessionClick = { session ->
                    viewModel.resumeUnfinishedSession(session)
                    navController.navigate(Screen.QuestionFlow.createRoute(session.issueId))
                },
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                },
                currentRoute = currentRoute
            )
        }

        composable(Screen.RepairJournal.route) {
            RepairJournalScreen(
                viewModel = viewModel,
                onAddEntry = {
                    navController.navigate(Screen.RepairJournalEditor.createRoute(NEW_JOURNAL_ENTRY_ID))
                },
                onEditEntry = { entryId ->
                    navController.navigate(Screen.RepairJournalEditor.createRoute(entryId))
                },
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                },
                currentRoute = currentRoute
            )
        }

        composable(
            route = Screen.RepairJournalEditor.route,
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("entryId") ?: NEW_JOURNAL_ENTRY_ID
            RepairJournalEditorScreen(
                entryId = entryId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onSaved = { navController.popBackStack() }
            )
        }

        // About
        composable(Screen.About.route) {
            AboutScreen(
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                },
                currentRoute = currentRoute
            )
        }
    }
}
