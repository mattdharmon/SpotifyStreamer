package us.gingertech.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;
import us.gingertech.spotifystreamer.spotify.api.adapter.TracksAdapter;
import us.gingertech.spotifystreamer.spotify.api.task.FetchArtistsTopTracksAsyncTask;
import us.gingertech.spotifystreamer.spotify.api.task.IOnTaskCompleted;


/**
 * This fragment will hold all a list of the artist's top tracks.
 */
public class TrackListFragment extends Fragment implements
        IOnTaskCompleted<ArrayList<Track>>
{
    private String artistId;
    private ArrayList<Track> tracks;

    @InjectView(R.id.list_view_tracks)
    protected ListView lvTracks;

    public TrackListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        // Get the fragment view.
        View rootView = inflater.inflate(R.layout.fragment_track_list, null);
        ButterKnife.inject(this, rootView);

        // If the instances is not saved, get the intent.
        if (savedInstanceState == null) {
            // Get the intent to get the tracks for the artist.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_UID)) {
                artistId = intent.getStringExtra(Intent.EXTRA_UID);
            }
            getTopTracks();
        }

        if (savedInstanceState != null) {
            build();
        }
        return rootView;
    }

    /**
     * Needed to handle the information received from the spotify async task.
     *
     * @param results
     */
    @Override
    public void onTaskCompleted(ArrayList<Track> results) {
        tracks = results;
        build();
    }

    @Override
    public void onTaskFailure(String error) {
        Toast toast = Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void build() {
        lvTracks.setAdapter(new TracksAdapter(getActivity(), tracks));
    }

    private void getTopTracks() {
        new FetchArtistsTopTracksAsyncTask(this).execute(artistId);
    }
}
