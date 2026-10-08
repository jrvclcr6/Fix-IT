package com.example

import android.app.Application
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.TroubleshootingRepository
import com.example.ui.MainViewModel
import com.example.ui.components.FixItBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.CategoryCard
import com.example.ui.screens.RepairOutcomeScreen
import com.example.ui.screens.RepairJournalEditorScreen
import com.example.ui.screens.NEW_JOURNAL_ENTRY_ID
import com.example.ui.theme.FixITTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w320dp-h640dp")
class UiExperienceTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `category artwork card remains usable at 320 dp in dark theme`() {
        val category = TroubleshootingRepository().getCategoryById("cat_hardware")!!
        composeRule.setContent {
            FixITTheme(darkTheme = true) {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(currentDensity.density, 1.3f)) {
                    CategoryCard(category = category, onClick = {}, modifier = Modifier.width(148.dp))
                }
            }
        }

        composeRule.onNodeWithTag("category_cat_hardware").assertIsDisplayed()
        composeRule.onNodeWithText("Hardware Problems").assertIsDisplayed()
        composeRule.onNodeWithText("7 Issues").assertIsDisplayed()
    }

    @Test
    fun `repair outcome offers all three accessible choices`() {
        val viewModel = MainViewModel(ApplicationProvider.getApplicationContext<Application>())
        viewModel.startTroubleshooting("issue_webcam_01")
        viewModel.selectOption("q_webcam_1", "opt_webcam_1a")
        viewModel.selectOption("q_webcam_2", "opt_webcam_2a")
        viewModel.evaluateResult()

        composeRule.setContent {
            FixITTheme {
                RepairOutcomeScreen(
                    issueId = "issue_webcam_01",
                    causeId = "cause_webcam_privacy",
                    viewModel = viewModel,
                    onBackClick = {},
                    onTryNextCause = {},
                    onRestartDiagnosis = {},
                    onHome = {},
                    onHistory = {}
                )
            }
        }

        composeRule.onNodeWithTag("repair_outcome_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("outcome_fixed").assertIsDisplayed()
        composeRule.onNodeWithTag("outcome_not_fixed").assertIsDisplayed()
        composeRule.onNodeWithTag("outcome_skipped").assertIsDisplayed()
    }

    @Test
    fun `repair journal editor exposes issue solution status and photo actions`() {
        val viewModel = MainViewModel(ApplicationProvider.getApplicationContext<Application>())

        composeRule.setContent {
            FixITTheme {
                RepairJournalEditorScreen(
                    entryId = NEW_JOURNAL_ENTRY_ID,
                    viewModel = viewModel,
                    onBackClick = {},
                    onSaved = {}
                )
            }
        }

        composeRule.onNodeWithTag("journal_editor_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("journal_title").assertIsDisplayed()
        composeRule.onNodeWithTag("journal_issue").assertIsDisplayed()
        composeRule.onNodeWithTag("journal_actions").assertIsDisplayed()
        composeRule.onNodeWithTag("journal_solution").assertIsDisplayed()
        composeRule.onNodeWithTag("journal_status_in_progress").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("journal_status_resolved").assertIsDisplayed()
        composeRule.onNodeWithTag("take_journal_photo").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("choose_journal_photo").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("save_journal_entry").assertIsDisplayed()
    }

    @Test
    fun `bottom navigation keeps all destinations readable at compact width and large text`() {
        composeRule.setContent {
            FixITTheme {
                val currentDensity = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(currentDensity.density, 1.3f)) {
                    FixItBottomBar(currentRoute = Screen.Bookmarks.route, onNavigate = {})
                }
            }
        }

        composeRule.onNodeWithText("Saved").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_home").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_bookmarks").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_journal").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_history").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_about").assertIsDisplayed()
    }

    @Test
    fun `journal warns before discarding an unsaved entry`() {
        val viewModel = MainViewModel(ApplicationProvider.getApplicationContext<Application>())

        composeRule.setContent {
            FixITTheme {
                RepairJournalEditorScreen(
                    entryId = NEW_JOURNAL_ENTRY_ID,
                    viewModel = viewModel,
                    onBackClick = {},
                    onSaved = {}
                )
            }
        }

        composeRule.onNodeWithTag("journal_title").performTextInput("Charging issue")
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Discard unsaved changes?").assertIsDisplayed()
        composeRule.onNodeWithText("Keep editing").assertIsDisplayed()
    }
}
