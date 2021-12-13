package ru.nicys.musicplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.nicys.musicplayer.R
import ru.nicys.musicplayer.databinding.TrackCardBinding
import ru.nicys.musicplayer.dto.Track

interface OnInteractionListener {
    fun onPlay(track: Track) {}
    fun onPause(track: Track) {}
}

class TrackAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Track, TrackViewHolder>(TrackDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }

    }
}

class TrackViewHolder(
    private val binding: TrackCardBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        binding.apply {
            tvTrackName.text = track.file
            if (track.isPlaying) {
                fabPlayButton.setImageResource(R.drawable.ic_pause_48)
            } else {
                fabPlayButton.setImageResource(R.drawable.ic_play_48)
            }

            fabPlayButton.setOnClickListener {
                when (track.isPlaying) {
                    false -> {
                        onInteractionListener.onPlay(track)
                        track.isPlaying = true
                    }
                    true -> {
                        onInteractionListener.onPause(track)
                        track.isPlaying = false
                    }
                }
//                onInteractionListener.onPlay(track)
            }
        }
    }
}

class TrackDiffCallBack : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}