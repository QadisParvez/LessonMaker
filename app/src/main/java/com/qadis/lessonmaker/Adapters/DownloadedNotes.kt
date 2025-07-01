package com.qadis.lessonmaker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.databinding.ListItemDownloadedNotsBinding
import com.qadis.lessonmaker.sqlite.DownloadedNote

class DownloadedNotesAdapter(
    private val notes: List<DownloadedNote>,
    private val onViewClick: (DownloadedNote) -> Unit,
    private val onShareClick: (DownloadedNote) -> Unit
) : RecyclerView.Adapter<DownloadedNotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ListItemDownloadedNotsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ListItemDownloadedNotsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.binding.SubjectName.text = note.subjectName
        holder.binding.teachersName.text = "Week ${note.weekNumber}"

        holder.binding.openSubjectNotes.setOnClickListener {
            onViewClick(note)
        }

        holder.binding.Share.setOnClickListener {
            onShareClick(note)
        }
    }

    override fun getItemCount(): Int = notes.size
}