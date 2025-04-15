package com.qadis.lessonmaker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Model.TOC
import com.qadis.lessonmaker.Model.WeekNo
import com.qadis.lessonmaker.R

class StudentNotesAdapter(
//    private val listOfToc: List<TOC>,
    private val listOfWeekNo:List<WeekNo>,
    private val onItemClick: (WeekNo) -> Unit,

    ) : RecyclerView.Adapter<StudentNotesAdapter.NotesViewHolder>() {
    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val toc: TextView = itemView.findViewById<TextView>(R.id.toc)
        val weekNo: TextView = itemView.findViewById<TextView>(R.id.weekText)
        val openNotes: CardView = itemView.findViewById<CardView>(R.id.openNotes)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StudentNotesAdapter.NotesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_notes_components, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StudentNotesAdapter.NotesViewHolder, position: Int,
    ) {
//        val tableOfContent = listOfToc[position]
        val weekNo = listOfWeekNo[position]
//        holder.toc.text = tableOfContent.toc
        holder.weekNo.text = weekNo.WeekNo

        holder.openNotes.setOnClickListener {
            onItemClick(weekNo)
        }

    }


    override fun getItemCount(): Int {
        return listOfWeekNo.size
    }

}
