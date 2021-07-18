package ru.gressor.gametimer.interactor

import kotlinx.coroutines.flow.*

class MainInteractor(
    private val timersRepository: ITimersRepository
) {
    private val _updateListFlow = MutableStateFlow(0L)
    val updateListFlow: StateFlow<Long> = _updateListFlow.asStateFlow()

    private val _timersList = mutableListOf<Flow<ActiveTimer>>()
    val timersList: List<Flow<ActiveTimer>> = _timersList

    init {
        updateTimersList()
    }

    fun storeTimer(timer: ActiveTimer) {
        timersRepository.storeTimer(timer)
        updateTimersList()
    }

    fun deleteTimer(timer: ActiveTimer) {
        timersRepository.deleteTimer(timer)
        updateTimersList()
    }

    private fun updateTimersList() {
        _timersList.clear()
        timersRepository.getAllTimers().forEach {
            _timersList.add(
                flow {
                    emit(it)
                }
            )
        }
        _updateListFlow.value = System.currentTimeMillis()
    }
}