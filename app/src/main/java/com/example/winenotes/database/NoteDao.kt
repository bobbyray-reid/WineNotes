package com.example.winenotes.database

import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    fun addNote(note : Note) : Long

    @Update
    fun updateNote(note : Note)

    @Delete
    fun deleteNote(note : Note)

    @Query("SELECT * FROM note ORDER BY title")
    fun getAllNotesByTitle() : List<Note>

    @Query("SELECT * FROM note ORDER BY last_modified DESC")
    fun getAllNotesByDate() : List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun getNote(noteId : Long) : Note
}