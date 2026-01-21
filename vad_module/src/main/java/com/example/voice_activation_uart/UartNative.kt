package com.example.voice_activation_uart

internal object UartNative {

    init {
        System.loadLibrary("uartreader")
    }

    internal interface Callback {
        fun onCmd(cmdId: Int)
        fun onError(msg: String)
    }

    fun start(devicePath: String, baud: Int, callback: Callback) {
        nativeSetCallback(callback)
        nativeStart(devicePath, baud)
    }

    fun stop() {
        nativeStop()
        nativeSetCallback(null)
    }

    private external fun nativeStart(devicePath: String, baud: Int)
    private external fun nativeStop()
    private external fun nativeSetCallback(callback: Callback?)
}