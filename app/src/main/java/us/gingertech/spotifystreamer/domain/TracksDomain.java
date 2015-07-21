package us.gingertech.spotifystreamer.domain;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Track Domain class for saving data to SnappyDB.
 *
 * Created by Matthew Harmon on 7/20/15.
 */
public class TracksDomain {
    private Context context;

    public TracksDomain(Context context) {
        this.context = context;
    }

    public void saveCurrentTrackPosition(int currentTrackPosition) {
        try {
            DB tracksDB = DBFactory.open(context, "tracks");
            tracksDB.putInt("currentTrackPos", currentTrackPosition);
            tracksDB.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public void saveTopTracks(ArrayList<Track> tracks) {
        try {
            DB tracksDB = DBFactory.open(context, "tracks");
            for (int i = 0; i < tracks.size(); i++) {
                tracksDB.put(Integer.toString(i), tracks.get(i));
            }
            tracksDB.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public void saveArtistsId(String artistsId) {
        try {
            DB tracksDB = DBFactory.open(context, "tracks");
            tracksDB.put("artistsId", artistsId);
            tracksDB.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
