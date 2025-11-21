package com.helathchickapp.chickhealth

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkResult
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthPushToken
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthRepository
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthSharedPreference
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthSystemService
import com.helathchickapp.chickhealth.sr.domain.usecases.ChickHealthGetAllUseCase
import com.helathchickapp.chickhealth.sr.handlers.ChickHealthPushHandler
import com.helathchickapp.chickhealth.sr.pres.ui.ChickHealthLoadViewModel
import com.helathchickapp.chickhealth.sr.pres.views.ChickHealthViFun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.io.IOException


sealed interface EggLabelAppsFlyerState {
    data object EggLabelDefault : EggLabelAppsFlyerState
    data class EggLabelSuccess(val eggLabelData: MutableMap<String, Any>?) :
        EggLabelAppsFlyerState
    data object EggLabelError : EggLabelAppsFlyerState
}

interface EggLabelAppsApi {
    @Headers("Content-Type: application/json")
    @GET(EGG_LABEL_LIN)
    fun eggLabelGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
private const val EGG_LABEL_APP_DEV = "ERZhD7F2fGsup9Uy9vsfGH"
private const val EGG_LABEL_LIN = "com.helathchickapp.chickhealth"

class ChickHealtApp : Application() {

    companion object {
        var eggLabelInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val eggLabelConversionFlow: MutableStateFlow<EggLabelAppsFlyerState> = MutableStateFlow(
            EggLabelAppsFlyerState.EggLabelDefault
        )
        var EGG_LABEL_FB_LI: String? = null
        const val CHICK_HEALTH_MAIN_TAG = "EggLabelMainTag"
    }
    
    private var eggLabelIsResumed = false
    private var chickHealthConversionTimeoutJob: Job? = null
    private var chickDataDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        eggLabelSetDebufLogger(appsflyer)
        eggLabelMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink { p0 ->
            when (p0.status) {
                DeepLinkResult.Status.FOUND -> {
                    olympPlannerExtractDeepMap(p0.deepLink)
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                }

                DeepLinkResult.Status.NOT_FOUND -> {
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                }

                DeepLinkResult.Status.ERROR -> {
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                }
            }
        }

        appsflyer.init(
            EGG_LABEL_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    chickHealthConversionTimeoutJob?.cancel()
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = eggLabelGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.eggLabelGetClient(
                                    devkey = EGG_LABEL_APP_DEV,
                                    deviceId = eggLabelGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(CHICK_HEALTH_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic") {
                                    eggLabelResume(EggLabelAppsFlyerState.EggLabelError)
                                } else {
                                    eggLabelResume(
                                        EggLabelAppsFlyerState.EggLabelSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(CHICK_HEALTH_MAIN_TAG, "Error: ${d.message}")
                                eggLabelResume(EggLabelAppsFlyerState.EggLabelError)
                            }
                        }
                    } else {
                        eggLabelResume(EggLabelAppsFlyerState.EggLabelSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    chickHealthConversionTimeoutJob?.cancel()
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onConversionDataFail: $p0")
                    eggLabelResume(EggLabelAppsFlyerState.EggLabelError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(CHICK_HEALTH_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, EGG_LABEL_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(CHICK_HEALTH_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(CHICK_HEALTH_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
                eggLabelResume(EggLabelAppsFlyerState.EggLabelError)
            }
        })
        olympPlannerStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickHealtApp)
            modules(
                listOf(
                    eggLabelModule
                )
            )
        }
    }

    private fun olympPlannerExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(CHICK_HEALTH_MAIN_TAG, "Extracted DeepLink data: $map")
        chickDataDeepLinkData = map
    }

    private fun olympPlannerStartConversionTimeout() {
        chickHealthConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!eggLabelIsResumed) {
                Log.d(CHICK_HEALTH_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                eggLabelResume(EggLabelAppsFlyerState.EggLabelError)
            }
        }
    }

    private fun eggLabelResume(state: EggLabelAppsFlyerState) {
        chickHealthConversionTimeoutJob?.cancel()
        if (state is EggLabelAppsFlyerState.EggLabelSuccess) {
            val convData = state.eggLabelData ?: mutableMapOf()
            val deepData = chickDataDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!eggLabelIsResumed) {
                eggLabelIsResumed = true
                eggLabelConversionFlow.value = EggLabelAppsFlyerState.EggLabelSuccess(merged)
            }
        } else {
            if (!eggLabelIsResumed) {
                eggLabelIsResumed = true
                eggLabelConversionFlow.value = state
            }
        }
    }

    private fun eggLabelGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(CHICK_HEALTH_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun eggLabelSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun eggLabelMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun eggLabelGetApi(url: String, client: OkHttpClient?) : EggLabelAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

}

val eggLabelModule = module {
    factory {
        ChickHealthPushHandler()
    }
    single {
        ChickHealthRepository()
    }
    single {
        ChickHealthSharedPreference(get())
    }
    factory {
        ChickHealthPushToken()
    }
    factory {
        ChickHealthSystemService(get())
    }
    factory {
        ChickHealthGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        ChickHealthViFun(get())
    }
    viewModel {
        ChickHealthLoadViewModel(get(), get(), get())
    }
}