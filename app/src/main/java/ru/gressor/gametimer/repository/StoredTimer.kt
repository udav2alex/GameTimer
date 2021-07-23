package ru.gressor.gametimer.repository

import java.util.*

data class StoredTimer(
    val id: UUID,
    var name: String = "",
    var time: Long = 0,
    val initialTime: Long = 0,
    var running: Boolean = false
) {
    override fun equals(other: Any?) =
        if (other == null || other !is StoredTimer) {
            false
        } else id == other.id

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        val empty = StoredTimer(UUID.randomUUID())
    }
}