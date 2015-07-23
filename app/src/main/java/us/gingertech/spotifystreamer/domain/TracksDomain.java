package us.gingertech.spotifystreamer.domain;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.R;

/**
 * Track Domain class for saving data to SnappyDB.
 *
 * Created by Matthew Harmon on 7/20/15.
 */
public class TracksDomain extends Domain {
    private static final String DATABASE_NAME = "tracks";

    public TracksDomain(Context context) {
        super(context);
    }

    public void saveCurrentTrackPosition(int currentTrackPosition) {
        try {
            openDatabase(DATABASE_NAME);
            snappyDB.putInt(getContext().getString(R.string.db_current_track_pos), currentTrackPosition);
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving current track position.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }

    public void saveTopTracks(ArrayList<Track> tracks) {
        try {
            openDatabase(DATABASE_NAME);
            for (int i = 0; i < tracks.size(); i++) {
                snappyDB.put(Integer.toString(i), tracks.get(i));
            }
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving top tracks.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }

    public void saveTracks(ArrayList<Track> tracks) {

        try {
            openDatabase(DATABASE_NAME);
            snappyDB.put(getContext().getString(R.string.db_cached_tracks), tracks.toArray());
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving top tracks.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }
}
