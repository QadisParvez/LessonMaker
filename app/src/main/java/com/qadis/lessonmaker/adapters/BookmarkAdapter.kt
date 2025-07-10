package com.qadis.lessonmaker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.R
import com.qadis.lessonmaker.model.Bookmark
import java.text.SimpleDateFormat
import java.util.*

class BookmarkAdapter(
    private val bookmarks: List<Bookmark>,
    private val onBookmarkClick: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.bookmarkTitle)
        val subjectTextView: TextView = view.findViewById(R.id.bookmarkSubject)
        val teacherTextView: TextView = view.findViewById(R.id.bookmarkTeacher)
        val weekTextView: TextView = view.findViewById(R.id.bookmarkWeek)
        val dateTextView: TextView = view.findViewById(R.id.bookmarkDate)
        val courseCodeTextView: TextView = view.findViewById(R.id.bookmarkCourseCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        
        holder.titleTextView.text = bookmark.title
        holder.subjectTextView.text = bookmark.subjectName
        holder.teacherTextView.text = "Teacher: ${bookmark.teacherName}"
        holder.weekTextView.text = "Week: ${bookmark.weekNumber}"
        holder.courseCodeTextView.text = bookmark.courseCode
        
        // Format date
        val date = Date(bookmark.bookmarkedAt)
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        holder.dateTextView.text = "Bookmarked: ${formatter.format(date)}"
        
        holder.itemView.setOnClickListener {
            onBookmarkClick(bookmark)
        }
    }

    override fun getItemCount() = bookmarks.size
}