package com.helathchickapp.chickhealth.sr.pres.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.helathchickapp.chickhealth.MainActivity
import com.helathchickapp.chickhealth.R
import com.helathchickapp.chickhealth.databinding.FragmentLoadEggLabelBinding
import com.helathchickapp.chickhealth.sr.domain.data.ChickHealthSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException


class ChickHealthLoadFragment : Fragment(R.layout.fragment_load_egg_label) {
    private lateinit var chickHealthLoadBinding: FragmentLoadEggLabelBinding

    private val chickHealthLoadViewModel by viewModel<ChickHealthLoadViewModel>()

    private val chickHealthSharedPreference by inject<ChickHealthSharedPreference>()

    private var eggLabelUrl = ""

    private val chickHealthRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            eggLabelNavigateToSuccess(eggLabelUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                chickHealthSharedPreference.eggLabelNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                eggLabelNavigateToSuccess(eggLabelUrl)
            } else {
                eggLabelNavigateToSuccess(eggLabelUrl)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 999 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            eggLabelNavigateToSuccess(eggLabelUrl)
        } else {
            // твой код на отказ
            eggLabelNavigateToSuccess(eggLabelUrl)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chickHealthLoadBinding = FragmentLoadEggLabelBinding.bind(view)

        chickHealthLoadBinding.eggLabelGrandButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val eggLabelPermission = Manifest.permission.POST_NOTIFICATIONS
                chickHealthRequestNotificationPermission.launch(eggLabelPermission)
                chickHealthSharedPreference.eggLabelNotificationRequestedBefore = true
            } else {
                eggLabelNavigateToSuccess(eggLabelUrl)
                chickHealthSharedPreference.eggLabelNotificationRequestedBefore = true
            }
        }

        chickHealthLoadBinding.eggLabelSkipButton.setOnClickListener {
            chickHealthSharedPreference.eggLabelNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            eggLabelNavigateToSuccess(eggLabelUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chickHealthLoadViewModel.chickHealthHomeScreenState.collect {
                    when (it) {
                        is ChickHealthLoadViewModel.EggLabelHomeScreenState.EggLabelLoading -> {

                        }

                        is ChickHealthLoadViewModel.EggLabelHomeScreenState.EggLabelError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is ChickHealthLoadViewModel.EggLabelHomeScreenState.EggLabelSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val eggLabelPermission = Manifest.permission.POST_NOTIFICATIONS
                                val eggLabelPermissionRequestedBefore =
                                    chickHealthSharedPreference.eggLabelNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(
                                        requireContext(),
                                        eggLabelPermission
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    eggLabelNavigateToSuccess(it.data)
                                } else if (!eggLabelPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > chickHealthSharedPreference.eggLabelNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    chickHealthLoadBinding.eggLabelNotiGroup.visibility = View.VISIBLE
                                    chickHealthLoadBinding.eggLabelLoadingGroup.visibility = View.GONE
                                    eggLabelUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(eggLabelPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > chickHealthSharedPreference.eggLabelNotificationRequest) {
                                        chickHealthLoadBinding.eggLabelNotiGroup.visibility =
                                            View.VISIBLE
                                        chickHealthLoadBinding.eggLabelLoadingGroup.visibility =
                                            View.GONE
                                        eggLabelUrl = it.data
                                    } else {
                                        eggLabelNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    eggLabelNavigateToSuccess(it.data)
                                }
                            } else {
                                eggLabelNavigateToSuccess(it.data)
                            }
                        }

                        ChickHealthLoadViewModel.EggLabelHomeScreenState.EggLabelNotInternet -> {
                            chickHealthLoadBinding.eggLabelStateGroup.visibility = View.VISIBLE
                            chickHealthLoadBinding.eggLabelLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun eggLabelNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_eggLabelLoadFragment_to_eggLabelV,
            bundleOf(EGG_LABEL_D to data)
        )
    }

    companion object {
        const val EGG_LABEL_D = "eggLabelData"
    }
}