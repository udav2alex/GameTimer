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

    init {
        updateTimersList()
    }

    fun storeTimer(timer: ActiveTimer) {
        timersRepository.storeTimer(timer)
        updateTimersList()
    }

    fun toggleTimerById(id: UUID) {
        _timersList.find {
            it.id == id
        }?.let {
            if (it.ticker.isRunning()) {
                it.ticker.stop()
            } else {
                it.ticker.start()
            }
            storeTimer(it)
        }
    }

    fun deleteTimerById(id: UUID) {
        _timersList.find {
            it.id == id
        }?.let {
            timersRepository.deleteTimer(it)
            updateTimersList()
        }
    }

    private fun updateTimersList() {
        _timersList.clear()
        _timersList.addAll(timersRepository.getAllTimers())
        _updateListStatusFlow.value = System.currentTimeMillis()
    }
}