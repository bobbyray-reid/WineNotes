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

}