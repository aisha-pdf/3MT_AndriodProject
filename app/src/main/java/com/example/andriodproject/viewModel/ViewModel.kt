package com.example.andriodproject.viewModel

import androidx.lifecycle.ViewModel
import com.example.andriodproject.roomDB.Book
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val repository: Repository): ViewModel() {
    //Read
    val allBooks: StateFlow<List<Book>> = repository.getAllBooks()

    //Create
    fun insertBook(book: Book) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    //Update
    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    //Delete
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }
}