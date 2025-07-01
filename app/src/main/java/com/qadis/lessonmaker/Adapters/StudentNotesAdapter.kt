package com.qadis.lessonmaker.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.model.WeekNo
import com.qadis.lessonmaker.R

class StudentNotesAdapter(
    private var listOfWeekNo: List<WeekNo>,
    private val onItemClick: (WeekNo) -> Unit
) : RecyclerView.Adapter<StudentNotesAdapter.NotesViewHolder>() {

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weekText: TextView = itemView.findViewById(R.id.weekText)
        val openNotesCard: CardView = itemView.findViewById(R.id.openNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_notes_components, parent, false)
        return NotesViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val week = listOfWeekNo[position]
        holder.weekText.text = "Week ${week.weekNumber}"

        holder.openNotesCard.setOnClickListener {
            onItemClick(week)
        }
    }

    override fun getItemCount(): Int = listOfWeekNo.size

    // ✅ Add this method to update data
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<WeekNo>) {
        listOfWeekNo = newList
        notifyDataSetChanged()
    }
}
