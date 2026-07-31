package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FixItDao {

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
