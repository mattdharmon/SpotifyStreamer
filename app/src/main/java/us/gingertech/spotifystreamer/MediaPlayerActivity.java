package us.gingertech.spotifystreamer;

import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Matthew Harmon on 7/6/15.
 *
 * @TODO: Move this MediaPlayer to a fragment.
 *
 */
public class MediaPlayerActivity extends AppCompatActivity implements
        OnPreparedListener,
        OnErrorListener
{
    protected Track currentTrack;
    protected int currentTrackPos;
    public MediaPlayer mediaPlayer;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_activity);
        ButterKnife.bind(this);

        // Build out the MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // @TODO: Move this data to SnappyDB.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_UID)) {
            intent.getIntExtra(Intent.EXTRA_UID, currentTrackPos);
        }

        // Prepare the current track.
        prepareTrack();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    @OnClick(R.id.ic_media_next)
    public void playNextTrack() {
        try {
            DB tracksDB = DBFactory.open(getApplicationContext(), "tracks");
            KeyIterator it = tracksDB.findKeysIterator(Integer.toString(currentTrackPos));
            if (it.hasNext()) {
                String[] nextTrack = it.next(1);
                currentTrackPos = Integer.parseInt(nextTrack[0]);
                prepareTrack();
            }
            it.close();
        } catch (SnappydbException e) {
            Logger.e(e, "In Prepare track.");
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ic_prev_track)
    public void playPrevTrack() {
    }

    @OnClick(R.id.ic_media_play)
    public void playCurrentClick() {
        // If the media player is playing, pause it.
        if (mediaPlayer.isPlaying()) {
            pause();
            return;
        }
        // Otherwise play it.
        play();
    }

    /**
     * Update the SeekBar to update it's position.
     */
    public void seekBarUpdate() {
        sbCurrent.setMax(mediaPlayer.getDuration());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()) % TimeUnit.MINUTES.toSeconds(1);
        if (mediaPlayer.getCurrentPosition() < 0) {
            minutes = 0;
            seconds = 0;
            sbCurrent.setProgress(0);
        } else {
            sbCurrent.setProgress(mediaPlayer.getCurrentPosition());
        }

        String hms = String.format("%02d:%02d", minutes, seconds);
        duration.setText(hms);

        if (mediaPlayer.isPlaying()) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    seekBarUpdate();
                }
            };
            new Handler().postDelayed(run, 500);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * The MediaPlayer.OnPreparedListener function.
     *
     * @param mp The MediaPlayer object once prepare is done.
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        // Human readable output of how long the song is.
        String hms = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) % TimeUnit.MINUTES.toSeconds(1)
        );
        duration.setText("00:00");
        totalTime.setText(" - ".concat(hms));

        // Load the album.
        Picasso.with(this)
                .load(currentTrack.album.images.get(0).url)
                .fit()
                .into(ivAlbum);
        // Play the track.
        play();
    }

    /**
     * Prepare the MediaPlayer for the current track to be played.
     */
    private void prepareTrack() {
        try {
            // Get the current track
            DB tracksDB = DBFactory.open(getApplicationContext(), "tracks");
            currentTrack = tracksDB.getObject(Integer.toString(currentTrackPos), Track.class);
            tracksDB.close();

            // Build the current player.
            mediaPlayer.setDataSource(currentTrack.preview_url);
            mediaPlayer.prepareAsync();
        } catch (SnappydbException | IOException e) {
            Logger.e(e, "In Prepare track.");
            e.printStackTrace();
        }
    }

    /**
     * Starts the MediaPlayer and changes the icon to the pause icon.
     */
    private void play() {
        Resources res = getResources();
        ivPlay.setImageDrawable(res.getDrawable(android.R.drawable.ic_media_pause));
        mediaPlayer.start();
        seekBarUpdate();
    }

    /**
     * Pauses the MediaPlayer and changes the icon to the play icon.
     */
    private void pause() {
        Resources res = getResources();
        ivPlay.setImageDrawable(res.getDrawable(android.R.drawable.ic_media_play));
        mediaPlayer.pause();
    }
}
