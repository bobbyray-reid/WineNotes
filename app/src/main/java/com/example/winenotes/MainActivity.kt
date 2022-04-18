package com.example.winenotes

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.AppDatabase_Impl
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var adapter : MyAdapter
    private val notes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            applicationContext, layoutManager.orientation
        )
        binding.recyclerview.addItemDecoration(dividerItemDecoration)

        adapter = MyAdapter()
        binding.recyclerview.adapter = adapter

        loadAllNotes()
    }

    private fun loadAllNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getAllNotesByTitle()

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getAllNotesByDate() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.noteDao()
            val results = dao.getAllNotesByDate()

            withContext(Dispatchers.Main) {
                notes.clear()
                notes.addAll(results)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add_note){
            addNewNote()
            return true
        }else if(item.itemId == R.id.menu_title_sort){
            loadAllNotes()
            return true
        }else if(item.itemId == R.id.menu_date_sort){
            getAllNotesByDate()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    private val startForAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->

            if(result.resultCode == Activity.RESULT_OK){
                loadAllNotes()
            }
        }

    private val startForUpdateResults =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->

            if(result.resultCode == Activity.RESULT_OK){
                loadAllNotes()
            }
        }

    private fun addNewNote() {
        val intent = Intent(applicationContext, NotesActivity::class.java)
        intent.putExtra(
            getString(R.string.intent_purpose_key),
            getString(R.string.intent_purpose_add_note)
        )
        startForAddResult.launch(intent)
    }

    inner class MyViewHolder(val view: TextView) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener{

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val intent = Intent(applicationContext, DataActivity::class.java)

            intent.putExtra(
                getString(R.string.intent_purpose_key),
                getString(R.string.intent_purpose_update_note)
            )

            val note = notes[adapterPosition]
            intent.putExtra(
                getString(R.string.intent_key_note_id),
                note.id
            )
            startForUpdateResults.launch(intent)
        }

        override fun onLongClick(v: View?): Boolean {
            val note = notes[adapterPosition]

            val builder = AlertDialog.Builder(v!!.context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete " +
                "${note.title}?")
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok){
                    dialogInterface, whichButton ->

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getDatabase(applicationContext)
                            .noteDao().deleteNote(note)
                        loadAllNotes()
                    }
                }
            builder.show()
            return true
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_view, parent, false) as TextView
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val note = notes[position]
            val date = note.lastModified

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val dateInDatabase : Date = parser.parse(date)
            val displayFormat = SimpleDateFormat("HH:mm a MM/yyyy")
            val displayDate : String = displayFormat.format(dateInDatabase)

            holder.view.setText(
                "${note.title}\n${displayDate}"
            )
        }

        override fun getItemCount(): Int {
            return notes.size
        }

    }


}