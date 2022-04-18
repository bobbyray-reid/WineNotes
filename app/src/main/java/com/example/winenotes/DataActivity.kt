package com.example.winenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DataActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDataBinding

    private var noteId : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        noteId = intent.getLongExtra(
            getString(R.string.intent_key_note_id),
            -1
        )

        CoroutineScope(Dispatchers.IO).launch {
            val note = AppDatabase.getDatabase(applicationContext)
                .noteDao()
                .getNote(noteId)

            val date = note.lastModified

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val dateInDatabase : Date = parser.parse(date)
            val displayFormat = SimpleDateFormat("HH:mm a MM/yyyy")
            val displayDate : String = displayFormat.format(dateInDatabase)

            withContext(Dispatchers.Main){
                binding.titleDisplayEditText.setText(note.title)
                binding.notesDisplayEditText.setText(note.notes)
                binding.lastModifiedTextView.setText(displayDate)
            }
        }
    }

    override fun onBackPressed() {
        val title = binding.titleDisplayEditText.text.toString().trim()
        if(title.isEmpty()){
            Toast.makeText(applicationContext,
                "Title cannot be empty.", Toast.LENGTH_LONG).show()
            return
        }

        var notes = binding.notesDisplayEditText.text.toString().trim()
        if(notes.isEmpty()){
            notes = ""
        }

        CoroutineScope(Dispatchers.IO).launch {
            val noteDao = AppDatabase.getDatabase(applicationContext).noteDao()

            val now : Date = Date()
            val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            databaseDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            var dateString : String = databaseDateFormat.format(now)


            val note = Note(noteId, title, notes, dateString)
            noteDao.updateNote(note)


            val intent = Intent()
            intent.putExtra(
                getString(R.string.intent_key_note_id),
                noteId
            )
            withContext(Dispatchers.Main){
                setResult(RESULT_OK, intent)
                super.onBackPressed()
            }
        }
    }
}