package com.ceylonapz.tourguide.tguide

import android.media.MediaDataSource
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object TTSHelper {

    private const val TAG = "AiSeeTG"

    private var mediaPlayer: MediaPlayer? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

    const val API_LINK = "http://dev-api.aisee.ai/text-to-speech"

    fun callTtsApi(text: String, needCache: Boolean) {

        // Cache first
        StorageHelper.getAudio(text)?.let {
            Log.d(TAG, "Using cached audio")
            play(it)
            return
        }

        val (lang, voice) = detectLanguage(text)

        val body = JSONObject()
            .put("text", text)
            .put("language", lang)
            .put("voice", voice)
            .put("audio_format", "mp3")
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(API_LINK)
            .post(body)
            .build()

        scope.launch {
            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "HTTP ${response.code}")
                        return@use
                    }

                    val result = response.body?.string()
                    if (result.isNullOrEmpty()) {
                        Log.e(TAG, "Empty response body")
                        return@use
                    }

                    val voiceResponse =
                        JSONObject(result).optString("voice_response")

                    if (voiceResponse.isNotBlank()) {
                        if (needCache) {
                            StorageHelper.putAudio(text, voiceResponse)
                        }
                        play(voiceResponse)
                    } else {
                        Log.e(TAG, "voice_response empty")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "TTS error", e)
            }
        }
    }

    private fun play(base64String: String) {
        val audioBytes = Base64.decode(base64String, Base64.DEFAULT)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(object : MediaDataSource() {
                override fun readAt(
                    position: Long,
                    buffer: ByteArray,
                    offset: Int,
                    size: Int
                ): Int {
                    if (position >= audioBytes.size) return -1
                    val remaining = audioBytes.size - position.toInt()
                    val bytesToRead = minOf(size, remaining)
                    System.arraycopy(audioBytes, position.toInt(), buffer, offset, bytesToRead)
                    return bytesToRead
                }

                override fun getSize(): Long = audioBytes.size.toLong()
                override fun close() {}
            })
            prepare()
            start()
        }
    }

    fun detectLanguage(text: String): Pair<String, String> =
        when {
            text.any { it in '\u4E00'..'\u9FFF' } ->
                "zh-CN" to "zh-CN-XiaoxiaoNeural"
            text.any { it in '\u0D80'..'\u0DFF' } ->
                "si-LK" to "si-LK-SameeraNeural"
            text.any { it in "äöüß" } ->
                "de-DE" to "de-DE-Standard-A"
            else ->
                "en-US" to "en-US-Standard-A"
        }

    fun destroy() {
        scope.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}