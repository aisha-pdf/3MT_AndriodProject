package com.example.andriodproject.viewModel

import com.example.andriodproject.roomDB.BookDatabase
import com.example.andriodproject.roomDB.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class Repository(private val db: BookDatabase) {
    //Create
    suspend fun insertBook (book: Book){
        db.dao.insertBook(book)
    }

    //Read
    fun getAllBooks(): StateFlow<List<Book>> {
        return db.dao.getAllBooks().stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    //Update
    suspend fun updateBook (book: Book){
        db.dao.updateBook(book)
    }

    //Delete
    suspend fun deleteBook (book: Book){
        db.dao.deleteBook(book)
    }
}