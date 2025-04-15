package com.qadis.lessonmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qadis.lessonmaker.Adapters.StudentNotesAdapter
import com.qadis.lessonmaker.Model.WeekNo
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.LoginPage
import com.qadis.lessonmaker.databinding.ActivityStudentNotesBinding

class StudentNotes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val bind = ActivityStudentNotesBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val recyclerView: RecyclerView=bind.recyclerViewNotes
        recyclerView.layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy= StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }



//      ShowLessonByID
        val WeekNo=listOf(
            WeekNo("WEEK 1"),
            WeekNo("WEEK 2"),
            WeekNo("WEEK 3"),
            WeekNo("WEEK 4"),
            WeekNo("WEEK 5"),
            WeekNo("WEEK 6"),
            WeekNo("WEEK 7"),
            WeekNo("WEEK 8"),
            WeekNo("WEEK 9"),
            WeekNo("WEEK 10"),
            WeekNo("WEEK 11"),
            WeekNo("WEEK 12"),
            WeekNo("WEEK 13"),
            WeekNo("WEEK 14"),
            WeekNo("WEEK 15"),
            WeekNo("WEEK 16"),
        )


        recyclerView.adapter = StudentNotesAdapter(WeekNo)
        {

            selectedToc->Toast.makeText(this@StudentNotes,"Opening Notes For ${selectedToc.WeekNo}", Toast.LENGTH_SHORT).show()
            val showWeekNotes = Intent(this@StudentNotes, ShowNotesActivity::class.java)
            startActivity(showWeekNotes)
        }

    }
}

private fun getEnrolledCourses()
{
    

}