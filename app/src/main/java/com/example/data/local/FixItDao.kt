package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FixItDao {

    // Repair Journal
    @Query("SELECT * FROM repair_journal ORDER BY updatedAt DESC, id DESC")
    fun getAllJournalEntries(): Flow<List<RepairJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJournalEntry(entry: RepairJournalEntity): Long

    @Query("DELETE FROM repair_journal WHERE id = :id")
    suspend fun deleteJournalEntryById(id: Int)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE issueId = :issueId)")
    fun isBookmarked(issueId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE issueId = :issueId")
    suspend fun deleteBookmarkByIssueId(issueId: String)

    // History
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Query(
        """
        UPDATE history
        SET repairOutcome = :outcome,
            attemptedCauseId = :causeId,
            attemptedCauseTitle = :causeTitle
        WHERE id = (
            SELECT id FROM history
            WHERE issueId = :issueId
            ORDER BY timestamp DESC, id DESC
            LIMIT 1
        )
        """
    )
    suspend fun updateLatestHistoryOutcome(
        issueId: String,
        causeId: String,
        causeTitle: String,
        outcome: String
    ): Int

    // Unfinished Sessions
    @Query("SELECT * FROM unfinished_sessions ORDER BY timestamp DESC")
    fun getAllUnfinishedSessions(): Flow<List<UnfinishedSessionEntity>>

    @Query("SELECT * FROM unfinished_sessions WHERE issueId = :issueId LIMIT 1")
    fun getUnfinishedSession(issueId: String): Flow<UnfinishedSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUnfinishedSession(session: UnfinishedSessionEntity)

    @Query("DELETE FROM unfinished_sessions WHERE issueId = :issueId")
    suspend fun deleteUnfinishedSession(issueId: String)
}
