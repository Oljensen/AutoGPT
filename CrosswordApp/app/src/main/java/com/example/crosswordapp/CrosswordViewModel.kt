package com.example.crosswordapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Direction { ACROSS, DOWN }

data class Clue(val word: String, val hint: String)

class CrosswordViewModel : ViewModel() {
    val size = 10
    private val _grid = MutableStateFlow(List(size) { MutableList<Char?>(size) { null } })
    val grid: StateFlow<List<List<Char?>>> = _grid.asStateFlow()

    private val _clues = MutableStateFlow<List<Clue>>(emptyList())
    val clues: StateFlow<List<Clue>> = _clues.asStateFlow()

    fun addWord(word: String, hint: String, row: Int, col: Int, direction: Direction) {
        if (word.isEmpty() || row !in 0 until size || col !in 0 until size) return
        val mutableGrid = _grid.value.map { it.toMutableList() }
        val letters = word.uppercase()
        letters.forEachIndexed { index, c ->
            val r = row + if (direction == Direction.DOWN) index else 0
            val cc = col + if (direction == Direction.ACROSS) index else 0
            if (r in 0 until size && cc in 0 until size) {
                mutableGrid[r][cc] = c
            }
        }
        _grid.value = mutableGrid
        _clues.value = _clues.value + Clue(word, hint)
    }
}
