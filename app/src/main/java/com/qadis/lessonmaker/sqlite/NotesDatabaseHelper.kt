package com.qadis.lessonmaker.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.qadis.lessonmaker.sqlite.DownloadedNote

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "lesson_notes.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NOTES = "notes"
        const val COLUMN_LESSON_ID = "lessonId"
        const val COLUMN_SUBJECT_NAME = "subjectName"
        const val COLUMN_WEEK_NUMBER = "weekNumber"
        const val COLUMN_HTML_CONTENT = "htmlContent"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_LESSON_ID INTEGER PRIMARY KEY,
                $COLUMN_SUBJECT_NAME TEXT,
                $COLUMN_WEEK_NUMBER INTEGER,
                $COLUMN_HTML_CONTENT TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun saveNote(note: DownloadedNote): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LESSON_ID, note.lessonId)
            put(COLUMN_SUBJECT_NAME, note.subjectName)
            put(COLUMN_WEEK_NUMBER, note.weekNumber)
            put(COLUMN_HTML_CONTENT, note.htmlContent)
        }

        val result = db.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result != -1L
    }

    fun getNoteByLessonId(lessonId: Int): DownloadedNote? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_LESSON_ID = ?",
            arrayOf(lessonId.toString())
        )

        return if (cursor.moveToFirst()) {
            val subject = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME))
            val week = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEEK_NUMBER))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HTML_CONTENT))
            cursor.close()
            db.close()
            DownloadedNote(lessonId, subject, week, content)
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    fun getAllDownloadedNotes(): List<DownloadedNote> {
        val db = readableDatabase
        val noteList = mutableListOf<DownloadedNote>()

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES ORDER BY $COLUMN_WEEK_NUMBER", null)
        if (cursor.moveToFirst()) {
            do {
                val lessonId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LESSON_ID))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME))
                val week = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEEK_NUMBER))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HTML_CONTENT))

                noteList.add(DownloadedNote(lessonId, subject, week, content))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return noteList
    }
}