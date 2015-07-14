package us.gingertech.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.spotify.api.adapter.TracksAdapter;
import us.gingertech.spotifystreamer.spotify.api.task.FetchArtistsTopTracksAsyncTask;
import us.gingertech.spotifystreamer.spotify.api.task.IOnTaskCompleted;


/**
 * This fragment will hold all a list of the artist's top tracks.
 */
public class TrackListFragment extends Fragment implements
        IOnTaskCompleted<ArrayList<Track>>,
        AdapterView.OnItemClickListener
{
    protected String artistId;
    protected ArrayList<Track> tracks;

    @Bind(R.id.list_view_tracks)
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
        ButterKnife.bind(this, rootView);

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

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            // Save the top ten tracks to the snappy.
            DB tracksDB = DBFactory.open(getActivity(), "tracks");
            for (int i = 0; i < tracks.size() - 1; i++) {
                tracksDB.put(Integer.toString(i), tracks.get(i));
            }
            tracksDB.close();

            // Start the mediaplayer activity
            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class)
                    .putExtra(Intent.EXTRA_UID, position);
            startActivity(intent);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    private void build() {
        // Prevent an error to make sure this activity is still active, since an
        // artist selection may recreate a fragment.
        if (getActivity() == null) {
            return;
        }

        lvTracks.setOnItemClickListener(this);
        lvTracks.setAdapter(new TracksAdapter(getActivity(), tracks));
    }

    private void getTopTracks() {
        new FetchArtistsTopTracksAsyncTask(this).execute(artistId);
    }
}
