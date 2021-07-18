package ru.gressor.gametimer.ui

import kotlinx.coroutines.flow.StateFlow
import java.util.*

data class StatedTimer(
    val id: UUID,
    var name: String = "",
    var timeFlow: StateFlow<String>,
    val initialSeconds: Int = 0,
    var running: Boolean = false
)