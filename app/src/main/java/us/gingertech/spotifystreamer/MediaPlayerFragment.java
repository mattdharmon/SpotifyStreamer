package us.gingertech.spotifystreamer;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import kaaes.spotify.webapi.android.models.Image;
import us.gingertech.spotifystreamer.repository.TracksRepository;


public class MediaPlayerFragment extends DialogFragment implements
        MediaServiceListener,
        SeekBar.OnSeekBarChangeListener
{
    private SpotifyStreamerMediaPlayerService playerService;
    private TracksRepository tracksRepository;
    private Resources res;
    private boolean isSeeking = false;
    private Handler handler;

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
        SpotifyStreamerApplication application = (SpotifyStreamerApplication) getActivity().getApplication();
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

        sbCurrent.setOnSeekBarChangeListener(this);
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    /**
     * Update the SeekBar to update it's position.
     */
    public void seekBarUpdate() {
        if (!playerService.isPlaying()) {
            return;
        }

        if (isSeeking) {
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

        if (!sbCurrent.isEnabled()) {
            sbCurrent.setEnabled(true);
        }

        String hms = String.format("%02d:%02d", minutes, seconds);
        duration.setText(hms);

        handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                seekBarUpdate();
            }
        };
        handler.postDelayed(run, 1000);
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

    @Override
    public void onSeekComplete() {
        playCurrentClick();
    }

    @Override
    public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            playerService.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        playCurrentClick();
    }

    @Override
    public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
        sbCurrent.setEnabled(false);
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }

        Image image = playerService.getCurrentTrack().album.images.get(0);
        Picasso.with(getActivity())
                .load(image.url)
                .centerInside()
                .resize(image.width, image.height)
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
