package us.gingertech.spotifystreamer.spotify.api.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Matthew Harmon on 6/13/15.
 */
public class FetchArtistsAsyncTask extends AsyncTask<String, Void, ArrayList<Artist>> {
    private SpotifyApi api;
    private SpotifyService service;
    private IOnTaskCompleted listener;

    public FetchArtistsAsyncTask(IOnTaskCompleted listener) {
        this.listener = listener;
        api = new SpotifyApi();
        service = api.getService();
    }

    @Override
    protected ArrayList<Artist> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        ArtistsPager artistsPager = service.searchArtists(params[0]);
        return (ArrayList<Artist>) artistsPager.artists.items;
    }

    @Override
    protected void onPostExecute(ArrayList<Artist> artists) {
        if (artists.isEmpty()) {
            listener.onTaskFailure("No results found.");
            return;
        }
        listener.onTaskCompleted(artists);
    }
}
