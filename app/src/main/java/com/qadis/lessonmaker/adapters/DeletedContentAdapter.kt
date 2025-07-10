package com.qadis.lessonmaker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.R
import com.qadis.lessonmaker.model.DeletedContent
import java.text.SimpleDateFormat
import java.util.*

class DeletedContentAdapter(
    private val deletedContent: List<DeletedContent>,
    private val onRestoreClick: (DeletedContent) -> Unit
) : RecyclerView.Adapter<DeletedContentAdapter.DeletedContentViewHolder>() {

    class DeletedContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.deletedContentTitle)
        val typeTextView: TextView = view.findViewById(R.id.deletedContentType)
        val dateTextView: TextView = view.findViewById(R.id.deletedContentDate)
        val deletedByTextView: TextView = view.findViewById(R.id.deletedContentBy)
        val restoreButton: TextView = view.findViewById(R.id.restoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deleted_content, parent, false)
        return DeletedContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeletedContentViewHolder, position: Int) {
        val content = deletedContent[position]
        
        holder.titleTextView.text = content.title
        holder.typeTextView.text = "Type: ${content.contentType.replace("_", " ").capitalize()}"
        holder.deletedByTextView.text = "Deleted by: ${content.deletedBy}"
        
        // Format date
        val date = Date(content.deletedAt)
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.dateTextView.text = "Deleted: ${formatter.format(date)}"
        
        // Handle restore button
        if (content.canRestore) {
            holder.restoreButton.visibility = View.VISIBLE
            holder.restoreButton.setOnClickListener {
                onRestoreClick(content)
            }
        } else {
            holder.restoreButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = deletedContent.size
}