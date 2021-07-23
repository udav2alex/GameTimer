package ru.gressor.gametimer.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Ticker(
    val startValue: Long,
    private val finishValue: Long = 0L,
    isRunning: Boolean = false,
    private val pauseMillis: Long = 1000L
) {
    private var job: Job? = null
    var finished = false
        private set
    var running = isRunning
        private set
    private var currentValue = startValue

    private val _flow: MutableStateFlow<Long> = MutableStateFlow(startValue)
    val flow: StateFlow<Long> = _flow.asStateFlow()

    init {
        if (isRunning) start()
    }

    fun start() {
        if (finished) {
            resetValue()
            _flow.value = currentValue
        }

        running = true
        finished = false
        job = CoroutineScope(Dispatchers.IO).launch {
            while (!finished && currentValue > finishValue) {
                delay(pauseMillis)
                currentValue--

                if (currentValue == 0L) {
                    finished = true
                    running = false
                }
                _flow.value = currentValue
            }
        }
    }

    fun stop() {
        running = false
        try {
            job?.cancel("Self cancelled...")
        } catch (e: Throwable) {
            // job cancellation
        }
    }

    private fun resetValue() {
        currentValue = startValue
    }
}