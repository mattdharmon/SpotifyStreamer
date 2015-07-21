package us.gingertech.spotifystreamer.repository;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.models.Artist;

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
            results = snappyDB.get("artistsId");
        } catch (SnappydbException e) {
            Logger.e(e, "Get Current track Position.");
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
            results = snappyDB.get("searchQuery");
            // Get the intent to get the tracks for the artist.
        } catch (SnappydbException e) {
            Logger.e(e, "Get Current track Position.");
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
            Artist[] artists = snappyDB.getObjectArray("searchResults", Artist.class);
            results = new ArrayList<>(Arrays.asList(artists));
        } catch (SnappydbException e) {
            Logger.e(e, "Get Current track Position.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }
}
