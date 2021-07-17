package ru.gressor.gametimer.entities

data class BaseTimer(
    val id: Long = 0,
    var name: String = "",
    var seconds: Int = 0,
    var running: Boolean = false
) {
    override fun equals(other: Any?) =
        if (other == null || other !is BaseTimer) {
            false
        } else id == other.id

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        val empty = BaseTimer(-1, "", 0, false)
    }
}