package com.helathchickapp.chickhealth.sr.pres.views

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class ChickHealthDataStore : ViewModel(){
    val chickHealthViList: MutableList<ChickHealthVi> = mutableListOf()
    var eggLabelIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var eggLabelContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var chickHealthView: ChickHealthVi

}