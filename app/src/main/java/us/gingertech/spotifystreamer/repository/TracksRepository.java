package us.gingertech.spotifystreamer.repository;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import kaaes.spotify.webapi.android.models.Track;

/**
 * The Track Repository class for retrieving data from SnappyDB
 *
 * Created by Matthew Harmon on 7/20/15.
 */
public class TracksRepository {
    private Context context;

    public TracksRepository(Context context) {
        this.context = context;
    }

    public String getSelectedArtistId() {
        try {
            // Get the intent to get the tracks for the artist.
            DB tracksDB = DBFactory.open(context, "tracks");
            String artistId = tracksDB.get("artistsId");
            tracksDB.close();
            return artistId;
        } catch (SnappydbException e) {
            Logger.e(e, "Get Current track Position.");
            e.printStackTrace();
        }
        return null;
    }

    public int getCurrentTrackPosition() {
        try {
            DB tracksDB = DBFactory.open(context, "tracks");
            int currentTrackPos = tracksDB.getInt("currentTrackPos");
            tracksDB.close();
            return currentTrackPos;
        } catch (SnappydbException e) {
            Logger.e(e, "Get Current track Position.");
            Logger.e(e, "In Prepare track.");
            e.printStackTrace();
        }
        return 0;
    }

    public Track getTrack(int key) {
        return getTrack(Integer.toString(key));
    }

    public Track getTrack(String key) {
        try {
            // Get the current track
            DB tracksDB = DBFactory.open(context, "tracks");
            Track track = tracksDB.getObject(key, Track.class);
            tracksDB.close();
            return track;
        } catch (SnappydbException e) {
            Logger.e(e, "Get track error. Key: ".concat(key));
            Logger.e(e, "In Prepare track.");
            e.printStackTrace();
        }
        return null;
    }

    public boolean trackExists(int key) {
        return trackExists(Integer.toString(key));
    }

    public boolean trackExists(String key) {
        boolean results = false;
        try {
            DB tracksDB = DBFactory.open(context, "tracks");
            results = tracksDB.exists(key);
            tracksDB.close();
        } catch (SnappydbException e) {
            Logger.e(e, "Track exists error. Key: ".concat(key));
            e.printStackTrace();
        }
        return results;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
