package com.qadis.lessonmaker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.qadis.lessonmaker.adapters.VoiceNoteAdapter
import com.qadis.lessonmaker.databinding.ActivityVoiceNotesBinding
import com.qadis.lessonmaker.model.VoiceNote
import com.qadis.lessonmaker.sqlite.NotesDatabaseHelper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VoiceNotesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVoiceNotesBinding
    private lateinit var voiceNoteAdapter: VoiceNoteAdapter
    private lateinit var dbHelper: NotesDatabaseHelper
    private val voiceNotesList = mutableListOf<VoiceNote>()
    
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false
    private var currentFilePath: String? = null
    private var recordingStartTime: Long = 0
    private var lessonId: Int = -1
    private var subjectName: String = ""
    
    private val handler = Handler(Looper.getMainLooper())
    private var recordingRunnable: Runnable? = null
    
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVoiceNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dbHelper = NotesDatabaseHelper(this)
        
        // Get lesson info from intent
        lessonId = intent.getIntExtra("lessonId", -1)
        subjectName = intent.getStringExtra("subjectName") ?: ""
        
        if (lessonId == -1) {
            Toast.makeText(this, "Invalid lesson ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Setup RecyclerView
        voiceNoteAdapter = VoiceNoteAdapter(voiceNotesList) { voiceNote ->
            playVoiceNote(voiceNote)
        }
        
        binding.voiceNotesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@VoiceNotesActivity)
            adapter = voiceNoteAdapter
        }
        
        // Setup buttons
        binding.recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        
        binding.backButton.setOnClickListener {
            finish()
        }
        
        // Load voice notes
        loadVoiceNotes()
        
        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }
    
    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            return
        }
        
        try {
            // Create filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "voice_note_${timestamp}.3gp"
            val audioDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "VoiceNotes")
            if (!audioDir.exists()) {
                audioDir.mkdirs()
            }
            currentFilePath = File(audioDir, fileName).absolutePath
            
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(currentFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                
                try {
                    prepare()
                    start()
                    isRecording = true
                    recordingStartTime = System.currentTimeMillis()
                    
                    binding.recordButton.text = "Stop Recording"
                    binding.recordingStatus.text = "Recording..."
                    
                    // Start timer
                    startRecordingTimer()
                    
                    Toast.makeText(this@VoiceNotesActivity, "Recording started", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this@VoiceNotesActivity, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting recording: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            
            val duration = System.currentTimeMillis() - recordingStartTime
            
            binding.recordButton.text = "Start Recording"
            binding.recordingStatus.text = "Recording stopped"
            
            // Stop timer
            recordingRunnable?.let { handler.removeCallbacks(it) }
            
            // Show dialog to save voice note
            showSaveVoiceNoteDialog(duration)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error stopping recording: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startRecordingTimer() {
        recordingRunnable = object : Runnable {
            override fun run() {
                if (isRecording) {
                    val elapsed = System.currentTimeMillis() - recordingStartTime
                    val seconds = elapsed / 1000
                    val minutes = seconds / 60
                    val remainingSeconds = seconds % 60
                    
                    binding.recordingStatus.text = "Recording: ${minutes}:${String.format("%02d", remainingSeconds)}"
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(recordingRunnable!!)
    }
    
    private fun showSaveVoiceNoteDialog(duration: Long) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_save_voice_note, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.descriptionEditText)
        
        AlertDialog.Builder(this)
            .setTitle("Save Voice Note")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                
                if (title.isNotEmpty()) {
                    saveVoiceNote(title, description, duration)
                } else {
                    Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Delete the recorded file if user cancels
                currentFilePath?.let { File(it).delete() }
            }
            .show()
    }
    
    private fun saveVoiceNote(title: String, description: String, duration: Long) {
        currentFilePath?.let { filePath ->
            val voiceNote = VoiceNote(
                lessonId = lessonId,
                subjectName = subjectName,
                audioFilePath = filePath,
                duration = duration,
                title = title,
                description = description
            )
            
            val success = dbHelper.insertVoiceNote(voiceNote)
            if (success) {
                Toast.makeText(this, "Voice note saved successfully", Toast.LENGTH_SHORT).show()
                loadVoiceNotes()
            } else {
                Toast.makeText(this, "Failed to save voice note", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun playVoiceNote(voiceNote: VoiceNote) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(voiceNote.audioFilePath)
                prepare()
                start()
                setOnCompletionListener {
                    Toast.makeText(this@VoiceNotesActivity, "Playback completed", Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(this, "Playing: ${voiceNote.title}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing voice note: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun loadVoiceNotes() {
        try {
            val voiceNotes = dbHelper.getVoiceNotesByLessonId(lessonId)
            voiceNotesList.clear()
            voiceNotesList.addAll(voiceNotes)
            voiceNoteAdapter.notifyDataSetChanged()
            
            if (voiceNotes.isEmpty()) {
                binding.recordingStatus.text = "No voice notes found"
            } else {
                binding.recordingStatus.text = "${voiceNotes.size} voice notes found"
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading voice notes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Audio recording permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Audio recording permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
        recordingRunnable?.let { handler.removeCallbacks(it) }
    }
}