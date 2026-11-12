package com.example.bokhyllan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bokhyllan.ui.theme.BokhyllanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BokhyllanTheme {
                ShelfScreen()
            }
        }
    }
}

data class Book(
    val id: Int,
    val title: String,
    val read: Boolean = false,
)

data class ShelfUiState(
    val newTitle: String = "",
    val books: List<Book> = listOf(
        Book(1, "Dune"),
        Book(2, "Hobbiten", read = true),
        Book(3, "Sapiens"),
    ),
)

class ShelfViewModel : ViewModel() {
    var uiState by mutableStateOf(ShelfUiState())
        private set

    private var nextId = 4

    fun onTitleChange(text: String) {
        uiState = uiState.copy(newTitle = text)
    }

    fun addBook() {
        val title = uiState.newTitle.trim()
        if (title.isEmpty()) return
        uiState = uiState.copy(
            newTitle = "",
            books = uiState.books + Book(nextId, title),
        )
        nextId = nextId + 1
    }

    fun toggleRead(id: Int) {
        uiState = uiState.copy(
            books = uiState.books.map {
                if (it.id == id) it.copy(read = !it.read) else it
            }
        )
    }

    fun removeBook(id: Int) {
        uiState = uiState.copy(
            books = uiState.books.filter { it.id != id }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(viewModel: ShelfViewModel = viewModel()) {
    val state = viewModel.uiState
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bokhyllan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = state.newTitle,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Titel") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.addBook() },
                enabled = state.newTitle.isNotBlank(),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("Lägg till")
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.books, key = { it.id }) { book ->
                    BookRow(
                        book = book,
                        onToggle = { viewModel.toggleRead(book.id) },
                        onRemove = { viewModel.removeBook(book.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun BookRow(
    book: Book,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = book.read,
                onCheckedChange = { onToggle() },
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onRemove) {
                Text("Ta bort")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShelfScreenPreview() {
    BokhyllanTheme {
        ShelfScreen()
    }
}