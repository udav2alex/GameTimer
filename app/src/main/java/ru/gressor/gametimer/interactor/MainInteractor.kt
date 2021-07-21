package ru.gressor.gametimer.interactor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MainInteractor(
    private val timersRepository: ITimersRepository
) {
    private val _updateListStatusFlow = MutableStateFlow(0L)
    val updateListStatusFlow: StateFlow<Long> = _updateListStatusFlow.asStateFlow()

    private val _timersList = mutableListOf<ActiveTimer>()
    val timersList: List<ActiveTimer> = _timersList


    fun storeTimer(timer: ActiveTimer) {
        _timersList.indexOf(timer).let {
            if (it < 0) {
                _timersList.add(timer)
            }
        }
        _updateListStatusFlow.value = System.currentTimeMillis()
    }

    fun toggleTimerById(id: UUID) {
        _timersList.find {
            it.id == id
        }?.let {
            if (it.ticker.isRunning) {
                it.ticker.stop()
            } else {
                _timersList.forEach { timer ->
                    if (timer.ticker.isRunning) timer.ticker.stop()
                }
                it.ticker.start()
            }
            storeTimer(it)
        }
    }

    fun deleteTimerById(id: UUID) {
        _timersList.find {
            it.id == id
        }?.let {
            _timersList.remove(it)
            _updateListStatusFlow.value = System.currentTimeMillis()
        }
    }

    // TODO implement backup timers later
    private fun updateTimersList() {
        _timersList.clear()
        _timersList.addAll(timersRepository.getAllTimers())
        _updateListStatusFlow.value = System.currentTimeMillis()
    }
}