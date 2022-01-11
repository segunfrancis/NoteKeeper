package com.segunfrancis.notekeeper.ui.auth

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.FragmentOnboardingBinding
import com.segunfrancis.notekeeper.util.Destination
import com.segunfrancis.notekeeper.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    private val viewModel by viewModels<OnBoardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupClickListeners()
        setupObservers()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.forEach { it.isVisible = false }
    }

    private fun setupObservers() {
        viewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                is OnBoardingAction.Navigate -> findNavController().navigate(it.destination)
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        loginButton.setOnClickListener { viewModel.goTo(Destination.LOGIN) }
        registerButton.setOnClickListener { viewModel.goTo(Destination.REGISTER) }
    }
}
