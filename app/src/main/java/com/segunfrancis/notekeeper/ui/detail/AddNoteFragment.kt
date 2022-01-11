package com.segunfrancis.notekeeper.ui.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.FragmentAddNoteBinding
import com.segunfrancis.notekeeper.ui.model.NoteItem
import com.segunfrancis.notekeeper.util.Destination
import com.segunfrancis.notekeeper.util.displayDialog
import com.segunfrancis.notekeeper.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteFragment : Fragment(R.layout.fragment_add_note) {

    private val binding by viewBinding(FragmentAddNoteBinding::bind)
    private val viewModel by viewModels<AddNoteViewModel>()
    private val args: AddNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        args.id?.let {
            viewModel.getNote(it.toLong())
        }
        setupObservers()
    }

    private fun setupObservers() {
        binding.titleField.addTextChangedListener {
            it?.let { viewModel.setTitle(it.toString()) }
            viewModel.setMenuItemState()
        }
        binding.descriptionField.addTextChangedListener {
            it?.let { viewModel.setDescription(it.toString()) }
            viewModel.setMenuItemState()
        }
        viewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                AddNoteAction.NavigateUp -> findNavController().navigateUp()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_add).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = false
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is AddNoteState.SaveButtonState -> menu.findItem(R.id.action_save).isEnabled =
                    it.enable
                is AddNoteState.ContentState -> setupContent(it.note, menu)
                is AddNoteState.ErrorState -> handleError(it.errorMessage)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            if (Destination.valueOf(args.destination) == Destination.NEW) {
                addNote()
            } else {
                updateNote()
            }
        } else if (item.itemId == R.id.action_delete) {
            deleteNote()
        }
        return true
    }

    private fun addNote() {
        viewModel.addNote(
            NoteItem(
                id = 0,
                title = binding.titleField.text.toString(),
                description = binding.descriptionField.text.toString()
            )
        )
    }

    private fun updateNote() {
        viewModel.updateNote(
            NoteItem(
                id = args.id!!.toLong(),
                title = binding.titleField.text.toString().trim(),
                description = binding.descriptionField.text.toString().trim()
            )
        )
    }

    private fun deleteNote() {
        displayDialog(
            title = R.string.delete,
            description = R.string.delete_message,
            callback = {
                viewModel.deleteNote(
                    NoteItem(
                        id = args.id!!.toLong(),
                        title = binding.titleField.text.toString().trim(),
                        description = binding.descriptionField.text.toString().trim()
                    )
                )
            }
        )
    }

    private fun handleError(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setupContent(note: NoteItem, menu: Menu) = with(binding) {
        titleField.setText(note.title)
        descriptionField.setText(note.description)
        root.requestFocus()
        menu.findItem(R.id.action_delete).isVisible = true
    }
}
