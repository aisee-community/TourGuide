package com.ceylonapz.tourguide.tguide

import android.media.MediaDataSource
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.io.use
import kotlin.let
import kotlin.text.isNotBlank
import kotlin.text.orEmpty

object TTSHelper {

    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var okHttpClient: OkHttpClient? = OkHttpClient()
    private var job: Job? = null

    const val API_LINK = "http://dev-api.aisee.ai/text-to-speech"


    @OptIn(DelicateCoroutinesApi::class)
    fun callTtsApi(text: String, needCache: Boolean) {
        StorageHelper.getAudio(text)?.let {
            play(it)
            Log.d("zsc", "Use cache")
            return
        }
        val body = """{"text": "$text"}""".toRequestBody(
            "application/json; charset=utf-8".toMediaType()
        )
        val request = Request.Builder()
            .url(API_LINK)
            .post(body)
            .build()
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                okHttpClient?.newCall(request)?.execute()?.use { response ->
                    if (response.isSuccessful) {
                        val result = response.body?.string()
                        val jObject = JSONObject(result.orEmpty())
                        val voiceResponse = jObject.getString("voice_response")
                        Log.d("zsc", "Success: $voiceResponse")
                        if (voiceResponse.isNotBlank()) {
                            if (needCache) {
                                StorageHelper.putAudio(text, voiceResponse)
                            }
                            play(voiceResponse)
                        }
                    } else {
                        Log.d("zsc", "Error: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.d("zsc", "${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun destroy() {
        mediaPlayer?.release()
        job?.cancel()
        mediaPlayer = null
        okHttpClient = null
        job = null
    }

    private fun play(base64String: String) {
        val audioBytes = Base64.decode(base64String, Base64.DEFAULT)
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.let {
            it.setDataSource(object : MediaDataSource() {
                override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
                    if (position >= audioBytes.size) return -1
                    val remaining = audioBytes.size - position.toInt()
                    val bytesToRead = if (size > remaining) remaining else size
                    System.arraycopy(audioBytes, position.toInt(), buffer, offset, bytesToRead)
                    return bytesToRead
                }

                override fun getSize(): Long = audioBytes.size.toLong()
                override fun close() {}
            })
            it.prepare()
            it.start()
        }
    }

}