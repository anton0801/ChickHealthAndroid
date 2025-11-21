package com.helathchickapp.chickhealth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.helathchickapp.chickhealth.databinding.ActivityChickHealthBinding
import com.helathchickapp.chickhealth.sr.handlers.ChickHealthPushHandler
import com.helathchickapp.chickhealth.sr.layout.ChickHealthGlobalLayoutUtil
import com.helathchickapp.chickhealth.sr.layout.chickHealthSetupSystemBars
import org.koin.android.ext.android.inject

class ChickHealthActivity : AppCompatActivity() {

    private val eggLabelPushHandler by inject<ChickHealthPushHandler>()

    private lateinit var binding: ActivityChickHealthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chickHealthSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityChickHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eggLabelRootView = findViewById<View>(android.R.id.content)
        ChickHealthGlobalLayoutUtil().chickHealthAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(eggLabelRootView) { eggLabelView, eggLabelInsets ->
            val eggLabelSystemBars = eggLabelInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val eggLabelDisplayCutout = eggLabelInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val eggLabelIme = eggLabelInsets.getInsets(WindowInsetsCompat.Type.ime())


            val eggLabelTopPadding = maxOf(eggLabelSystemBars.top, eggLabelDisplayCutout.top)
            val eggLabelLeftPadding = maxOf(eggLabelSystemBars.left, eggLabelDisplayCutout.left)
            val eggLabelRightPadding = maxOf(eggLabelSystemBars.right, eggLabelDisplayCutout.right)
            window.setSoftInputMode(ChickHealtApp.eggLabelInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "ADJUST PUN")
                val eggLabelBottomInset = maxOf(eggLabelSystemBars.bottom, eggLabelDisplayCutout.bottom)

                eggLabelView.setPadding(eggLabelLeftPadding, eggLabelTopPadding, eggLabelRightPadding, 0)

                eggLabelView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = eggLabelBottomInset
                }
            } else {
                Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "ADJUST RESIZE")

                val eggLabelBottomInset = maxOf(eggLabelSystemBars.bottom, eggLabelDisplayCutout.bottom, eggLabelIme.bottom)

                eggLabelView.setPadding(eggLabelLeftPadding, eggLabelTopPadding, eggLabelRightPadding, 0)

                eggLabelView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = eggLabelBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Activity onCreate()")
        eggLabelPushHandler.chickHealthHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            chickHealthSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        chickHealthSetupSystemBars()
    }

}