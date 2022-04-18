package com.example.winenotes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Note(
    @PrimaryKey(autoGenerate = true) val id : Long,
    @ColumnInfo val title : String,
    @ColumnInfo val notes : String,
    @ColumnInfo(name = "last_modified") val lastModified : String
) {
    override fun toString(): String {
        return "${title} ${notes} ${lastModified} (${id})"
    }
}