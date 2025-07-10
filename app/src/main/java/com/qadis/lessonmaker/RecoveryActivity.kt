package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qadis.lessonmaker.adapters.DeletedContentAdapter
import com.qadis.lessonmaker.databinding.ActivityRecoveryBinding
import com.qadis.lessonmaker.model.DeletedContent
import com.qadis.lessonmaker.sqlite.NotesDatabaseHelper

class RecoveryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRecoveryBinding
    private lateinit var deletedContentAdapter: DeletedContentAdapter
    private lateinit var dbHelper: NotesDatabaseHelper
    private val deletedContentList = mutableListOf<DeletedContent>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dbHelper = NotesDatabaseHelper(this)
        
        // Setup RecyclerView
        deletedContentAdapter = DeletedContentAdapter(deletedContentList) { deletedContent ->
            showRestoreDialog(deletedContent)
        }
        
        binding.deletedContentRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecoveryActivity)
            adapter = deletedContentAdapter
        }
        
        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }
        
        // Load deleted content
        loadDeletedContent()
    }
    
    private fun showRestoreDialog(deletedContent: DeletedContent) {
        AlertDialog.Builder(this)
            .setTitle("Restore Content")
            .setMessage("Do you want to restore '${deletedContent.title}'?")
            .setPositiveButton("Restore") { _, _ ->
                restoreContent(deletedContent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun restoreContent(deletedContent: DeletedContent) {
        // This would typically involve API calls to restore content
        // For now, we'll just show a message
        Toast.makeText(this, "Content restoration functionality will be implemented with backend integration", Toast.LENGTH_LONG).show()
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun loadDeletedContent() {
        try {
            val deletedContent = dbHelper.getAllDeletedContent()
            deletedContentList.clear()
            deletedContentList.addAll(deletedContent)
            deletedContentAdapter.notifyDataSetChanged()
            
            if (deletedContent.isEmpty()) {
                Toast.makeText(this, "No deleted content found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading deleted content: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}