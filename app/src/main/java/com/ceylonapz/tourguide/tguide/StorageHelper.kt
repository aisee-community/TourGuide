package com.ceylonapz.tourguide.tguide

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object StorageHelper {

    const val SHARED_PREF_NAME = "tour_guild_pref"
    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getAudio(key: String): String? {
        return sharedPreferences?.getString(key, null)
    }

    fun putAudio(key: String, audioString: String) {
        sharedPreferences?.edit(commit = true) {
            putString(key, audioString)
        }
    }

}