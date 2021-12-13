package ru.nicys.musicplayer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import ru.nicys.musicplayer.BuildConfig.BASE_URL
import ru.nicys.musicplayer.adapter.OnInteractionListener
import ru.nicys.musicplayer.adapter.TrackAdapter
import ru.nicys.musicplayer.databinding.ActivityAppBinding
import ru.nicys.musicplayer.dto.Track
import ru.nicys.musicplayer.viewModel.TracksViewModel

class AppActivity : AppCompatActivity() {
    private val viewModel: TracksViewModel by viewModels()
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(mediaObserver)

        val adapter = TrackAdapter(object : OnInteractionListener {
            override fun onPlay(track: Track) {
                viewModel.isPlaying(track.id)
                mediaObserver.apply {
                    player?.setDataSource(BASE_URL + track.file)
                }.play()
            }

            override fun onPause(track: Track) {
                mediaObserver.onPause()
            }
        })

        binding.trackList.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.getAlbum().collectLatest { album ->
                binding.apply {
                    tvTitleAlbum.text = album.title
                    tvNameArtist.text = album.artist
                    tvPublished.text = album.published
                    tvGenre.text = album.genre
                }
            }
        }

        viewModel.dataTracks.observe(this) { tracks ->
            adapter.submitList(tracks)
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
    }
}