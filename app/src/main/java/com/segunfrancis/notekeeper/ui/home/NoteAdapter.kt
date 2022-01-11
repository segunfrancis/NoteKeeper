package com.segunfrancis.notekeeper.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.NoteItemBinding
import com.segunfrancis.notekeeper.ui.model.NoteItem

class NoteAdapter(
    private val onNoteItemClick: (Long) -> Unit,
    private val onDeleteItemClick: (Long) -> Unit
) : ListAdapter<NoteItem, NoteAdapter.NoteViewHolder>(NOTE_DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.bind(
                LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentNote = currentList[adapterPosition]
                onNoteItemClick.invoke(currentNote.id)
            }
        }

        fun bind(note: NoteItem) = with(binding) {
            titleText.text = note.title
            descriptionText.text = note.description
            deleteImage.setOnClickListener { onDeleteItemClick.invoke(note.id) }
        }
    }

    companion object {
        val NOTE_DIFF_UTIL = object : DiffUtil.ItemCallback<NoteItem>() {
            override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem) =
                oldItem == newItem
        }
    }
}
