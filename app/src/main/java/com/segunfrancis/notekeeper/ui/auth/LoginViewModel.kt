package com.segunfrancis.notekeeper.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.segunfrancis.notekeeper.data.repository.INoteRepository
import com.segunfrancis.notekeeper.util.SingleLiveEvent
import com.segunfrancis.notekeeper.util.isEmailValid
import com.segunfrancis.notekeeper.util.isPasswordValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: INoteRepository) : ViewModel() {
    private val _uiState = MutableLiveData<LoginStates>()
    val uiState: LiveData<LoginStates> = _uiState

    private val _action = SingleLiveEvent<LoginAction>()
    val action: LiveData<LoginAction> = _action

    private lateinit var password: String
    private lateinit var email: String

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.postValue(LoginStates.ErrorState(throwable.localizedMessage))
        Timber.e(throwable)
    }

    fun setPassword(password: String) {
        this.password = password
        if (this::email.isInitialized) {
            if (this.password.isPasswordValid() && this.email.isEmailValid()) {
                _uiState.value = LoginStates.ButtonState(enable = true)
            } else {
                _uiState.value = LoginStates.ButtonState(enable = false)
            }
        }
    }

    fun setEmail(email: String) {
        this.email = email
        if (this::password.isInitialized) {
            if (this.password.isPasswordValid() && this.email.isEmailValid()) {
                _uiState.value = LoginStates.ButtonState(enable = true)
            } else {
                _uiState.value = LoginStates.ButtonState(enable = false)
            }
        }
    }

    fun createUser(email: String, password: String) {
        _uiState.postValue(LoginStates.LoadingState)
        viewModelScope.launch(exceptionHandler) {
            kotlin.runCatching {
                repository.createUser(email, password)
            }.fold(onSuccess = {
                _action.postValue(LoginAction.Navigate(LoginFragmentDirections.toHomeFragment()))
            }, onFailure = {
                _uiState.postValue(LoginStates.ErrorState(it.localizedMessage))
            })
        }
    }

    fun loginUser(email: String, password: String) {
        _uiState.postValue(LoginStates.LoadingState)
        viewModelScope.launch(exceptionHandler) {
            kotlin.runCatching {
                repository.loginUser(email, password)
            }.fold(onSuccess = {
                _action.postValue(LoginAction.Navigate(LoginFragmentDirections.toHomeFragment()))
            }, onFailure = {
                _uiState.postValue(LoginStates.ErrorState(it.localizedMessage))
            })
        }
    }
}

sealed class LoginAction {
    data class Navigate(val destination: NavDirections) : LoginAction()
}

sealed class LoginStates {
    object LoadingState : LoginStates()
    data class ButtonState(val enable: Boolean) : LoginStates()
    data class ErrorState(val errorMessage: String?) : LoginStates()
}
