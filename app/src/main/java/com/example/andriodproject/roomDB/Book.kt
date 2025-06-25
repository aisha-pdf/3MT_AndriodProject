package com.example.andriodproject.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    val title: String,
    val author: String,
    val status: String,
    @PrimaryKey (autoGenerate = true)
    val bookID: Int = 0

)
