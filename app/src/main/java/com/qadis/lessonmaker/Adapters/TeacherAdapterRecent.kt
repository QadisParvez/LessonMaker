package com.qadis.lessonmaker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Model.Teacher
import com.qadis.lessonmaker.R

class TeacherAdapterRecent(
    private val subjectName: List<Teacher>,
    private val onClick: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapterRecent.TeacherViewHolderRecent>() {

    class TeacherViewHolderRecent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSubjects: TextView = itemView.findViewById(R.id.subjectNames)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolderRecent {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacher_recenet_courses, parent, false) // FIXED Layout Name
        return TeacherViewHolderRecent(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolderRecent, position: Int) {
        val subjects = subjectName[position]
        holder.textViewSubjects.text = subjects.subjectName

        holder.itemView.setOnClickListener {
            onClick(subjects)
        }
    }

    override fun getItemCount(): Int {
        return subjectName.size
    }
}
