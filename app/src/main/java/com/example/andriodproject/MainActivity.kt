package com.example.andriodproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.andriodproject.roomDB.Book
import com.example.andriodproject.roomDB.BookDatabase
import com.example.andriodproject.ui.theme.AndriodProjectTheme
import com.example.andriodproject.viewModel.BookViewModel
import com.example.andriodproject.viewModel.Repository
import com.example.andriodproject.roomDB.RoomDao

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            BookDatabase::class.java,
            "books.db"
        ).build()
    }

    private val viewModel by viewModels<BookViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookViewModel(Repository(db)) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndriodProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookManagerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun BookManagerScreen(viewModel: BookViewModel) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Unread") }
    val bookList by viewModel.allBooks.collectAsState()

    // Snack bar state
    val snackbarHostState = remember { SnackbarHostState() }
    var completedBookTitle by remember { mutableStateOf<String?>(null) }

    // Show snackbar when a book is marked complete
    completedBookTitle?.let { title ->
        LaunchedEffect(title) {
            snackbarHostState.showSnackbar(
                message = "'$title' marked as complete!",
                duration = SnackbarDuration.Short
            )
            completedBookTitle = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Input Fields
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(text = "Title") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = author,
                onValueChange = { author = it },
                placeholder = { Text(text = "Author") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown for Status
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text(text = status)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Unread") }, onClick = {
                        status = "Unread"
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Reading") }, onClick = {
                        status = "Reading"
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Completed") }, onClick = {
                        status = "Completed"
                        expanded = false
                    })
                }
            }

            // Add Book Button
            Button(
                onClick = {
                    if (title.isNotBlank() && author.isNotBlank()) {
                        viewModel.insertBook(Book(title = title, author = author, status = status))
                        title = ""
                        author = ""
                        status = "Unread"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Book")
            }

            // Displaying the list of books
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                bookList.forEach { book ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Book information in a Row to take full width
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${book.title} by ${book.author} - ${book.status}",
                                modifier = Modifier
                                    .weight(1f),
                                maxLines = 2, // Allow text to wrap to 2 lines
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Buttons row below the text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            // Show "Start Reading" button if book is Unread
                            if (book.status == "Unread") {
                                Button(
                                    onClick = {
                                        viewModel.updateBook(
                                            book.copy(status = "Reading")
                                        )
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text("Start Reading")
                                }
                            }

                            // Show "Mark Complete" button if book is Reading or Unread
                            if (book.status != "Completed") {
                                Button(
                                    onClick = {
                                        viewModel.updateBook(
                                            book.copy(status = "Completed")
                                        )
                                        completedBookTitle = book.title
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text("Mark Complete")
                                }
                            }

                            Button(onClick = { viewModel.deleteBook(book) }) {
                                Text("Delete")
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookManagerScreen() {
    // Preview without ViewModel (Static Data)
    AndriodProjectTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Your Books:", style = MaterialTheme.typography.titleMedium)
            Text("Book Title 1 by Author 1 - Unread")
            Text("Book Title 2 by Author 2 - Completed")
        }
    }
}


