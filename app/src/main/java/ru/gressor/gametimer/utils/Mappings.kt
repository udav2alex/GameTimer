package ru.gressor.gametimer.utils

import ru.gressor.gametimer.domain.ActiveTimer
import ru.gressor.gametimer.domain.Ticker
import ru.gressor.gametimer.repository.StoredTimer
import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)

fun ActiveTimer.toStored() =
    StoredTimer(id, name, ticker.flow.value.currentValue, ticker.startValue, ticker.state.isRunning)

fun StoredTimer.toActive() = ActiveTimer(id, name, Ticker(time, 0))

fun Long.secondsToString() = StringBuilder().also {
    val days = this / 1000 / 3600 / 24
    if (days > 0) it.append("$days:")

    val hours = this / 1000 / 3600 % 24
    it.addTwoDigits(hours).append(":")

    val minutes = this / 1000 / 60 % 60
    it.addTwoDigits(minutes).append(":")

    val seconds = this / 1000 % 60
    it.addTwoDigits(seconds)
}.toString()

fun StringBuilder.addTwoDigits(value: Long): StringBuilder = this.apply {
    if (value < 10L) {
        append("0")
    }
    append(value)
}