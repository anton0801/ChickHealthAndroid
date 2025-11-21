package com.helathchickapp.chickhealth.sr.domain.model

import com.google.gson.annotations.SerializedName

private const val EGG_LABEL_A = "com.helathchickapp.chickhealth"
private const val EGG_LABEL_B = ""

data class ChickHealthEntity (
    @SerializedName("ok")
    val eggLabelOk: String,
    @SerializedName("url")
    val eggLabelUrl: String,
    @SerializedName("expires")
    val eggLabelExpires: Long,
)

data class ChickHealthParam (
    @SerializedName("af_id")
    val chickHealthAfId: String,
    @SerializedName("bundle_id")
    val chickHealthBundleId: String = EGG_LABEL_A,
    @SerializedName("os")
    val chickHealthOs: String = "Android",
    @SerializedName("store_id")
    val chickHealthStoreId: String = EGG_LABEL_A,
    @SerializedName("locale")
    val chickHealthLocale: String,
    @SerializedName("push_token")
    val chickHealthPushToken: String,
    @SerializedName("firebase_project_id")
    val chickHealthFirebaseProjectId: String = EGG_LABEL_B,
)