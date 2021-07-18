package ru.gressor.gametimer.interactor

import ru.gressor.gametimer.repository.StoredTimer

interface ITimersRepository {
    fun storeTimer(timer: ActiveTimer)
    fun deleteTimer(timer: ActiveTimer)
    fun getAllTimers(): List<ActiveTimer>
}