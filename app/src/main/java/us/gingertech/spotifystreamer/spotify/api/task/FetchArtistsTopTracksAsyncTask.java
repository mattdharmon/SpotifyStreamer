package us.gingertech.spotifystreamer.spotify.api.task;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by Matthew Harmon on 6/13/15.
 */
public class FetchArtistsTopTracksAsyncTask extends AsyncTask<String, Void, ArrayList<Track>> {
    private SpotifyService service;
    private IOnTaskCompleted<ArrayList<Track>> listener;


    public FetchArtistsTopTracksAsyncTask(IOnTaskCompleted<ArrayList<Track>> listener) {
        this.listener = listener;
        this.service = new SpotifyApi().getService();
    }

    @Override
    protected ArrayList<Track> doInBackground(String... params) {
        try {
            if (params.length == 0) {
                throw new Exception("Params are not to be empty.");
            }

            // Build the country query params
            Map<String,Object> query = new HashMap<>();
            query.put("country", "US");

            // Execute the api service.
            Tracks tracks = service.getArtistTopTrack(params[0], query);

            return (ArrayList<Track>) tracks.tracks;
        } catch (RetrofitError e) {
            Log.d("Retrofit Error:", e.toString());
        } catch (Exception e) {
            Log.d("Missing Params: ", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Track> tracks) {
        // If an error occurred, return it to the user.
        if (tracks == null) {
            listener.onTaskFailure("Error.");
            return;
        }

        // Check to see if any tracks are returned.
        if (tracks.isEmpty()) {
            listener.onTaskFailure("No artist was found.");
            return;
        }

        // Trigger the listener with the tracks.
        listener.onTaskCompleted(tracks);
    }
}
