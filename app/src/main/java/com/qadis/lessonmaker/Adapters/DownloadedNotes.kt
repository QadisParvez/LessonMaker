package com.qadis.lessonmaker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Model.Notes
import com.qadis.lessonmaker.R
class DownloadedNotesAdapter(
    private val listOfNotes: List<Notes>,
    private val onItemClick: (Notes) -> Unit
) : RecyclerView.Adapter<DownloadedNotesAdapter.NotesViewHolder>() {

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weekText: TextView = itemView.findViewById(R.id.weekText)
        val openNotesCard: CardView = itemView.findViewById(R.id.openNotes)
        val openButton: Button = itemView.findViewById(R.id.openSubjectNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_downloaded_nots, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = listOfNotes[position]
        holder.weekText.text = note.subjectName

        holder.openButton.setOnClickListener {
            onItemClick(note)
        }
    }

    override fun getItemCount(): Int = listOfNotes.size
}
