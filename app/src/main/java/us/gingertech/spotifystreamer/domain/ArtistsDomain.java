package us.gingertech.spotifystreamer.domain;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import us.gingertech.spotifystreamer.R;

/**
 * Created by Matthew Harmon on 7/21/15.
 */
public class ArtistsDomain extends Domain {
    private static final String DATABASE_NAME = "artists";

    public ArtistsDomain(Context context) {
        super(context);
    }

    public void saveArtistsId(String artistsId) {
        try {
            openDatabase(DATABASE_NAME);
            snappyDB.put(getContext().getString(R.string.db_selected_artist_id), artistsId);
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving artist's id.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }

    public void saveCurrentPlayingArtist(String artistsId) {
        try {
            openDatabase(DATABASE_NAME);
            snappyDB.put(getContext().getString(R.string.db_current_artist_id), artistsId);
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving currently playing artist's id.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }

    public void saveArtistsSearchQuery(String query) {
        try {
            openDatabase(DATABASE_NAME);
            snappyDB.put(getContext().getString(R.string.db_search_query), query);
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving the artists search query.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }

    public void saveArtistsSearchResults(ArrayList<Artist> artists) {
        try {
            openDatabase(DATABASE_NAME);
            snappyDB.put(getContext().getString(R.string.db_artist_search_results), artists.toArray());
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving artist's id.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
    }
}
