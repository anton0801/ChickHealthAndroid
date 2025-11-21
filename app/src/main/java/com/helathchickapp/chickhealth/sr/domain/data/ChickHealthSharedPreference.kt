package com.helathchickapp.chickhealth.sr.domain.data

import android.content.Context
import androidx.core.content.edit

class ChickHealthSharedPreference(context: Context) {
    private val eggLabelPrefs =
        context.getSharedPreferences("eggLabelSharedPrefsAb", Context.MODE_PRIVATE)

    var eggLabelSavedUrl: String
        get() = eggLabelPrefs.getString(EGG_LABEL_SAVED_URL, "") ?: ""
        set(value) = eggLabelPrefs.edit { putString(EGG_LABEL_SAVED_URL, value) }

    var eggLabelExpired: Long
        get() = eggLabelPrefs.getLong(EGG_LABEL_EXPIRED, 0L)
        set(value) = eggLabelPrefs.edit { putLong(EGG_LABEL_EXPIRED, value) }

    var eggLabelAppState: Int
        get() = eggLabelPrefs.getInt(EGG_LABEL_APPLICATION_STATE, 0)
        set(value) = eggLabelPrefs.edit { putInt(EGG_LABEL_APPLICATION_STATE, value) }

    var eggLabelNotificationRequest: Long
        get() = eggLabelPrefs.getLong(EGG_LABEL_NOTIFICAITON_REQUEST, 0L)
        set(value) = eggLabelPrefs.edit { putLong(EGG_LABEL_NOTIFICAITON_REQUEST, value) }

    var eggLabelNotificationRequestedBefore: Boolean
        get() = eggLabelPrefs.getBoolean(EGG_LABEL_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = eggLabelPrefs.edit {
            putBoolean(
                EGG_LABEL_NOTIFICATION_REQUEST_BEFORE, value
            )
        }

    companion object {
        private const val EGG_LABEL_SAVED_URL = "eggLabelSavedUrl"
        private const val EGG_LABEL_EXPIRED = "eggLabelExpired"
        private const val EGG_LABEL_APPLICATION_STATE = "eggLabelApplicationState"
        private const val EGG_LABEL_NOTIFICAITON_REQUEST = "eggLabelNotificationRequest"
        private const val EGG_LABEL_NOTIFICATION_REQUEST_BEFORE =
            "eggLabelNotificationRequestedBefore"
    }
}