package ru.gressor.gametimer.repository

import ru.gressor.gametimer.entities.BaseTimer

class TimersRepositoryList: TimersRepository {
    private val list = mutableListOf<BaseTimer>()

    override fun storeTimer(timer: BaseTimer) {
        val index = list.indexOf(timer)

        if (index > 0) {
            list[index] = timer
        } else {
            list.add(timer)
        }
    }

    override fun getAllTimers(): List<BaseTimer> = list
}