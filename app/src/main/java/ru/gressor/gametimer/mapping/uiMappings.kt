package ru.gressor.gametimer.mapping

import ru.gressor.gametimer.entities.BaseTimer
import ru.gressor.gametimer.states.TimerState
import java.text.DecimalFormat

object DF: DecimalFormat("00")

fun BaseTimer.toState(): TimerState = TimerState(id, name, this.secondsToString(), running)

fun BaseTimer.secondsToString() = StringBuilder().also {
    var seconds = (this.seconds)
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