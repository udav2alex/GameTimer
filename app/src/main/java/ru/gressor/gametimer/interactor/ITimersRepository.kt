package ru.gressor.gametimer.interactor

import ru.gressor.gametimer.entities.BaseTimer

interface ITimersRepository {
    fun storeTimer(timer: BaseTimer)
    fun deleteTimer(timer: BaseTimer)
    fun getAllTimers(): List<BaseTimer>
}