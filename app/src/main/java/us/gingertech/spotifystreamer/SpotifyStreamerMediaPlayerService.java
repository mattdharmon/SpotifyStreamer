package us.gingertech.spotifystreamer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.domain.TracksDomain;
import us.gingertech.spotifystreamer.repository.TracksRepository;

public class SpotifyStreamerMediaPlayerService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener
{
    private static final int NOTIFY_ID=1;
    private NotificationManager notificationManager;
    private Track currentTrack;
    private MediaPlayer mediaPlayer;
    private MediaServiceListener fragmentListener;
    private final IBinder musicServiceBinder = new MusicServiceBinder();
    private TracksDomain tracksDomain;
    private TracksRepository tracksRepository;

    public int currentTrackPlayingPosition;
    public boolean isPrepared = false;

    public SpotifyStreamerMediaPlayerService() {

    }

    public class MusicServiceBinder extends Binder {
        public SpotifyStreamerMediaPlayerService getService() {
            return SpotifyStreamerMediaPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tracksDomain = new TracksDomain(getApplicationContext());
        tracksRepository = new TracksRepository(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    public void start() {
        prepareTrack();
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return musicServiceBinder;
    }

    @Override
    public void onDestroy() {
        kill();
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logger.e("Media Player error.");
        softKill();
        return false;
    }

    @Override
    public void onPrepared(@NonNull MediaPlayer mp) {
        isPrepared = true;
        currentTrackPlayingPosition = tracksRepository.getCurrentTrackPosition();
        fragmentListener.onPrepared();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        softKill();
        fragmentListener.onCompletion();
    }

    public void kill() {
        softKill();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void softKill() {
        mediaPlayer.reset();
        stopForeground(true);
        isPrepared = false;
        notificationManager.cancel(NOTIFY_ID);
    }

    /**
     * Starts the MediaPlayer and changes the icon to the pause icon.
     */
    public void play() {
        mediaPlayer.start();
        buildNotification();
    }

    public void playNextTrack() {
        if (isPrepared) {
            softKill();
        }
        if (hasNextTrack()) {
            setCurrentTrackPos(incrementCurrentTrack());
            prepareTrack();
        }
    }

    public void playPrevTrack() {
        if (isPrepared) {
            softKill();
        }
        if (hasPrevTrack()) {
            setCurrentTrackPos(decrementCurrentTrack());
            prepareTrack();
        }
    }

    public boolean hasNextTrack() {
        return tracksRepository.trackExists(incrementCurrentTrack());
    }

    public boolean hasPrevTrack() {
        return tracksRepository.trackExists(decrementCurrentTrack());
    }

    /**
     * Pauses the MediaPlayer and changes the icon to the play icon.
     */
    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    private void initMediaPlayer() {
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setLooping(false);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * Prepare the MediaPlayer for the current track to be played.
     */
    private void prepareTrack() {
        try {
            if (isPlaying()) {
                softKill();
            }
            // Get the current track
            currentTrack = tracksRepository.getTrack(tracksRepository.getCurrentTrackPosition());
            Logger.d(currentTrack.name);

            // Build the current player.
            mediaPlayer.setDataSource(currentTrack.preview_url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildNotification() {
        //notification
        Intent notificationIntent = new Intent(this, TrackList.class);
        PendingIntent pendInt = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker(currentTrack.name)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(currentTrack.name);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFY_ID, notification);
    }

    public void setFragmentListener(MediaServiceListener fragmentListener) {
        this.fragmentListener = fragmentListener;
    }

    private void setCurrentTrackPos(int position) {
        tracksDomain.saveCurrentTrackPosition(position);
    }

    protected int incrementCurrentTrack() {
        return currentTrackPlayingPosition + 1;
    }

    protected int decrementCurrentTrack() {
        return currentTrackPlayingPosition - 1;
    }
}
