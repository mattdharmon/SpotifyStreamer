package us.gingertech.spotifystreamer.spotify.api.task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

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
        // Build the country query params
        Map<String,Object> query = new HashMap<>();
        query.put("country", "US");

        Tracks tracks = service.getArtistTopTrack(params[0], query);
        return (ArrayList<Track>) tracks.tracks;
    }

    @Override
    protected void onPostExecute(ArrayList<Track> tracks) {
        if (tracks.isEmpty()) {
            listener.onTaskFailure("No artist was found.");
            return;
        }
        listener.onTaskCompleted(tracks);
    }
}
