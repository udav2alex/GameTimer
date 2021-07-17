package ru.gressor.gametimer.repository

import ru.gressor.gametimer.entities.BaseTimer

interface TimersRepository {
    fun storeTimer(timer: BaseTimer)
    fun getAllTimers(): List<BaseTimer>
}