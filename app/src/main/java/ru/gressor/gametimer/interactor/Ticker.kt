package ru.gressor.gametimer.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Ticker(
    startValue: Long,
    private val finishValue: Long = 0L,
    private var isRunning: Boolean = false,
    private val pauseMillis: Long = 1000L
) {
    private var job: Job? = null

    private val _flow: MutableStateFlow<Long> = MutableStateFlow(startValue)
    private var _finished = false

    val flow = _flow.asStateFlow()
    val finished get() = _finished

    private var value = startValue
        set(value) {
            // TODO need to be checked?
            if (_flow.value != value) _flow.value = value
            field = value
        }

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

    private fun getValueThenNext(): Long {
        return value.also { value++ }
    }
}