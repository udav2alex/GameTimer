package ru.gressor.gametimer.repository

import ru.gressor.gametimer.entities.BaseTimer
import ru.gressor.gametimer.interactor.ITimersRepository

class TimersRepositoryList: ITimersRepository {
    private val list = mutableListOf<BaseTimer>()

    override fun storeTimer(timer: BaseTimer) {
        list.indexOf(timer).let {
            if (it > 0) {
                list[it] = timer
            } else {
                list.add(timer)
            }
        }
    }

    override fun deleteTimer(timer: BaseTimer) {
        list.indexOf(timer).let {
            if (it >= 0) {
                list.removeAt(it)
            }
        }
    }

    override fun getAllTimers(): List<BaseTimer> = list
}