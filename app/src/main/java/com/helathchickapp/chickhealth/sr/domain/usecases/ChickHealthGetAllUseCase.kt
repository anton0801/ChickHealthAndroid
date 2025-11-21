package com.helathchickapp.chickhealth.sr.domain.usecases

import android.util.Log
import com.helathchickapp.chickhealth.ChickHealtApp
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthPushToken
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthRepository
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthSystemService
import com.helathchickapp.chickhealth.sr.domain.model.ChickHealthEntity
import com.helathchickapp.chickhealth.sr.domain.model.ChickHealthParam

class ChickHealthGetAllUseCase(
    private val chickHealthRepository: ChickHealthRepository,
    private val chickHealthSystemService: ChickHealthSystemService,
    private val chickHealthPushToken: ChickHealthPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?): ChickHealthEntity? {
        val params = ChickHealthParam(
            chickHealthLocale = chickHealthSystemService.eggLabelGetLocale(),
            chickHealthPushToken = chickHealthPushToken.eggLabelGetToken(),
            chickHealthAfId = chickHealthSystemService.eggLabelGetAppsflyerId()
        )
        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Params for request: $params")
        return chickHealthRepository.chickHealthLabelGetClient(params, conversion)
    }


}