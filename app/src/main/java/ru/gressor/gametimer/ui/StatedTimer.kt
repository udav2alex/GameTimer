package ru.gressor.gametimer.ui

import java.util.*

data class StatedTimer(
    val id: UUID,
    var name: String = "",
    var time: String = "00:00:00",
    var running: Boolean = false
)