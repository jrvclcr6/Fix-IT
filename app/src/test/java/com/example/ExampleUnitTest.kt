package com.example

import com.example.data.repository.TroubleshootingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TroubleshootingRepositoryTest {
    private val repository = TroubleshootingRepository()

    @Test
    fun `catalog has no broken diagnostic or repair paths`() {
        assertEquals(emptyList<String>(), repository.validateCatalog())
        assertEquals(35, repository.getAllIssues().size)
        assertEquals(35, repository.getAllCategories().sumOf { it.issueCount })
        assertEquals(
            mapOf(
                "cat_hardware" to 7,
                "cat_software" to 6,
                "cat_internet" to 5,
                "cat_performance" to 4,
                "cat_display" to 3,
                "cat_peripheral" to 7,
                "cat_other" to 3
            ),
            repository.getAllCategories().associate { it.id to it.issueCount }
        )
    }

    @Test
    fun `symptom search finds expanded content and repair causes`() {
        assertTrue(repository.searchIssues("169.254").any { it.id == "issue_ethernet_01" })
        assertTrue(repository.searchIssues("charging port").any { it.id == "issue_battery_01" })
        assertTrue(repository.searchIssues("spooler").any { it.id == "issue_printer_01" })
        assertTrue(repository.searchIssues("kernel power").any { it.id == "issue_restart_01" })
        assertTrue(repository.searchIssues("pin unavailable").any { it.id == "issue_signin_01" })
        assertTrue(repository.searchIssues("overscan").any { it.id == "issue_scaling_01" })
        assertTrue(repository.searchIssues("touchpad disabled").any { it.id == "issue_mouse_01" })
    }

    @Test
    fun `diagnosis requires every answer and selects the strongest relevant cause`() {
        val issueId = "issue_battery_01"
        assertNull(repository.evaluateDiagnosis(issueId, mapOf("q_battery_1" to "opt_battery_1a")))

        val result = repository.evaluateDiagnosis(
            issueId,
            mapOf(
                "q_battery_1" to "opt_battery_1a",
                "q_battery_2" to "opt_battery_2c"
            )
        )

        assertNotNull(result)
        assertEquals("cause_battery_adapter", result?.mostLikelyCause?.id)
        assertTrue(result!!.confidencePercentage in 45..96)
        assertTrue(result.alternativeCauses.all { it.issueId == issueId })
    }

    @Test
    fun `dangerous answer carries its safety warning into the result`() {
        val result = repository.evaluateDiagnosis(
            "issue_battery_01",
            mapOf(
                "q_battery_1" to "opt_battery_1b",
                "q_battery_2" to "opt_battery_2a"
            )
        )

        assertTrue(result?.triggeredDangerWarning?.contains("SWOLLEN BATTERY") == true)
    }

    @Test
    fun `new issue ranks its strongest relevant cause`() {
        val result = repository.evaluateDiagnosis(
            "issue_webcam_01",
            mapOf(
                "q_webcam_1" to "opt_webcam_1a",
                "q_webcam_2" to "opt_webcam_2a"
            )
        )

        assertEquals("cause_webcam_privacy", result?.mostLikelyCause?.id)
        assertTrue(result?.alternativeCauses?.all { it.issueId == "issue_webcam_01" } == true)
    }

    @Test
    fun `scam answer interrupts with a safety warning`() {
        val result = repository.evaluateDiagnosis(
            "issue_malware_01",
            mapOf(
                "q_malware_1" to "opt_malware_1b",
                "q_malware_2" to "opt_malware_2c"
            )
        )

        assertTrue(result?.triggeredDangerWarning?.contains("DO NOT CALL") == true)
    }
}
