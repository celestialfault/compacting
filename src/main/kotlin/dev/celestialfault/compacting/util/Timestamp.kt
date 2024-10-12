package dev.celestialfault.compacting.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@JvmInline
value class Timestamp(private val millis: Long) : Comparable<Timestamp> {
	operator fun unaryMinus() = Timestamp(-millis)

	operator fun plus(duration: Duration) = Timestamp(millis + duration.inWholeMilliseconds)
	operator fun plus(milliseconds: Long) = Timestamp(millis + milliseconds)
	operator fun plus(other: Timestamp) = (millis + other.millis).milliseconds

	operator fun minus(duration: Duration) = Timestamp(millis - duration.inWholeMinutes)
	operator fun minus(milliseconds: Long) = Timestamp(millis - milliseconds)
	operator fun minus(other: Timestamp) = (millis - other.millis).milliseconds

	fun elapsedSince() = now() - this

	override fun compareTo(other: Timestamp): Int = millis.compareTo(other.millis)

	companion object {
		fun now() = Timestamp(System.currentTimeMillis())
	}
}
