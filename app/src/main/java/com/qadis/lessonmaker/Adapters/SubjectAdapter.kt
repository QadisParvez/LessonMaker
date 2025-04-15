package com.qadis.lessonmaker.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Model.Subject
import com.qadis.lessonmaker.R


public class SubjectAdapter (
    private val SubjectList:List<Subject>,
    private val onItemClick:(Subject)-> Unit
): RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {


    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById<TextView>(R.id.SubjectName)
        val teacherName: TextView = itemView.findViewById<TextView>(R.id.teachersName)
        val openNotes: Button = itemView.findViewById<Button>(R.id.openSubjectNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_dashboard_componets1,parent,false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int, ) {
        val subject = SubjectList[position]
        holder.subjectName.text=subject.subjectName
        holder.teacherName.text=subject.teacherName
        //button click Listener
        holder.openNotes.setOnClickListener{
            onItemClick(subject)
        }
    }

    override fun getItemCount(): Int {
        return SubjectList.size
    }


}
