package com.segunfrancis.notekeeper.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.google.firebase.auth.FirebaseAuth
import com.segunfrancis.notekeeper.util.Destination
import com.segunfrancis.notekeeper.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _action = SingleLiveEvent<OnBoardingAction>()
    val action: LiveData<OnBoardingAction> = _action

    init {
        if (firebaseAuth.currentUser != null) {
            _action.value = OnBoardingAction.Navigate(OnBoardingFragmentDirections.toHomeFragment())
        }
    }

    fun goTo(destination: Destination) {
        _action.value = OnBoardingAction.Navigate(
            OnBoardingFragmentDirections.toLoginFragment(destination.name)
        )
    }
}

sealed class OnBoardingAction {
    data class Navigate(val destination: NavDirections) : OnBoardingAction()
}
