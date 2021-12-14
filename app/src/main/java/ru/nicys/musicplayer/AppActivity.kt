package ru.nicys.musicplayer

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.nicys.musicplayer.BuildConfig.BASE_URL
import ru.nicys.musicplayer.adapter.OnInteractionListener
import ru.nicys.musicplayer.adapter.TrackAdapter
import ru.nicys.musicplayer.databinding.ActivityAppBinding
import ru.nicys.musicplayer.dto.Track
import ru.nicys.musicplayer.viewModel.TracksViewModel

class AppActivity : AppCompatActivity() {
    private val viewModel: TracksViewModel by viewModels()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityAppBinding.inflate(layoutInflater)
    }

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private val mediaItemTransitionListener: Player.Listener = mediaItemTransitionListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlay(track: Track) {
                initializePlayer(track)
                binding.ivPlayButton.setImageResource(R.drawable.ic_pause_48)
            }

            override fun onPause(track: Track) {
                binding.ivPlayButton.setImageResource(R.drawable.ic_play_48)
                stopPlaying()
                binding.trackList.adapter?.notifyDataSetChanged()
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.getAlbum().collectLatest { album ->
                binding.apply {
                    tvTitleAlbum.text = album.title
                    tvNameArtist.text = album.artist
                    tvPublished.text = album.published
                    tvGenre.text = album.genre
                    ivPlayButton.setOnClickListener {
                        if (player?.isPlaying == false) {
                            initializePlayer(null)
                        } else {
                            stopPlaying()
                            ivPlayButton.setImageResource(R.drawable.ic_play_48)
                            viewModel.dataTracks.value?.map { it.isPlaying = false }
                            binding.trackList.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        viewModel.loadTracksExceptionEvent.observe(this, {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(R.string.error_loading)
                .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        })

        binding.trackList.adapter = adapter
        viewModel.dataTracks.observe(this, { tracklist ->
            adapter.submitList(tracklist)
        })
    }



    private fun initializePlayer(track: Track?) {
        binding.playerView.visibility = View.VISIBLE
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
                val mediaItems = mutableListOf<MediaItem>()
                viewModel.dataTracks.value?.forEach { track ->
                    mediaItems.addAll(
                        listOf(MediaItem.fromUri(BASE_URL + track.file))
                    )
                }
                if (track != null) {
                    val mediaItem = MediaItem.fromUri(BASE_URL + track.file)
                    val currentTrack = mediaItems.indexOf(mediaItem)
                    currentWindow = currentTrack
                }
                stopPlaying()
                exoPlayer.addMediaItems(mediaItems)
                exoPlayer.addListener(mediaItemTransitionListener)
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.prepare()
                exoPlayer.repeatMode
            }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            removeListener(mediaItemTransitionListener)
            release()
        }
        player = null
    }

    private fun stopPlaying() {
        player?.run {
            release()
        }
    }

    private fun mediaItemTransitionListener() = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            lifecycleScope.launch {
                when (reason) {
                    ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                        refreshUI()
                    }
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                        delay(10)
                        if (player?.isPlaying == true) refreshUI()
                    }
                    else -> null
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            when (isPlaying) {
                false -> {
                    viewModel.dataTracks.value?.map { it.isPlaying = false }
                    binding.ivPlayButton.setImageResource(R.drawable.ic_play_48)
                    binding.trackList.adapter?.notifyDataSetChanged()
                }
                true -> {
                    binding.ivPlayButton.setImageResource(R.drawable.ic_pause_48)
                    refreshUI()
                }
            }
        }
    }

    private fun refreshUI() {
        viewModel.dataTracks.value?.let { tracklist ->
            tracklist.map { it.isPlaying = false }
            player?.let { currentWindow = player!!.currentWindowIndex }
            val playingTrack = tracklist.find { it.id == player?.nextWindowIndex }
            playingTrack?.isPlaying = true
            binding.trackList.adapter?.notifyDataSetChanged()
        }
    }

    public override fun onResume() {
        super.onResume()
        initializePlayer(null)
    }

    public override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}