package us.gingertech.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.domain.ArtistsDomain;
import us.gingertech.spotifystreamer.domain.TracksDomain;
import us.gingertech.spotifystreamer.repository.ArtistsRepository;
import us.gingertech.spotifystreamer.repository.StateRepository;
import us.gingertech.spotifystreamer.repository.TracksRepository;
import us.gingertech.spotifystreamer.spotify.api.MediaPlayerFragmentListener;
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
    protected TracksRepository tracksRepository;
    protected ArtistsRepository artistsRepository;
    protected ArtistsDomain artistsDomain;
    protected TracksDomain tracksDomain;
    protected StateRepository stateRepository;
    public MediaPlayerFragment mediaPlayerFragment;
    public MenuItem nowPlayMenuItem;
    public String selectedArtistId = null;
    public String selectedArtistsName = null;

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
        artistsRepository = new ArtistsRepository(getActivity());
        artistsDomain = new ArtistsDomain(getActivity());
        stateRepository = new StateRepository(getActivity());
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

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("selectedArtistId")) {
            selectedArtistId = intent.getStringExtra("selectedArtistId");
        }
        if (intent.hasExtra("selectedArtistsName")) {
            selectedArtistsName = intent.getStringExtra("selectedArtistsName");
        }

        build();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDefaultActionBar();
        if (!lvTracks.isEnabled()) {
            lvTracks.setEnabled(true);
        }
        if (playerService.isPrepared) {
            lvTracks.setEnabled(true);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        nowPlayMenuItem = menu.add("Now Playing");
        nowPlayMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                renderMediaPlayer();
                return true;
            }
        });
    }

    /**
     * Needed to handle the information received from the spotify async task.
     */
    @Override
    public void onTaskCompleted(ArrayList<Track> results) {
        tracksDomain.saveTracks(results);
        build();
    }

    @Override
    public void onTaskFailure(String error) {
        Toast toast = Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
        tracksDomain.saveTopTracks(tracksRepository.getTracks());
        artistsDomain.saveCurrentPlayingArtist(artistsRepository.getSelectedArtistId());
        tracksDomain.saveCurrentTrackPosition(position);
        lvTracks.setEnabled(false); // prevents click through of the dialog fragment.
        renderMediaPlayer();
    }

    private void renderMediaPlayer() {
        if (mediaPlayerFragment != null) {
            mediaPlayerFragment.dismiss();
        }
        setActionBar("Now Playing", artistsRepository.getSelectedArtistsName());
        mediaPlayerFragment = new MediaPlayerFragment();
        mediaPlayerFragment.setMediaPlayerFragmentListener(new MediaPlayerFragmentListener() {
            @Override
            public void onDismiss() {
                setDefaultActionBar();
                lvTracks.setEnabled(true); // When the dialog fragment is dismissed (et.al.) re-enable the clicking.
                if (playerService.isPrepared) {
                    setHasOptionsMenu(true);
                }
            }
        });
        if (stateRepository.isLargeScreen()) {
            mediaPlayerFragment.show(getActivity().getSupportFragmentManager(), "Dialog");
            return;
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.top_tracks_container, mediaPlayerFragment)
                .addToBackStack(null)
                .commit();
    }

    public void build() {
        // Prevent an error to make sure this activity is still active, since an
        // artist selection may recreate a fragment.
        if (getActivity() == null) {
            Logger.e("No activity is found.");
            return;
        }
        if (needsToFetchTracksFromSpotify()) {
            artistsDomain.saveArtistsId(selectedArtistId);
            artistsDomain.saveSelectedArtistsName(selectedArtistsName);
            getTopTracks();
            return;
        }
        lvTracks.setOnItemClickListener(this);
        lvTracks.setAdapter(new TracksAdapter(getActivity(), tracksRepository.getTracks()));
    }

    public void setDefaultActionBar() {
        setActionBar("Top 10 Tracks", artistsRepository.getSelectedArtistsName());
    }

    public void setActionBar(@NonNull String title, @NonNull String subtitle) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity == null) {
            return;
        }
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(title);
        actionBar.setSubtitle(subtitle);
    }

    private void getTopTracks() {
        new FetchArtistsTopTracksAsyncTask(this).execute(artistsRepository.getSelectedArtistId());
    }

    private boolean needsToFetchTracksFromSpotify() {
        boolean results = false;
        if (artistsRepository.getSelectedArtistId() == null) {
            results = true;
        }

        if (selectedArtistId != null
                && !selectedArtistId.equals(artistsRepository.getSelectedArtistId())
        ) {
            results = true;
        }
        return results;
    }
}
