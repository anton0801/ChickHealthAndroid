package com.helathchickapp.chickhealth.sr.pres.views

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.helathchickapp.chickhealth.ChickHealtApp
import com.helathchickapp.chickhealth.sr.pres.ui.ChickHealthLoadFragment
import org.koin.android.ext.android.inject

class ChickHealthV : Fragment() {

    private lateinit var eggLabelPhoto: Uri
    private var eggLabelFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val eggLabelTakeFile: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            eggLabelFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
            eggLabelFilePathFromChrome = null
        }

    private val eggLabelTakePhoto: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                eggLabelFilePathFromChrome?.onReceiveValue(arrayOf(eggLabelPhoto))
                eggLabelFilePathFromChrome = null
            } else {
                eggLabelFilePathFromChrome?.onReceiveValue(null)
                eggLabelFilePathFromChrome = null
            }
        }

    private val chickHealthDataStore by activityViewModels<ChickHealthDataStore>()


    private val chickHealthViFun by inject<ChickHealthViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (chickHealthDataStore.chickHealthView.canGoBack()) {
                        chickHealthDataStore.chickHealthView.goBack()
                        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "WebView can go back")
                    } else if (chickHealthDataStore.chickHealthViList.size > 1) {
                        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "WebView can`t go back")
                        chickHealthDataStore.chickHealthViList.removeAt(chickHealthDataStore.chickHealthViList.lastIndex)
                        Log.d(
                            ChickHealtApp.CHICK_HEALTH_MAIN_TAG,
                            "WebView list size ${chickHealthDataStore.chickHealthViList.size}"
                        )
                        chickHealthDataStore.chickHealthView.destroy()
                        val previousWebView = chickHealthDataStore.chickHealthViList.last()
                        eggLabelAttachWebViewToContainer(previousWebView)
                        chickHealthDataStore.chickHealthView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (chickHealthDataStore.eggLabelIsFirstCreate) {
            chickHealthDataStore.eggLabelIsFirstCreate = false
            chickHealthDataStore.eggLabelContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return chickHealthDataStore.eggLabelContainerView
        } else {
            return chickHealthDataStore.eggLabelContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "onViewCreated")
        if (chickHealthDataStore.chickHealthViList.isEmpty()) {
            chickHealthDataStore.chickHealthView = ChickHealthVi(requireContext(), object :
                ChickHealthCallBack {
                override fun chickHealthHandleCreateWebWindowRequest(chickHealthVi: ChickHealthVi) {
                    chickHealthDataStore.chickHealthViList.add(chickHealthVi)
                    Log.d(
                        ChickHealtApp.CHICK_HEALTH_MAIN_TAG,
                        "WebView list size = ${chickHealthDataStore.chickHealthViList.size}"
                    )
                    Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "CreateWebWindowRequest")
                    chickHealthDataStore.chickHealthView = chickHealthVi
                    chickHealthVi.eggLabelSetFileChooserHandler { callback ->
                        eggLabelHandleFileChooser(callback)
                    }
                    eggLabelAttachWebViewToContainer(chickHealthVi)
                }

            }, eggLabelWindow = requireActivity().window).apply {
                eggLabelSetFileChooserHandler { callback ->
                    eggLabelHandleFileChooser(callback)
                }
            }
            chickHealthDataStore.chickHealthView.eggLabelFLoad(
                arguments?.getString(ChickHealthLoadFragment.EGG_LABEL_D) ?: ""
            )
//            ejvview.fLoad("www.google.com")
            chickHealthDataStore.chickHealthViList.add(chickHealthDataStore.chickHealthView)
            eggLabelAttachWebViewToContainer(chickHealthDataStore.chickHealthView)
        } else {
            chickHealthDataStore.chickHealthViList.forEach { webView ->
                webView.eggLabelSetFileChooserHandler { callback ->
                    eggLabelHandleFileChooser(callback)
                }
            }
            chickHealthDataStore.chickHealthView = chickHealthDataStore.chickHealthViList.last()

            eggLabelAttachWebViewToContainer(chickHealthDataStore.chickHealthView)
        }
        Log.d(
            ChickHealtApp.CHICK_HEALTH_MAIN_TAG,
            "WebView list size = ${chickHealthDataStore.chickHealthViList.size}"
        )
    }

    private fun eggLabelHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(
            ChickHealtApp.CHICK_HEALTH_MAIN_TAG,
            "handleFileChooser called, callback: ${callback != null}"
        )

        eggLabelFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Launching file picker")
                    eggLabelTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                1 -> {
                    Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "Launching camera")
                    eggLabelPhoto = chickHealthViFun.eggLabelSavePhoto()
                    eggLabelTakePhoto.launch(eggLabelPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(ChickHealtApp.CHICK_HEALTH_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                eggLabelFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun eggLabelAttachWebViewToContainer(w: ChickHealthVi) {
        chickHealthDataStore.eggLabelContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            chickHealthDataStore.eggLabelContainerView.removeAllViews()
            chickHealthDataStore.eggLabelContainerView.addView(w)
        }
    }


}