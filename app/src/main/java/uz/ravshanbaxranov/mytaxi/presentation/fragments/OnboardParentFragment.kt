package uz.ravshanbaxranov.mytaxi.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.ravshanbaxranov.mytaxi.R
import uz.ravshanbaxranov.mytaxi.databinding.FragmentOnboardingParentBinding
import uz.ravshanbaxranov.mytaxi.presentation.adapter.OnboardingPagerAdapter
import uz.ravshanbaxranov.mytaxi.presentation.viewmodel.OnboardViewModel

@AndroidEntryPoint
class OnboardParentFragment : Fragment(R.layout.fragment_onboarding_parent) {

    private val binding by viewBinding(FragmentOnboardingParentBinding::bind)
    private lateinit var pagerAdapter: OnboardingPagerAdapter
    private lateinit var viewPager: ViewPager2
    private val viewModel: OnboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerAdapter = OnboardingPagerAdapter(this)
        viewPager = binding.viewPager
        viewPager.adapter = pagerAdapter
        binding.dotsIndicator.attachTo(viewPager)

        lifecycleScope.launchWhenCreated {
            viewModel.startFlow.collect {
                findNavController().navigate(OnboardParentFragmentDirections.actionOnboardParentFragmentToMainFragment())
            }
        }


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.skipTv.isVisible = position != 2
                binding.nextTv.isVisible = position != 2
                binding.startBtn.isVisible = position == 2
            }

        })

        binding.startBtn.setOnClickListener {
            viewModel.start()
        }

        binding.nextTv.setOnClickListener {
            viewPager.currentItem++
        }
        binding.skipTv.setOnClickListener {
            viewPager.currentItem = 2
        }

    }

}