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

    // Bookmark methods
    fun insertBookmark(bookmark: Bookmark): Boolean {
        val db = writableDatabase
        
        // Check if already exists
        val cursor = db.rawQuery("SELECT * FROM bookmarks WHERE lessonId = ? AND isActive = 1", arrayOf(bookmark.lessonId.toString()))
        if (cursor.moveToFirst()) {
            cursor.close()
            return false // Duplicate found
        }
        cursor.close()

        val values = ContentValues().apply {
            put("lessonId", bookmark.lessonId)
            put("subjectName", bookmark.subjectName)
            put("teacherName", bookmark.teacherName)
            put("weekNumber", bookmark.weekNumber)
            put("title", bookmark.title)
            put("courseCode", bookmark.courseCode)
            put("bookmarkedAt", bookmark.bookmarkedAt)
            put("isActive", if (bookmark.isActive) 1 else 0)
        }

        val result = db.insert("bookmarks", null, values)
        return result != -1L
    }

    fun getAllBookmarks(): List<Bookmark> {
        val bookmarks = mutableListOf<Bookmark>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM bookmarks WHERE isActive = 1 ORDER BY bookmarkedAt DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val bookmark = Bookmark(
                    id = cursor.getInt(0),
                    lessonId = cursor.getInt(1),
                    subjectName = cursor.getString(2),
                    teacherName = cursor.getString(3),
                    weekNumber = cursor.getInt(4),
                    title = cursor.getString(5),
                    courseCode = cursor.getString(6),
                    bookmarkedAt = cursor.getLong(7),
                    isActive = cursor.getInt(8) == 1
                )
                bookmarks.add(bookmark)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return bookmarks
    }

    fun removeBookmark(lessonId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("isActive", 0)
        }
        val result = db.update("bookmarks", values, "lessonId = ?", arrayOf(lessonId.toString()))
        return result > 0
    }

    fun isBookmarked(lessonId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM bookmarks WHERE lessonId = ? AND isActive = 1", arrayOf(lessonId.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // Voice note methods
    fun insertVoiceNote(voiceNote: VoiceNote): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("lessonId", voiceNote.lessonId)
            put("subjectName", voiceNote.subjectName)
            put("audioFilePath", voiceNote.audioFilePath)
            put("duration", voiceNote.duration)
            put("title", voiceNote.title)
            put("description", voiceNote.description)
            put("recordedAt", voiceNote.recordedAt)
            put("isActive", if (voiceNote.isActive) 1 else 0)
        }

        val result = db.insert("voice_notes", null, values)
        return result != -1L
    }

    fun getAllVoiceNotes(): List<VoiceNote> {
        val voiceNotes = mutableListOf<VoiceNote>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM voice_notes WHERE isActive = 1 ORDER BY recordedAt DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val voiceNote = VoiceNote(
                    id = cursor.getInt(0),
                    lessonId = cursor.getInt(1),
                    subjectName = cursor.getString(2),
                    audioFilePath = cursor.getString(3),
                    duration = cursor.getLong(4),
                    title = cursor.getString(5),
                    description = cursor.getString(6),
                    recordedAt = cursor.getLong(7),
                    isActive = cursor.getInt(8) == 1
                )
                voiceNotes.add(voiceNote)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return voiceNotes
    }

    fun getVoiceNotesByLessonId(lessonId: Int): List<VoiceNote> {
        val voiceNotes = mutableListOf<VoiceNote>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM voice_notes WHERE lessonId = ? AND isActive = 1 ORDER BY recordedAt DESC", arrayOf(lessonId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val voiceNote = VoiceNote(
                    id = cursor.getInt(0),
                    lessonId = cursor.getInt(1),
                    subjectName = cursor.getString(2),
                    audioFilePath = cursor.getString(3),
                    duration = cursor.getLong(4),
                    title = cursor.getString(5),
                    description = cursor.getString(6),
                    recordedAt = cursor.getLong(7),
                    isActive = cursor.getInt(8) == 1
                )
                voiceNotes.add(voiceNote)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return voiceNotes
    }

    // Deleted content methods for recovery
    fun insertDeletedContent(deletedContent: DeletedContent): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("originalId", deletedContent.originalId)
            put("contentType", deletedContent.contentType)
            put("title", deletedContent.title)
            put("content", deletedContent.content)
            put("metadata", deletedContent.metadata)
            put("deletedAt", deletedContent.deletedAt)
            put("deletedBy", deletedContent.deletedBy)
            put("canRestore", if (deletedContent.canRestore) 1 else 0)
        }

        val result = db.insert("deleted_content", null, values)
        return result != -1L
    }

    fun getAllDeletedContent(): List<DeletedContent> {
        val deletedContent = mutableListOf<DeletedContent>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM deleted_content WHERE canRestore = 1 ORDER BY deletedAt DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val content = DeletedContent(
                    id = cursor.getInt(0),
                    originalId = cursor.getInt(1),
                    contentType = cursor.getString(2),
                    title = cursor.getString(3),
                    content = cursor.getString(4),
                    metadata = cursor.getString(5),
                    deletedAt = cursor.getLong(6),
                    deletedBy = cursor.getString(7),
                    canRestore = cursor.getInt(8) == 1
                )
                deletedContent.add(content)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return deletedContent
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
