package com.example.winenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.winenotes.databinding.ActivityMainBinding

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var purpose : String? = ""
    private var noteId : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        purpose = intent.getStringExtra(
            getString(R.string.intent_purpose_key)
        )

        setTitle("${purpose} Note")
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}