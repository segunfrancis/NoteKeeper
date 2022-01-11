package com.segunfrancis.notekeeper.ui.auth

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.FragmentLoginBinding
import com.segunfrancis.notekeeper.util.Destination
import com.segunfrancis.notekeeper.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val args by navArgs<LoginFragmentArgs>()
    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setupUi(args.destination)
        setupObservers()
        setupClickListeners()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.forEach { it.isVisible = false }
    }

    private fun setupUi(destination: String) = with(binding) {
        when (Destination.valueOf(destination)) {
            Destination.LOGIN -> {
                authButton.setText(R.string.login)
            }
            Destination.REGISTER -> {
                authButton.setText(R.string.register)
            }
            else -> {}
        }
    }

    private fun setupObservers() = with(binding) {
        emailField.addTextChangedListener {
            it?.let { viewModel.setEmail(it.toString()) }
        }
        passwordField.addTextChangedListener {
            it?.let { viewModel.setPassword(it.toString()) }
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                LoginStates.LoadingState -> handleLoading()
                is LoginStates.ButtonState -> authButton.isEnabled = it.enable
                is LoginStates.ErrorState -> handleError(it.errorMessage)
            }
        }
        viewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                is LoginAction.Navigate -> findNavController().navigate(it.destination)
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        authButton.setOnClickListener {
            when (Destination.valueOf(args.destination)) {
                Destination.LOGIN -> viewModel.loginUser(
                    email = emailField.text.toString().trim(),
                    password = passwordField.text.toString()
                )
                Destination.REGISTER -> viewModel.createUser(
                    email = emailField.text.toString().trim(),
                    password = passwordField.text.toString()
                )
                else -> {}
            }
        }
    }

    private fun handleError(errorMessage: String?) = with(binding) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        progressIndicator.isGone = true
        authButton.isVisible = true
        emailLayout.isEnabled = true
        passwordLayout.isEnabled = true
    }

    private fun handleLoading() = with(binding) {
        progressIndicator.isVisible = true
        authButton.isGone = true
        emailLayout.isEnabled = false
        passwordLayout.isEnabled = false
    }
}
