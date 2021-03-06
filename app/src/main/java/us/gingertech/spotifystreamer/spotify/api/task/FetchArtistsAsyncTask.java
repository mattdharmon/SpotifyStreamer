package us.gingertech.spotifystreamer.spotify.api.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Fetch an ArrayList of Artists from spotify, async style.
 * Created by Matthew Harmon on 6/13/15.
 */
public class FetchArtistsAsyncTask extends AsyncTask<String, Void, ArrayList<Artist>> {
    private SpotifyService service;
    private IOnTaskCompleted<ArrayList<Artist>> listener;

    public FetchArtistsAsyncTask(IOnTaskCompleted<ArrayList<Artist>> listener) {
        this.listener = listener;
        service = new SpotifyApi().getService();
    }

    @Override
    protected ArrayList<Artist> doInBackground(@NonNull String[] params) {
        try {
            if (params.length == 0) {
                throw new Exception("Params are not to be empty.");
            }

            // Execute the service.
            ArtistsPager artistsPager = service.searchArtists(params[0]);

            return (ArrayList<Artist>) artistsPager.artists.items;
        } catch (RetrofitError e) {
            Logger.e(e, Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            Logger.e(e, "Missing Params");
        }
        return null;
    }

    @Override
    protected void onPostExecute(@Nullable ArrayList<Artist> artists) {
        // If an error occurred, return it to the user.
        if (artists == null) {
            listener.onTaskFailure("Error.");
            return;
        }

        // Check to see if any artists are returned.
        if (artists.isEmpty()) {
            listener.onTaskFailure("No results found.");
            return;
        }

        // Return the list of artists to the listener.
        listener.onTaskCompleted(artists);
    }
}
