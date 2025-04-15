package com.qadis.lessonmaker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Model.Teacher
import com.qadis.lessonmaker.R


class TeacherAdapter (
    private val subjectName:List<Teacher>,
    private val onClick:(Teacher)-> Unit
): RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>(){


    class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSubjects: TextView = itemView.findViewById<TextView>(R.id.textViewSubjects)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.teacher_dashboard_components,parent,false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int, ) {
        val subjects=subjectName[position]
        holder.textViewSubjects.text=subjects.subjectName




      }

    override fun getItemCount(): Int {
        return subjectName.size
    }

}