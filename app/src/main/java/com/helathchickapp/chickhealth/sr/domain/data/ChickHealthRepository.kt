package com.helathchickapp.chickhealth.sr.domain.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.helathchickapp.chickhealth.ChickHealtApp.Companion.CHICK_HEALTH_MAIN_TAG
import com.helathchickapp.chickhealth.sr.domain.model.ChickHealthEntity
import com.helathchickapp.chickhealth.sr.domain.model.ChickHealthParam
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChickHealthLabelApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun eggLabelGetClient(
        @Body jsonString: JsonObject,
    ): Call<ChickHealthEntity>
}


private const val CHICK_HEALTH_MAIN = "https://chickheallth.com/"

class ChickHealthRepository {

    suspend fun chickHealthLabelGetClient(
        chickHealthParam: ChickHealthParam,
        eggLabelConversion: MutableMap<String, Any>?
    ): ChickHealthEntity? {
        val gson = Gson()
        val api = chickHealthLabelGetApi(CHICK_HEALTH_MAIN, null)

        val eggLabelJsonObject = gson.toJsonTree(chickHealthParam).asJsonObject
        eggLabelConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            eggLabelJsonObject.add(key, element)
        }
        return try {
            val eggLabelRequest: Call<ChickHealthEntity> = api.eggLabelGetClient(
                jsonString = eggLabelJsonObject,
            )
            val eggLabelResult = eggLabelRequest.awaitResponse()
            Log.d(CHICK_HEALTH_MAIN_TAG, "Retrofit: Result code: ${eggLabelResult.code()}")
            if (eggLabelResult.code() == 200) {
                Log.d(CHICK_HEALTH_MAIN_TAG, "Retrofit: Get request success")
                Log.d(CHICK_HEALTH_MAIN_TAG, "Retrofit: Code = ${eggLabelResult.code()}")
                Log.d(CHICK_HEALTH_MAIN_TAG, "Retrofit: ${eggLabelResult.body()}")
                eggLabelResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(CHICK_HEALTH_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(CHICK_HEALTH_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun chickHealthLabelGetApi(url: String, client: OkHttpClient?): ChickHealthLabelApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
