package com.example.voice_activation_uart

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class VoiceActivation private constructor(
    private val devicePath: String,
    private val baud: Int
) {
    sealed class Event {
        data object Started : Event()
        data object Stopped : Event()
        data class Command(val cmdId: Int) : Event()
        data class Error(val message: String) : Event()
    }

    fun interface EventListener {
        fun onEvent(event: Event)
    }

    private val listeners = CopyOnWriteArrayList<EventListener>()
    private val running = AtomicBoolean(false)

    private val callback = object : UartNative.Callback {
        override fun onCmd(cmdId: Int) {
            dispatch(Event.Command(cmdId))
        }

        override fun onError(msg: String) {
            dispatch(Event.Error(msg))
        }
    }

    fun events(listener: EventListener) {
        listeners.add(listener)
    }

    fun remove(listener: EventListener) {
        listeners.remove(listener)
    }

    fun onWake(cmdId: Int = 2, block: () -> Unit) {
        events { e ->
            if (e is Event.Command && e.cmdId == cmdId) block()
        }
    }

    fun start() {
        if (!running.compareAndSet(false, true)) return
        dispatch(Event.Started)
        UartNative.start(devicePath, baud, callback)
    }

    fun stop() {
        if (!running.compareAndSet(true, false)) return
        UartNative.stop()
        dispatch(Event.Stopped)
    }

    private fun dispatch(e: Event) {
        for (l in listeners) l.onEvent(e)
    }

    companion object {
        fun create(devicePath: String, baud: Int): VoiceActivation {
            return VoiceActivation(devicePath, baud)
        }
    }
}