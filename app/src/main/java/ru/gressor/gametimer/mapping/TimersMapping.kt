package ru.gressor.gametimer.mapping

import ru.gressor.gametimer.interactor.ActiveTimer
import ru.gressor.gametimer.interactor.Ticker
import ru.gressor.gametimer.repository.StoredTimer
import ru.gressor.gametimer.ui.StatedTimer
import java.text.DecimalFormat

object DF : DecimalFormat("00")



fun StatedTimer.toStored() = StoredTimer(id)

fun StoredTimer.toStated(): StatedTimer = StatedTimer(id, name, seconds.secondsToString(), running)



fun StatedTimer.toActive() = ActiveTimer(id, name, Ticker(0))

fun ActiveTimer.toStated() = StatedTimer(id, name, ticker.flow.value.secondsToString(), ticker.isRunning())

fun ActiveTimer.toStored() = StoredTimer(id, name, ticker.flow.value, ticker.isRunning())

fun StoredTimer.toActive() = ActiveTimer(id, name, Ticker(seconds))

fun Int.secondsToString() = StringBuilder().also {
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