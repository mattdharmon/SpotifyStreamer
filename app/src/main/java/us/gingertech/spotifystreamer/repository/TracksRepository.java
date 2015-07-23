package us.gingertech.spotifystreamer.repository;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.R;

/**
 * The Track Repository class for retrieving data from SnappyDB
 *
 * Created by Matthew Harmon on 7/20/15.
 */
public class TracksRepository extends Repository {
    private static final String DATABASE_NAME = "tracks";

    public TracksRepository(Context context) {
        super(context);
    }

    public int getCurrentTrackPosition() {
        int results = 0;
        try {
            openDatabase(DATABASE_NAME);
            results = snappyDB.getInt(getContext().getString(R.string.db_current_track_pos));
        } catch (SnappydbException e) {
            Logger.e(e, "Get Current track Position.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }

    public Track getTrack(int key) {
        return getTrack(Integer.toString(key));
    }

    public Track getTrack(String key) {
        Track track = null;
        try {
            // Get the current track
            openDatabase(DATABASE_NAME);
            track = snappyDB.getObject(key, Track.class);
        } catch (SnappydbException e) {
            Logger.e(e, "Get track error. Key: ".concat(key));
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return track;
    }

    public boolean trackExists(int key) {
        return trackExists(Integer.toString(key));
    }

    public boolean trackExists(String key) {
        boolean results = false;
        try {
            openDatabase(DATABASE_NAME);
            results = snappyDB.exists(key);
        } catch (SnappydbException e) {
            Logger.e(e, "Track exists error. Key: ".concat(key));
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }

    public ArrayList<Track> getTracks() {
        ArrayList<Track> results = null;
        try {
            openDatabase(DATABASE_NAME);
            Track[] tracks = snappyDB.getObjectArray(
                    getContext().getString(R.string.db_cached_tracks),
                    Track.class
            );
            results = new ArrayList<>(Arrays.asList(tracks));
        } catch (SnappydbException e) {
            Logger.e(e, "Get an array list of tracks.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }
}
