package us.gingertech.spotifystreamer;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.domain.TracksDomain;
import us.gingertech.spotifystreamer.repository.TracksRepository;
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
    protected SpotifyStreamerApplication application;
    protected SpotifyStreamerMediaPlayerService playerService;
    protected ArrayList<Track> tracks;
    protected boolean isLargeView = false;
    protected TracksRepository tracksRepository;
    protected TracksDomain tracksDomain;

    @Bind(R.id.list_view_tracks)
    protected ListView lvTracks;

    public TrackListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (SpotifyStreamerApplication) getActivity().getApplication();
        playerService = application.getPlayerService();
        tracksDomain = new TracksDomain(getActivity());
        tracksRepository = new TracksRepository(getActivity());
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
            getTopTracks();
        }

        if (savedInstanceState != null) {
            build();
        }

        if (playerService.isPrepared) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    renderMediaPlayer();
                }
            };
            new Handler().postDelayed(run, 1000);
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

    public void setIsLargeView(boolean isLargeView) {
        this.isLargeView = isLargeView;
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
        tracksDomain.saveTopTracks(tracks);
        tracksDomain.saveCurrentTrackPosition(position);
        renderMediaPlayer();
    }

    private void renderMediaPlayer() {
        MediaPlayerFragment mediaPlayerFragment = new MediaPlayerFragment();
        if (isLargeView) {
            mediaPlayerFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
        } else {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.top_tracks_container, mediaPlayerFragment)
                    .addToBackStack(null)
                    .commit();
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
        new FetchArtistsTopTracksAsyncTask(this).execute(tracksRepository.getSelectedArtistId());
    }
}
