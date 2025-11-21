package com.helathchickapp.chickhealth.sr.pres.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helathchickapp.chickhealth.ChickHealtApp
import com.helathchickapp.chickhealth.EggLabelAppsFlyerState
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthSharedPreference
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthSystemService
import com.helathchickapp.chickhealth.sr.domain.usecases.ChickHealthGetAllUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChickHealthLoadViewModel(
    private val chickHealthGetAllUseCase: ChickHealthGetAllUseCase,
    private val chickHealthSharedPreference: ChickHealthSharedPreference,
    private val chickHealthSystemService: ChickHealthSystemService
) : ViewModel() {

    private val _chickHealthHomeScreenState: MutableStateFlow<EggLabelHomeScreenState> =
        MutableStateFlow(EggLabelHomeScreenState.EggLabelLoading)
    val chickHealthHomeScreenState = _chickHealthHomeScreenState.asStateFlow()

    private var eggLabelGetApps = false

    init {
        viewModelScope.launch {
            when (chickHealthSharedPreference.eggLabelAppState) {
                0 -> {
                    if (chickHealthSystemService.eggLabelIsOnline()) {
                        ChickHealtApp.eggLabelConversionFlow.collect {
                            when (it) {
                                EggLabelAppsFlyerState.EggLabelDefault -> {}
                                EggLabelAppsFlyerState.EggLabelError -> {
                                    chickHealthSharedPreference.eggLabelAppState = 2
                                    _chickHealthHomeScreenState.value =
                                        EggLabelHomeScreenState.EggLabelError
                                    eggLabelGetApps = true
                                }

                                is EggLabelAppsFlyerState.EggLabelSuccess -> {
                                    if (!eggLabelGetApps) {
                                        eggLabelGetData(it.eggLabelData)
                                        eggLabelGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickHealthHomeScreenState.value =
                            EggLabelHomeScreenState.EggLabelNotInternet
                    }
                }

                1 -> {
                    if (chickHealthSystemService.eggLabelIsOnline()) {
                        if (ChickHealtApp.EGG_LABEL_FB_LI != null) {
                            _chickHealthHomeScreenState.value =
                                EggLabelHomeScreenState.EggLabelSuccess(
                                    ChickHealtApp.EGG_LABEL_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > chickHealthSharedPreference.eggLabelExpired) {
                            Log.d(
                                ChickHealtApp.CHICK_HEALTH_MAIN_TAG,
                                "Current time more then expired, repeat request"
                            )
                            ChickHealtApp.eggLabelConversionFlow.collect {
                                when (it) {
                                    EggLabelAppsFlyerState.EggLabelDefault -> {}
                                    EggLabelAppsFlyerState.EggLabelError -> {
                                        _chickHealthHomeScreenState.value =
                                            EggLabelHomeScreenState.EggLabelSuccess(
                                                chickHealthSharedPreference.eggLabelSavedUrl
                                            )
                                        eggLabelGetApps = true
                                    }

                                    is EggLabelAppsFlyerState.EggLabelSuccess -> {
                                        if (!eggLabelGetApps) {
                                            eggLabelGetData(it.eggLabelData)
                                            eggLabelGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(
                                ChickHealtApp.CHICK_HEALTH_MAIN_TAG,
                                "Current time less then expired, use saved url"
                            )
                            _chickHealthHomeScreenState.value =
                                EggLabelHomeScreenState.EggLabelSuccess(
                                    chickHealthSharedPreference.eggLabelSavedUrl
                                )
                        }
                    } else {
                        _chickHealthHomeScreenState.value =
                            EggLabelHomeScreenState.EggLabelNotInternet
                    }
                }

                2 -> {
                    _chickHealthHomeScreenState.value =
                        EggLabelHomeScreenState.EggLabelError
                }
            }
        }
    }


    private suspend fun eggLabelGetData(conversation: MutableMap<String, Any>?) {
        val eggLabelData = chickHealthGetAllUseCase.invoke(conversation)
        if (chickHealthSharedPreference.eggLabelAppState == 0) {
            if (eggLabelData == null) {
                chickHealthSharedPreference.eggLabelAppState = 2
                _chickHealthHomeScreenState.value =
                    EggLabelHomeScreenState.EggLabelError
            } else {
                chickHealthSharedPreference.eggLabelAppState = 1
                chickHealthSharedPreference.apply {
                    eggLabelExpired = eggLabelData.eggLabelExpires
                    eggLabelSavedUrl = eggLabelData.eggLabelUrl
                }
                _chickHealthHomeScreenState.value =
                    EggLabelHomeScreenState.EggLabelSuccess(eggLabelData.eggLabelUrl)
            }
        } else {
            if (eggLabelData == null) {
                _chickHealthHomeScreenState.value =
                    EggLabelHomeScreenState.EggLabelSuccess(chickHealthSharedPreference.eggLabelSavedUrl)
            } else {
                chickHealthSharedPreference.apply {
                    eggLabelExpired = eggLabelData.eggLabelExpires
                    eggLabelSavedUrl = eggLabelData.eggLabelUrl
                }
                _chickHealthHomeScreenState.value =
                    EggLabelHomeScreenState.EggLabelSuccess(eggLabelData.eggLabelUrl)
            }
        }
    }


    sealed class EggLabelHomeScreenState {
        data object EggLabelLoading : EggLabelHomeScreenState()
        data object EggLabelError : EggLabelHomeScreenState()
        data class EggLabelSuccess(val data: String) : EggLabelHomeScreenState()
        data object EggLabelNotInternet : EggLabelHomeScreenState()
    }
}