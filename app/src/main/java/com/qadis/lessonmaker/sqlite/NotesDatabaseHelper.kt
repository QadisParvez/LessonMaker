package com.qadis.lessonmaker.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.qadis.lessonmaker.sqlite.DownloadedNote
import com.qadis.lessonmaker.model.Bookmark
import com.qadis.lessonmaker.model.VoiceNote
import com.qadis.lessonmaker.model.DeletedContent

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "LessonNotes.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        // Original notes table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "lessonId INTEGER," +
                    "subjectName TEXT," +
                    "weekNumber INTEGER," +
                    "htmlContent TEXT)"
        )
        
        // Bookmarks table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS bookmarks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "lessonId INTEGER," +
                    "subjectName TEXT," +
                    "teacherName TEXT," +
                    "weekNumber INTEGER," +
                    "title TEXT," +
                    "courseCode TEXT," +
                    "bookmarkedAt INTEGER," +
                    "isActive INTEGER DEFAULT 1)"
        )
        
        // Voice notes table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS voice_notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "lessonId INTEGER," +
                    "subjectName TEXT," +
                    "audioFilePath TEXT," +
                    "duration INTEGER," +
                    "title TEXT," +
                    "description TEXT," +
                    "recordedAt INTEGER," +
                    "isActive INTEGER DEFAULT 1)"
        )
        
        // Deleted content table for recovery
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS deleted_content (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "originalId INTEGER," +
                    "contentType TEXT," +
                    "title TEXT," +
                    "content TEXT," +
                    "metadata TEXT," +
                    "deletedAt INTEGER," +
                    "deletedBy TEXT," +
                    "canRestore INTEGER DEFAULT 1)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add new tables for version 2
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS bookmarks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "lessonId INTEGER," +
                        "subjectName TEXT," +
                        "teacherName TEXT," +
                        "weekNumber INTEGER," +
                        "title TEXT," +
                        "courseCode TEXT," +
                        "bookmarkedAt INTEGER," +
                        "isActive INTEGER DEFAULT 1)"
            )
            
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS voice_notes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "lessonId INTEGER," +
                        "subjectName TEXT," +
                        "audioFilePath TEXT," +
                        "duration INTEGER," +
                        "title TEXT," +
                        "description TEXT," +
                        "recordedAt INTEGER," +
                        "isActive INTEGER DEFAULT 1)"
            )
            
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS deleted_content (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "originalId INTEGER," +
                        "contentType TEXT," +
                        "title TEXT," +
                        "content TEXT," +
                        "metadata TEXT," +
                        "deletedAt INTEGER," +
                        "deletedBy TEXT," +
                        "canRestore INTEGER DEFAULT 1)"
            )
        }
    }

    fun insertNote(note: DownloadedNote): Boolean {
        val db = writableDatabase

        // Check if already exists
        val cursor = db.rawQuery("SELECT * FROM notes WHERE lessonId = ?", arrayOf(note.lessonId.toString()))
        if (cursor.moveToFirst()) {
            cursor.close()
            return false // Duplicate found
        }
        cursor.close()

        val values = ContentValues().apply {
            put("lessonId", note.lessonId)
            put("subjectName", note.subjectName)
            put("weekNumber", note.weekNumber)
            put("htmlContent", note.htmlContent)
        }

        val result = db.insert("notes", null, values)
        return result != -1L
    }


    fun getAllDownloadedNotes(): List<DownloadedNote> {
        val notes = mutableListOf<DownloadedNote>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM notes", null)

        if (cursor.moveToFirst()) {
            do {
                val note = DownloadedNote(
                    id = cursor.getInt(0),
                    lessonId = cursor.getInt(1),
                    subjectName = cursor.getString(2),
                    weekNumber = cursor.getInt(3),
                    htmlContent = cursor.getString(4)
                )
                notes.add(note)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return notes
    }

    fun isNoteExists(lessonId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM notes WHERE lessonId = ?", arrayOf(lessonId.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }
}
