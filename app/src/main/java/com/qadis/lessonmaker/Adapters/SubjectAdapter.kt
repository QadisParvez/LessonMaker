package com.qadis.lessonmaker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.R
import com.qadis.lessonmaker.model.Subject

class SubjectAdapter(
    private val SubjectList: List<Subject>,
    private val onItemClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.SubjectName)
        val teacherName: TextView = itemView.findViewById(R.id.teachersName)
        val openNotes: Button = itemView.findViewById(R.id.openSubjectNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_dashboard_componets1, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = SubjectList[position]
        holder.subjectName.text = subject.subjectName
        holder.teacherName.text = subject.teacherName

        holder.openNotes.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Open Notes button clicked", Toast.LENGTH_SHORT).show()
            Log.d("SubjectAdapter", "CourseCode: ${subject.courseCode}")

            onItemClick(subject)
        }
    }

    override fun getItemCount(): Int = SubjectList.size
}
