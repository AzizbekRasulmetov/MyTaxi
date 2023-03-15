package uz.ravshanbaxranov.mytaxi.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.ravshanbaxranov.mytaxi.R
import uz.ravshanbaxranov.mytaxi.presentation.viewmodel.SplashViewModel

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launchWhenCreated {
            viewModel.mapScreenFlow.collect {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.onboardingFlow.collect {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardParentFragment())
            }
        }

    }

}