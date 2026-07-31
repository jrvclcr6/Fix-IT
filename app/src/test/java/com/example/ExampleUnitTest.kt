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
        assertEquals(19, repository.getAllIssues().size)
        assertEquals(19, repository.getAllCategories().sumOf { it.issueCount })
    }

    @Test
    fun `symptom search finds expanded content and repair causes`() {
        assertTrue(repository.searchIssues("169.254").any { it.id == "issue_ethernet_01" })
        assertTrue(repository.searchIssues("charging port").any { it.id == "issue_battery_01" })
        assertTrue(repository.searchIssues("spooler").any { it.id == "issue_printer_01" })
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
}
