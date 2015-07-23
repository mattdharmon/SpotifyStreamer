package us.gingertech.spotifystreamer.repository;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.models.Artist;
import us.gingertech.spotifystreamer.R;

/**
 * Created by Matthew Harmon on 7/21/15.
 */
public class ArtistsRepository extends Repository {
    private static final String DATABASE_NAME = "artists";

    public ArtistsRepository(Context context) {
        super(context);
    }

    public String getSelectedArtistId() {
        String results = null;
        try {
            // Get the intent to get the tracks for the artist.
            openDatabase(DATABASE_NAME);
            results = snappyDB.get(getContext().getString(R.string.db_selected_artist_id));
        } catch (SnappydbException e) {
            Logger.e(e, "Get selected artist's id.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }

    public String getCurrentArtistId() {
        String results = null;
        try {
            // Get the intent to get the tracks for the artist.
            openDatabase(DATABASE_NAME);
            results = snappyDB.get(getContext().getString(R.string.db_current_artist_id));
        } catch (SnappydbException e) {
            Logger.e(e, "Get currently playing artist's id.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }

    public String getSelectedArtistsName() {
        String results = null;
        try {
            openDatabase(DATABASE_NAME);
            results = snappyDB.get(getContext().getString(R.string.db_current_artists_name));
        } catch (SnappydbException e) {
            Logger.e(e, "Error in saving the selected artist's name.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }

    public String getArtistsSearchQuery() {
        String results = null;
        try {
            openDatabase(DATABASE_NAME);
            results = snappyDB.get(getContext().getString(R.string.db_search_query));
            // Get the intent to get the tracks for the artist.
        } catch (SnappydbException e) {
            Logger.e(e, "Get artists search query.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }

    public ArrayList<Artist> getArtistsSearchResults() {
        ArrayList<Artist> results = null;
        try {
            openDatabase(DATABASE_NAME);
            Artist[] artists = snappyDB.getObjectArray(
                    getContext().getString(R.string.db_artist_search_results),
                    Artist.class
            );
            results = new ArrayList<>(Arrays.asList(artists));
        } catch (SnappydbException e) {
            Logger.e(e, "Get artist search results");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }
}
