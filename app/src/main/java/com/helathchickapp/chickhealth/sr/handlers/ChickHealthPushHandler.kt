package com.helathchickapp.chickhealth.sr.handlers

import android.os.Bundle
import android.util.Log
import com.helathchickapp.chickhealth.ChickHealtApp

class ChickHealthPushHandler {

    fun chickHealthHandlePush(extras: Bundle?) {
        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = chickHealthBundleToMap(extras)
            Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    ChickHealtApp.EGG_LABEL_FB_LI = map["url"]
                    Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Push data no!")
        }
    }

    private fun chickHealthBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}