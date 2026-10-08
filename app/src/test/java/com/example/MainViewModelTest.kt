package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MainViewModelTest {
    @Test
    fun `next recommendation follows ranked diagnosis order`() {
        val viewModel = MainViewModel(ApplicationProvider.getApplicationContext<Application>())
        viewModel.startTroubleshooting("issue_webcam_01")
        viewModel.selectOption("q_webcam_1", "opt_webcam_1a")
        viewModel.selectOption("q_webcam_2", "opt_webcam_2a")

        val result = viewModel.evaluateResult()

        assertEquals("cause_webcam_privacy", result?.mostLikelyCause?.id)
        assertEquals("cause_webcam_driver", viewModel.getNextRecommendedCause("cause_webcam_privacy")?.id)
        assertEquals(null, viewModel.getNextRecommendedCause("cause_webcam_driver"))
        assertEquals(null, viewModel.getNextRecommendedCause("missing-cause"))
    }
}
