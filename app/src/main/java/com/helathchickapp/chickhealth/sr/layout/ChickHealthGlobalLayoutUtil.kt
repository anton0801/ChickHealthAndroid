package com.helathchickapp.chickhealth.sr.layout

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.helathchickapp.chickhealth.ChickHealtApp

class ChickHealthGlobalLayoutUtil {

    private var eggLabelMChildOfContent: View? = null
    private var eggLabelUsableHeightPrevious = 0


    private fun chickHealthComputeUsableHeight(): Int {
        val r = Rect()
        eggLabelMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val eggLabelUsableHeightNow = chickHealthComputeUsableHeight()
        if (eggLabelUsableHeightNow != eggLabelUsableHeightPrevious) {
            val eggLabelUsableHeightSansKeyboard = eggLabelMChildOfContent?.rootView?.height ?: 0
            val eggLabelHeightDifference = eggLabelUsableHeightSansKeyboard - eggLabelUsableHeightNow

            if (eggLabelHeightDifference > (eggLabelUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(ChickHealtApp.eggLabelInputMode)
            } else {
                activity.window.setSoftInputMode(ChickHealtApp.eggLabelInputMode)
            }
//            mChildOfContent?.requestLayout()
            eggLabelUsableHeightPrevious = eggLabelUsableHeightNow
        }
    }
    fun chickHealthAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        eggLabelMChildOfContent = content.getChildAt(0)

        eggLabelMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }
}