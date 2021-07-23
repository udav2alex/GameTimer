package ru.gressor.gametimer.domain

interface ITimersRepository {
    fun storeTimer(timer: ActiveTimer)
    fun deleteTimer(timer: ActiveTimer)
    fun getAllTimers(): List<ActiveTimer>
}