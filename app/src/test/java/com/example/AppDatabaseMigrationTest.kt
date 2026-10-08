package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.HistoryEntity
import com.example.data.local.RepairJournalEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AppDatabaseMigrationTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val databaseName = "fixit-migration-test.db"

    @After
    fun cleanUp() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun `migration 1 to 2 preserves local data and adds nullable outcome columns`() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(databaseName)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE bookmarks (issueId TEXT NOT NULL PRIMARY KEY, issueTitle TEXT NOT NULL, categoryName TEXT NOT NULL, diagnosedCauseTitle TEXT NOT NULL, timestamp INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, issueId TEXT NOT NULL, issueTitle TEXT NOT NULL, categoryName TEXT NOT NULL, diagnosedCauseTitle TEXT NOT NULL, timestamp INTEGER NOT NULL, isCompleted INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE unfinished_sessions (issueId TEXT NOT NULL PRIMARY KEY, issueTitle TEXT NOT NULL, categoryName TEXT NOT NULL, currentQuestionIndex INTEGER NOT NULL, selectedOptionIdsJson TEXT NOT NULL, timestamp INTEGER NOT NULL)")
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )

        val db = helper.writableDatabase
        db.execSQL("INSERT INTO bookmarks VALUES ('issue_pwr_01','Power','Hardware','Power delivery',1)")
        db.execSQL("INSERT INTO history VALUES (1,'issue_pwr_01','Power','Hardware','Power delivery',2,1)")
        db.execSQL("INSERT INTO unfinished_sessions VALUES ('issue_wifi_01','Wi-Fi','Internet',0,'q:o',3)")

        AppDatabase.MIGRATION_1_2.migrate(db)

        db.query("SELECT repairOutcome, attemptedCauseId, attemptedCauseTitle FROM history WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertNull(cursor.getString(0))
            assertNull(cursor.getString(1))
            assertNull(cursor.getString(2))
        }
        db.query("SELECT COUNT(*) FROM bookmarks").use { cursor -> cursor.moveToFirst(); assertEquals(1, cursor.getInt(0)) }
        db.query("SELECT COUNT(*) FROM history").use { cursor -> cursor.moveToFirst(); assertEquals(1, cursor.getInt(0)) }
        db.query("SELECT COUNT(*) FROM unfinished_sessions").use { cursor -> cursor.moveToFirst(); assertEquals(1, cursor.getInt(0)) }
        helper.close()
    }

    @Test
    fun `latest diagnosis stores repair outcome and attempted cause`() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.fixItDao()
        dao.insertHistory(
            HistoryEntity(
                issueId = "issue_mouse_01",
                issueTitle = "Mouse or touchpad is not working correctly",
                categoryName = "Peripheral Problems",
                diagnosedCauseTitle = "Mouse connection"
            )
        )

        assertEquals(
            1,
            dao.updateLatestHistoryOutcome(
                issueId = "issue_mouse_01",
                causeId = "cause_mouse_connection",
                causeTitle = "Mouse power, surface, receiver, or connection fault",
                outcome = "FIXED"
            )
        )

        val saved = dao.getAllHistory().first().single()
        assertEquals("FIXED", saved.repairOutcome)
        assertEquals("cause_mouse_connection", saved.attemptedCauseId)
        db.close()
    }

    @Test
    fun `migration 2 to 3 adds repair journal without changing existing data`() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(databaseName)
                .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE bookmarks (issueId TEXT NOT NULL PRIMARY KEY, issueTitle TEXT NOT NULL, categoryName TEXT NOT NULL, diagnosedCauseTitle TEXT NOT NULL, timestamp INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, issueId TEXT NOT NULL, issueTitle TEXT NOT NULL, categoryName TEXT NOT NULL, diagnosedCauseTitle TEXT NOT NULL, timestamp INTEGER NOT NULL, isCompleted INTEGER NOT NULL, repairOutcome TEXT, attemptedCauseId TEXT, attemptedCauseTitle TEXT)")
                        db.execSQL("CREATE TABLE unfinished_sessions (issueId TEXT NOT NULL PRIMARY KEY, issueTitle TEXT NOT NULL, categoryName TEXT NOT NULL, currentQuestionIndex INTEGER NOT NULL, selectedOptionIdsJson TEXT NOT NULL, timestamp INTEGER NOT NULL)")
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )

        val db = helper.writableDatabase
        db.execSQL("INSERT INTO history VALUES (1,'issue_pwr_01','Power','Hardware','Power delivery',2,1,'FIXED','cause_power','Power delivery')")

        AppDatabase.MIGRATION_2_3.migrate(db)

        db.query("SELECT COUNT(*) FROM repair_journal").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }
        db.query("SELECT repairOutcome FROM history WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("FIXED", cursor.getString(0))
        }
        helper.close()
    }

    @Test
    fun `repair journal entry can be saved updated and deleted`() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.fixItDao()

        val id = dao.saveJournalEntry(
            RepairJournalEntity(
                title = "Laptop would not charge",
                issueDetails = "No charging light",
                actionsTaken = "Tried another outlet",
                solution = "Replaced the adapter",
                isResolved = true,
                photoPath = "/private/photo.jpg",
                createdAt = 10,
                updatedAt = 20
            )
        ).toInt()

        val saved = dao.getAllJournalEntries().first().single()
        assertEquals("Laptop would not charge", saved.title)
        assertTrue(saved.isResolved)
        assertEquals("/private/photo.jpg", saved.photoPath)

        dao.saveJournalEntry(saved.copy(title = "Charging issue", updatedAt = 30))
        assertEquals("Charging issue", dao.getAllJournalEntries().first().single().title)

        dao.deleteJournalEntryById(id)
        assertTrue(dao.getAllJournalEntries().first().isEmpty())
        db.close()
    }
}
