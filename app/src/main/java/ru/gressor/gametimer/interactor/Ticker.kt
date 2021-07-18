package ru.gressor.gametimer.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Ticker(
    val startValue: Int,
    private val finishValue: Int = 0,
    private var isRunning: Boolean = false,
    private val pauseMillis: Long = 1000L
) {
    private var job: Job? = null

    private val _flow: MutableStateFlow<Int> = MutableStateFlow(startValue)
    private var _finished = false

    val flow: StateFlow<Int> = _flow.asStateFlow()
    val finished get() = _finished

    private var value = startValue
        set(value) {
            // TODO need to be checked?
            if (_flow.value != value) _flow.value = value
            field = value
        }

    fun isRunning() = isRunning

    fun start() {
        isRunning = true
        job = CoroutineScope(Dispatchers.IO).launch {
            while (!_finished && value != finishValue) {
                delay(pauseMillis)
                getValueThenNext()
            }
            _finished = true
        }
    }

    fun stop() {
        isRunning = false
        try {
            job?.cancel("Self cancelled...")
        } catch (e: Throwable) {
            // TODO
            println("Ticker Throwable: $e")
        }
    }

    private fun getValueThenNext(): Int {
        return value.also { value++ }
    }
}