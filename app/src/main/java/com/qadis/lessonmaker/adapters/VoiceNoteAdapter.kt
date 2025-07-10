package com.qadis.lessonmaker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.R
import com.qadis.lessonmaker.model.VoiceNote
import java.text.SimpleDateFormat
import java.util.*

class VoiceNoteAdapter(
    private val voiceNotes: List<VoiceNote>,
    private val onVoiceNoteClick: (VoiceNote) -> Unit
) : RecyclerView.Adapter<VoiceNoteAdapter.VoiceNoteViewHolder>() {

    class VoiceNoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.voiceNoteTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.voiceNoteDescription)
        val durationTextView: TextView = view.findViewById(R.id.voiceNoteDuration)
        val dateTextView: TextView = view.findViewById(R.id.voiceNoteDate)
        val subjectTextView: TextView = view.findViewById(R.id.voiceNoteSubject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceNoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voice_note, parent, false)
        return VoiceNoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoiceNoteViewHolder, position: Int) {
        val voiceNote = voiceNotes[position]
        
        holder.titleTextView.text = voiceNote.title
        holder.descriptionTextView.text = voiceNote.description
        holder.subjectTextView.text = voiceNote.subjectName
        
        // Format duration
        val duration = voiceNote.duration
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        holder.durationTextView.text = "Duration: ${minutes}:${String.format("%02d", seconds)}"
        
        // Format date
        val date = Date(voiceNote.recordedAt)
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.dateTextView.text = "Recorded: ${formatter.format(date)}"
        
        holder.itemView.setOnClickListener {
            onVoiceNoteClick(voiceNote)
        }
    }

    override fun getItemCount() = voiceNotes.size
}