package com.segunfrancis.notekeeper.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.segunfrancis.notekeeper.data.repository.INoteRepository
import com.segunfrancis.notekeeper.ui.model.NoteItem
import com.segunfrancis.notekeeper.util.SingleLiveEvent
import com.segunfrancis.notekeeper.util.mapToData
import com.segunfrancis.notekeeper.util.mapToUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val repository: INoteRepository
) : ViewModel() {

    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()
    private val _uiState =
        MutableLiveData<AddNoteState>(AddNoteState.SaveButtonState(enable = false))
    val uiState: LiveData<AddNoteState> = _uiState

    private val _action = SingleLiveEvent<AddNoteAction>()
    val action: LiveData<AddNoteAction> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.postValue(AddNoteState.ErrorState(throwable.localizedMessage))
        Timber.e(throwable)
    }

    fun getNote(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            val note = repository.getSingleNote(id = id)
            _uiState.postValue(AddNoteState.ContentState(note = note.mapToUi()))
        }
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setMenuItemState() {
        _title.value?.let { title ->
            _description.value?.let { description ->
                if (title.length > 5 && description.length > 20) {
                    _uiState.value = AddNoteState.SaveButtonState(enable = true)
                } else {
                    _uiState.value = AddNoteState.SaveButtonState(enable = false)
                }
            }
        }
    }

    fun addNote(note: NoteItem) {
        viewModelScope.launch(exceptionHandler) {
            repository.addNote(note = note.mapToData())
            _action.postValue(AddNoteAction.NavigateUp)
        }
    }

    fun updateNote(note: NoteItem) {
        viewModelScope.launch(exceptionHandler) {
            repository.updateNote(note.mapToData())
            _action.postValue(AddNoteAction.NavigateUp)
        }
    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch(exceptionHandler) {
            repository.deleteNote(note = note.mapToData())
            _action.postValue(AddNoteAction.NavigateUp)
        }
    }
}

sealed class AddNoteState {
    data class SaveButtonState(val enable: Boolean) : AddNoteState()
    data class ContentState(val note: NoteItem): AddNoteState()
    data class ErrorState(val errorMessage: String?): AddNoteState()
}

sealed class AddNoteAction {
    object NavigateUp : AddNoteAction()
}
