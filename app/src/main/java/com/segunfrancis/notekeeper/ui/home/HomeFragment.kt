package com.segunfrancis.notekeeper.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.FragmentHomeBinding
import com.segunfrancis.notekeeper.ui.model.NoteItem
import com.segunfrancis.notekeeper.util.Destination
import com.segunfrancis.notekeeper.util.displayDialog
import com.segunfrancis.notekeeper.util.viewBinding
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel by viewModels<HomeViewModel>()
    private val noteAdapter: NoteAdapter by lazy {
        NoteAdapter(onNoteItemClick = {
            viewModel.toAddNote(
                destination = Destination.UPDATE.name,
                id = it.toString()
            )
        }, onDeleteItemClick = {
            deleteNote(id = it)
        })
    }
    private lateinit var spotlight: Spotlight

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                is MainActions.Navigate -> findNavController().navigate(it.destination)
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                MainStates.SuccessState.EmptyState -> handleEmptyState()
                is MainStates.SuccessState.NotEmptyState -> handleSuccess(it.data)
                is MainStates.ErrorState -> handleError(it.errorMessage)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        lifecycleScope.launch {
            viewModel.getSeenSpotLight()
                .collect {
                    if (it != true) {
                        displaySpotlight()
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            if (::spotlight.isInitialized) {
                spotlight.finish()
            }
            viewModel.toAddNote(destination = Destination.NEW.name, id = null)
            viewModel.setSeenSpotLight(value = true)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_save).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = false
    }

    private fun handleSuccess(notes: List<NoteItem>) = with(binding) {
        noteRecyclerView.isVisible = true
        noteRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
        noteAdapter.submitList(notes)
        emptyStateView.isGone = true
        noteAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

                binding.noteRecyclerView.scrollToPosition(0)
            }
        })
    }

    private fun handleEmptyState() {
        binding.emptyStateView.isVisible = true
        binding.noteRecyclerView.isGone = true
    }

    private fun handleError(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        binding.emptyStateView.isGone = true
    }

    private fun deleteNote(id: Long) {
        displayDialog(
            title = R.string.delete,
            description = R.string.delete_message,
            callback = { viewModel.deleteNote(id) }
        )
    }

    private fun displaySpotlight() {
        lifecycleScope.launch(Dispatchers.Main) {
            val item = requireActivity().findViewById<View>(R.id.action_add)
            binding.root.doOnPreDraw {
                val thirdRoot = FrameLayout(requireContext())
                val overlay = layoutInflater.inflate(R.layout.layout_target, thirdRoot)
                val target = Target.Builder()
                    .setAnchor(item)
                    .setShape(Circle(64F))
                    .setOverlay(overlay)
                    .build()

                spotlight = Spotlight.Builder(requireActivity())
                    .setTargets(target)
                    .setBackgroundColorRes(R.color.spotlightBackground)
                    .setAnimation(DecelerateInterpolator(2f))
                    .build()

                spotlight.start()
            }
        }
    }
}
