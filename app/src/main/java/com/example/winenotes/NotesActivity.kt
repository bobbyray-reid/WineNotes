package com.example.winenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding
import com.example.winenotes.databinding.ActivityNotesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding

    private var purpose : String? = ""
    private var noteId : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        purpose = intent.getStringExtra(
            getString(R.string.intent_purpose_key)
        )

        setTitle("${purpose} Note")
    }

    override fun onBackPressed() {
        val title = binding.titleEditText.text.toString().trim()
        if(title.isEmpty()){
            Toast.makeText(applicationContext,
            "Title cannot be empty.", Toast.LENGTH_LONG).show()
            return
        }

        val notes = binding.notesEditText.text.toString().trim()
        if(notes.isEmpty()){
            Toast.makeText(applicationContext,
            "Notes cannot be empty.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()

            val now : Date = Date()
            val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            var dateString : String = databaseDateFormat.format(now)

            var resultId : Long

            if(purpose.equals(getString(R.string.intent_purpose_add_note))){
                val note = Note(0, title, notes, dateString)
                resultId = noteDao.addNote(note)
            }else{
                TODO()
            }
        }
        super.onBackPressed()
    }
}