package com.helathchickapp.chickhealth.sr.domain.data

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.helathchickapp.chickhealth.ChickHealtApp
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChickHealthPushToken {

    suspend fun eggLabelGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}