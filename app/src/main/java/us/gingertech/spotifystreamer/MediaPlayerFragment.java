package us.gingertech.spotifystreamer;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import us.gingertech.spotifystreamer.SpotifyStreamerMediaPlayerService.MusicServiceBinder;
import us.gingertech.spotifystreamer.repository.TracksRepository;


public class MediaPlayerFragment extends DialogFragment implements
        MediaServiceListener
{
    private SpotifyStreamerApplication application;
    private SpotifyStreamerMediaPlayerService playerService;
    private Intent playerIntent;
    private TracksRepository tracksRepository;
    private Resources res;

    @Bind(R.id.media_album_imageview)
    public ImageView ivAlbum;

    @Bind(R.id.seek_bar)
    public SeekBar sbCurrent;

    @Bind(R.id.ic_prev_track)
    public ImageView ivPrevTrack;

    @Bind(R.id.ic_media_play)
    public ImageView ivPlay;

    @Bind(R.id.ic_media_next)
    public ImageView ivNextTrack;

    @Bind(R.id.duration)
    public TextView duration;

    @Bind(R.id.track_length)
    public TextView totalTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracksRepository = new TracksRepository(getActivity());
        application = (SpotifyStreamerApplication) getActivity().getApplication();
        playerService = application.getPlayerService();
        playerService.setFragmentListener(this);
        res = getResources();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.fragment_media_player, null);
        ButterKnife.bind(this, rootView);
        if (
            !playerService.isPrepared
            || playerService.currentTrackPlayingPosition != tracksRepository.getCurrentTrackPosition()
        ) {
            playerService.start();
        } else {
            initView();
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick(R.id.ic_media_next)
    public void playNextTrack() {
        playerService.playNextTrack();
    }

    @OnClick(R.id.ic_prev_track)
    public void playPrevTrack() {
        playerService.playPrevTrack();
    }

    @OnClick(R.id.ic_media_play)
    public void playCurrentClick() {
        // If the media player is playing, pause it.
        if (playerService.isPlaying()) {
            playerService.pause();
            initView();
            return;
        }
        // Otherwise play it.
        playerService.play();
        initView();
    }

    /**
     * Update the SeekBar to update it's position.
     */
    public void seekBarUpdate() {
        if (!playerService.isPlaying()) {
            return;
        }
        sbCurrent.setMax(playerService.getDuration());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(playerService.getCurrentPosition()) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(playerService.getCurrentPosition()) % TimeUnit.MINUTES.toSeconds(1);
        if (playerService.getCurrentPosition() < 0) {
            minutes = 0;
            seconds = 0;
            sbCurrent.setProgress(0);
        } else {
            sbCurrent.setProgress(playerService.getCurrentPosition());
        }

        String hms = String.format("%02d:%02d", minutes, seconds);
        duration.setText(hms);

        Runnable run = new Runnable() {
            @Override
            public void run() {
                seekBarUpdate();
            }
        };
        new Handler().postDelayed(run, 1000);
    }

    @Override
    public void onPrepared() {
        playCurrentClick();
        initView();
    }

    @Override
    public void onCompletion() {
        playNextTrack();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }

        Picasso.with(getActivity())
                .load(playerService.getCurrentTrack().album.images.get(0).url)
                .fit()
                .into(ivAlbum);

        if (playerService.isPlaying()) {
            ivPlay.setImageDrawable(res.getDrawable(android.R.drawable.ic_media_pause));
        }

        if (!playerService.hasNextTrack()) {
            ivNextTrack.setEnabled(false);
        }

        if (!playerService.hasPrevTrack()) {
            ivPrevTrack.setEnabled(false);
        }

        if (!playerService.isPlaying()) {
            ivPlay.setImageDrawable(res.getDrawable(android.R.drawable.ic_media_play));
        }

        seekBarUpdate();
    }
}
