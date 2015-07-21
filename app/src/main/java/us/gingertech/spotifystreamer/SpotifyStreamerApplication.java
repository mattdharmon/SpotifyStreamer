package us.gingertech.spotifystreamer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import us.gingertech.spotifystreamer.SpotifyStreamerMediaPlayerService.MusicServiceBinder;

/**
 * Created by Matthew Harmon on 7/21/15.
 */
public class SpotifyStreamerApplication extends Application {
    private Intent playerIntent;
    private SpotifyStreamerMediaPlayerService playerService;

    @Override
    public void onCreate() {
        super.onCreate();
        if (playerIntent == null) {
            playerIntent = new Intent(this, SpotifyStreamerMediaPlayerService.class);
            bindService(playerIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (playerIntent != null) {
            stopService(playerIntent);
        }
    }

    public SpotifyStreamerMediaPlayerService getPlayerService() {
        return playerService;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicServiceBinder binder = (MusicServiceBinder) service;
            //get service
            playerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
