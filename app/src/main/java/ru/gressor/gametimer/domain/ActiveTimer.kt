package ru.gressor.gametimer.domain

import java.util.*

data class ActiveTimer(
    val id: UUID,
    var name: String = "",
    val ticker: Ticker
)