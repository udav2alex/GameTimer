package ru.gressor.gametimer.domain

data class TickerState(
    var currentValue: Long = 0L,
    var startValue: Long = 0L,
    var finishValue: Long = 0L,
    var isRunning: Boolean = false,
    var isFinished: Boolean = false
)