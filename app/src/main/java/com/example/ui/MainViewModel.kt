package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.HistoryEntity
import com.example.data.local.UnfinishedSessionEntity
import com.example.data.model.Cause
import com.example.data.model.DiagnosticResult
import com.example.data.model.Issue
import com.example.data.repository.TroubleshootingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TroubleshootingRepository()
    private val dao = AppDatabase.getDatabase(application).fixItDao()

    // Room Flows
    val bookmarks: StateFlow<List<BookmarkEntity>> = dao.getAllBookmarks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val history: StateFlow<List<HistoryEntity>> = dao.getAllHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unfinishedSessions: StateFlow<List<UnfinishedSessionEntity>> = dao.getAllUnfinishedSessions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Diagnosis Session State
    private val _activeIssue = MutableStateFlow<Issue?>(null)
    val activeIssue: StateFlow<Issue?> = _activeIssue.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptions = MutableStateFlow<Map<String, String>>(emptyMap()) // questionId -> optionId
    val selectedOptions: StateFlow<Map<String, String>> = _selectedOptions.asStateFlow()

    private val _diagnosticResult = MutableStateFlow<DiagnosticResult?>(null)
    val diagnosticResult: StateFlow<DiagnosticResult?> = _diagnosticResult.asStateFlow()

    private val _activeCauseForGuide = MutableStateFlow<Cause?>(null)
    val activeCauseForGuide: StateFlow<Cause?> = _activeCauseForGuide.asStateFlow()

    private val _guideStepIndex = MutableStateFlow(0)
    val guideStepIndex: StateFlow<Int> = _guideStepIndex.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val repositoryData = repository

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startTroubleshooting(issueId: String) {
        val issue = repository.getIssueById(issueId) ?: return
        _activeIssue.value = issue
        _currentQuestionIndex.value = 0
        _selectedOptions.value = emptyMap()
        _diagnosticResult.value = null
    }

    fun selectOption(questionId: String, optionId: String) {
        val updated = _selectedOptions.value.toMutableMap()
        updated[questionId] = optionId
        _selectedOptions.value = updated

        // Auto-save unfinished session
        val issue = _activeIssue.value ?: return
        val category = repository.getCategoryById(issue.categoryId)
        val selectedOptionPairs = updated.entries.joinToString(",") { "${it.key}:${it.value}" }

        viewModelScope.launch {
            dao.saveUnfinishedSession(
                UnfinishedSessionEntity(
                    issueId = issue.id,
                    issueTitle = issue.title,
                    categoryName = category?.name ?: "General",
                    currentQuestionIndex = _currentQuestionIndex.value,
                    selectedOptionIdsJson = selectedOptionPairs
                )
            )
        }
    }

    fun goToNextQuestion(): Boolean {
        val issue = _activeIssue.value ?: return false
        val nextIndex = _currentQuestionIndex.value + 1
        if (nextIndex < issue.questions.size) {
            _currentQuestionIndex.value = nextIndex
            return true
        }
        return false // Reached end of questions
    }

    fun goToPreviousQuestion(): Boolean {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
            return true
        }
        return false
    }

    fun evaluateResult(): DiagnosticResult? {
        val issue = _activeIssue.value ?: return null
        val result = repository.evaluateDiagnosis(issue.id, _selectedOptions.value)
        _diagnosticResult.value = result

        if (result != null) {
            val category = repository.getCategoryById(issue.categoryId)
            viewModelScope.launch {
                // Remove from unfinished sessions
                dao.deleteUnfinishedSession(issue.id)

                // Save to History
                dao.insertHistory(
                    HistoryEntity(
                        issueId = issue.id,
                        issueTitle = issue.title,
                        categoryName = category?.name ?: "General",
                        diagnosedCauseTitle = result.mostLikelyCause.title,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
        return result
    }

    fun isIssueBookmarked(issueId: String): StateFlow<Boolean> {
        return dao.isBookmarked(issueId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    fun toggleBookmark(issueId: String, issueTitle: String, categoryName: String, causeTitle: String) {
        viewModelScope.launch {
            val currentlyBookmarked = dao.isBookmarked(issueId).firstOrNull() ?: false
            if (currentlyBookmarked) {
                dao.deleteBookmarkByIssueId(issueId)
            } else {
                dao.insertBookmark(
                    BookmarkEntity(
                        issueId = issueId,
                        issueTitle = issueTitle,
                        categoryName = categoryName,
                        diagnosedCauseTitle = causeTitle
                    )
                )
            }
        }
    }

    fun deleteHistoryItem(historyId: Int) {
        viewModelScope.launch {
            dao.deleteHistoryById(historyId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearHistory()
        }
    }

    fun resumeUnfinishedSession(session: UnfinishedSessionEntity) {
        val issue = repository.getIssueById(session.issueId) ?: return
        _activeIssue.value = issue
        _currentQuestionIndex.value = session.currentQuestionIndex.coerceIn(0, issue.questions.size - 1)

        val optionsMap = mutableMapOf<String, String>()
        if (session.selectedOptionIdsJson.isNotBlank()) {
            session.selectedOptionIdsJson.split(",").forEach { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    optionsMap[parts[0]] = parts[1]
                }
            }
        }
        _selectedOptions.value = optionsMap
    }

    fun setGuideCause(cause: Cause) {
        _activeCauseForGuide.value = cause
        _guideStepIndex.value = 0
    }

    fun setGuideStep(index: Int) {
        val cause = _activeCauseForGuide.value ?: return
        _guideStepIndex.value = index.coerceIn(0, cause.solutions.size - 1)
    }
}
