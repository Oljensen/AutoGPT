package com.example.crosswordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crosswordapp.ui.theme.CrosswordAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrosswordAppTheme {
                val viewModel: CrosswordViewModel = viewModel()
                CrosswordScreen(viewModel)
            }
        }
    }
}

@Composable
fun CrosswordScreen(viewModel: CrosswordViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { openDialog.value = true }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CrosswordGrid(viewModel)
            ClueList(viewModel)
        }
        if (openDialog.value) {
            AddWordDialog(viewModel, onDismiss = { openDialog.value = false })
        }
    }
}

@Composable
fun CrosswordGrid(viewModel: CrosswordViewModel) {
    val grid by viewModel.grid.collectAsState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(viewModel.size),
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        itemsIndexed(grid.flatten()) { index, cell ->
            val row = index / viewModel.size
            val col = index % viewModel.size
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, MaterialTheme.colors.primary)
            ) {
                Text(cell?.toString() ?: "")
            }
        }
    }
}

@Composable
fun ClueList(viewModel: CrosswordViewModel) {
    val clues by viewModel.clues.collectAsState()
    Column(modifier = Modifier.padding(8.dp)) {
        clues.forEachIndexed { index, clue ->
            Text(text = "${index + 1}. ${clue.hint}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddWordDialog(viewModel: CrosswordViewModel, onDismiss: () -> Unit) {
    var word by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var row by remember { mutableStateOf("0") }
    var col by remember { mutableStateOf("0") }
    var direction by remember { mutableStateOf(Direction.ACROSS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Legg til ord") },
        text = {
            Column {
                OutlinedTextField(value = word, onValueChange = { word = it }, label = { Text("Ord") })
                OutlinedTextField(value = hint, onValueChange = { hint = it }, label = { Text("Hint") })
                OutlinedTextField(value = row, onValueChange = { row = it }, label = { Text("Rad (0-${viewModel.size - 1})") })
                OutlinedTextField(value = col, onValueChange = { col = it }, label = { Text("Kolonne (0-${viewModel.size - 1})") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = direction == Direction.ACROSS,
                        onClick = { direction = Direction.ACROSS }
                    )
                    Text("Bortover")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = direction == Direction.DOWN,
                        onClick = { direction = Direction.DOWN }
                    )
                    Text("Nedover")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.addWord(word.trim(), hint.trim(), row.toInt(), col.toInt(), direction)
                onDismiss()
            }) { Text("Legg til") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Avbryt") }
        }
    )
}
