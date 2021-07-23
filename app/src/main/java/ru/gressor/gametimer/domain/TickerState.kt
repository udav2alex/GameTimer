package ru.gressor.gametimer.domain

data class TickerState(
    val currentValue: Long = 0L,
    val startValue: Long = 0L,
    val finishValue: Long = 0L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)