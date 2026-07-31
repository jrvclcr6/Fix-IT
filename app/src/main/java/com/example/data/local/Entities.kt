package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val issueId: String,
    val issueTitle: String,
    val categoryName: String,
    val diagnosedCauseTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val issueId: String,
    val issueTitle: String,
    val categoryName: String,
    val diagnosedCauseTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true
)

@Entity(tableName = "unfinished_sessions")
data class UnfinishedSessionEntity(
    @PrimaryKey val issueId: String,
    val issueTitle: String,
    val categoryName: String,
    val currentQuestionIndex: Int,
    val selectedOptionIdsJson: String, // e.g. "q1:optA,q2:optB"
    val timestamp: Long = System.currentTimeMillis()
)
