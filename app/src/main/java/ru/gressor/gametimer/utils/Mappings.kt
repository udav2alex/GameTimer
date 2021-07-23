package ru.gressor.gametimer.utils

import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.Ticker
import ru.gressor.gametimer.repository.StoredTimer
import java.text.DecimalFormat
import java.util.*

object DF : DecimalFormat("00")

fun String.toUUID(): UUID = UUID.fromString(this)

fun ActiveTimer.toStored() =
    StoredTimer(id, name, ticker.flow.value, ticker.startValue, ticker.running)

fun StoredTimer.toActive() = ActiveTimer(id, name, Ticker(time, 0, running))

fun Long.secondsToString() = StringBuilder().also {
    var seconds = (this)
    val days = seconds / 3600 / 24
    if (days > 0) it.append("$days:")

    seconds -= days * 3600 * 24
    val hours = seconds / 3600
    it.append("${DF.format(hours)}:")

    seconds -= hours * 3600
    val minutes = seconds / 60
    it.append("${DF.format(minutes)}:")

    seconds -= minutes * 60
    it.append(DF.format(seconds))
}.toString()