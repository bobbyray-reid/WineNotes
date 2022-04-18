package com.example.winenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.winenotes.databinding.ActivityMainBinding
import com.example.winenotes.databinding.ActivityNotesBinding

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
        super.onBackPressed()
    }
}