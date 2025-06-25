package com.example.andriodproject.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface RoomDao {
    //Create
    @Insert
    suspend fun insertBook (book: Book)

    //Read
    @Query("SELECT * FROM Book")
    fun getAllBooks(): Flow<List<Book>>

    //Update
    @Update
    suspend fun updateBook (book: Book)

    //delete
    @Delete
    suspend fun deleteBook (book: Book)
}