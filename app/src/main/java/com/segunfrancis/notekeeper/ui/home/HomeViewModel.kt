package com.segunfrancis.notekeeper.ui.home

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.segunfrancis.notekeeper.data.repository.INoteRepository
import com.segunfrancis.notekeeper.ui.model.NoteItem
import com.segunfrancis.notekeeper.util.AppConstants.SPOTLIGHT_PREF_KEY
import com.segunfrancis.notekeeper.util.AppConstants.dataStore
import com.segunfrancis.notekeeper.util.SingleLiveEvent
import com.segunfrancis.notekeeper.util.mapToUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: INoteRepository,
    private val app: Application
) : AndroidViewModel(app) {

    private val _action = SingleLiveEvent<MainActions>()
    val action: LiveData<MainActions> = _action

    private val _uiState = MutableLiveData<MainStates>()
    val uiState: LiveData<MainStates> = _uiState

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.postValue(MainStates.ErrorState(throwable.localizedMessage))
        Timber.e(throwable)
    }

    init {
        getAllNotes()
    }

    fun toAddNote(destination: String, id: String?) {
        _action.value = MainActions.Navigate(
            HomeFragmentDirections.toAddNoteFragment(destination = destination, id = id)
        )
    }

    private fun getAllNotes() {
        viewModelScope.launch(exceptionHandler) {
            repository.getAllNotes()
                .map { notes -> notes.map { it.mapToUi() } }
                .catch { _uiState.postValue(MainStates.ErrorState(it.localizedMessage)) }
                .collect {
                    if (it.isEmpty()) {
                        _uiState.postValue(MainStates.SuccessState.EmptyState)
                    } else {
                        _uiState.postValue(MainStates.SuccessState.NotEmptyState(it))
                    }
                }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            repository.deleteNoteById(id)
        }
    }

    fun getSeenSpotLight(): Flow<Boolean?> {
        return app.applicationContext.dataStore.data.map { pref ->
            pref[SPOTLIGHT_PREF_KEY]
        }
    }

    fun setSeenSpotLight(value: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            app.applicationContext.dataStore.edit { pref ->
                pref[SPOTLIGHT_PREF_KEY] = value
            }
        }
    }
}

sealed class MainStates {
    sealed class SuccessState : MainStates() {
        object EmptyState : MainStates()
        data class NotEmptyState(val data: List<NoteItem>) : MainStates()
    }

    data class ErrorState(val errorMessage: String?) : MainStates()
}

sealed class MainActions {
    data class Navigate(val destination: NavDirections) : MainActions()
}
