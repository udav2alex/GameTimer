package ru.gressor.gametimer.states

data class TimerState(
    val id: Long = 0,
    var name: String = "",
    var time: String = "00:00:00",
    var running: Boolean = false
)