package ru.gressor.gametimer.interactor

interface ITimersRepository {
    fun storeTimer(timer: ActiveTimer)
    fun deleteTimer(timer: ActiveTimer)
    fun getAllTimers(): List<ActiveTimer>
}