package us.gingertech.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import kaaes.spotify.webapi.android.models.Artist;
import us.gingertech.spotifystreamer.domain.ArtistsDomain;
import us.gingertech.spotifystreamer.domain.StateDomain;
import us.gingertech.spotifystreamer.repository.ArtistsRepository;
import us.gingertech.spotifystreamer.repository.StateRepository;
import us.gingertech.spotifystreamer.spotify.api.adapter.ArtistsAdapter;
import us.gingertech.spotifystreamer.spotify.api.task.FetchArtistsAsyncTask;
import us.gingertech.spotifystreamer.spotify.api.task.IOnTaskCompleted;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements
        IOnTaskCompleted<ArrayList<Artist>>,
        AdapterView.OnItemClickListener
{
    private ArtistsAdapter artistsAdapter;
    private View currentSelection;
    private ArtistsDomain artistsDomain;
    private ArtistsRepository artistsRepository;
    private StateDomain stateDomain;
    private StateRepository stateRepository;
    private TrackListFragment trackListFragment;

    @Bind(R.id.list_view_search)
    protected ListView lvArtists;

    @Bind(R.id.edittext_artist_search)
    protected EditText etArtiestSearch;

    @Nullable
    @Bind(R.id.top_tracks_container)
    protected FrameLayout ctTopTracks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistsDomain = new ArtistsDomain(getActivity());
        artistsRepository = new ArtistsRepository(getActivity());
        stateDomain = new StateDomain(getActivity());
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
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.bind(this, rootView);

        // Attach the listeners
        lvArtists.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            // Generate a basic list of spotify  "A list" artists. Ha Ha, I made a punny.
            if (artistsRepository.getArtistsSearchQuery() == null) {
                artistsDomain.saveArtistsSearchQuery("A");
            }

            if (artistsRepository.getArtistsSearchResults() != null) {
                artistsAdapter = new ArtistsAdapter(getActivity(), artistsRepository.getArtistsSearchResults());
                lvArtists.setAdapter(artistsAdapter);
            } else {
                new FetchArtistsAsyncTask(this).execute(artistsRepository.getArtistsSearchQuery());
            }
            etArtiestSearch.setText(artistsRepository.getArtistsSearchQuery());
            stateDomain.isLargeScreen(ctTopTracks != null);
        }
        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        // Have it build the adaptor
        etArtiestSearch.setText(artistsRepository.getArtistsSearchQuery());
        lvArtists.setAdapter(artistsAdapter);
    }

    @Override
    public void onTaskCompleted(ArrayList<Artist> artists) {
        // Bind the adapters.
        artistsDomain.saveArtistsSearchResults(artists);
        artistsAdapter = new ArtistsAdapter(getActivity(), artistsRepository.getArtistsSearchResults());
        lvArtists.setAdapter(artistsAdapter);
    }

    @Override
    public void onTaskFailure(String error) {
        Toast toast = Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
        // Save an http call by comparing the old artistId with the new.
        String artistsName = artistsRepository.getArtistsSearchResults().get(position).name;
        if (currentSelection != view) {
            // Update the selection colors.
            setCurrentSelection(view);
        }
        // Render the large view layout.
        if (stateRepository.isLargeScreen()) {
            renderLargeViewTrackList((String)view.getTag(), artistsName);
            return;
        }

        startTrackListActivity((String)view.getTag(), artistsName);
    }

    @OnTextChanged(R.id.edittext_artist_search)
    public void onTextChanged(CharSequence text) {
        // If there is no difference, in the texts,
        // save an HTTP request.
        if (artistsRepository.getArtistsSearchQuery().equals(text.toString())) {
            return;
        }

        // Set the query variable and perform a search.
        artistsDomain.saveArtistsSearchQuery(text.toString());
        new FetchArtistsAsyncTask(this).execute(artistsRepository.getArtistsSearchQuery());
    }

    public void setCurrentSelection(View view) {
        // Change the current selection to the same background color.
        if (currentSelection != null) {
            int color = getResources().getColor(R.color.background_material_light);
            currentSelection.setBackgroundColor(color);
        }

        // Update the current selection color.
        int color = getResources().getColor(R.color.list_item_selected);
        view.setBackgroundColor(color);
        currentSelection = view;
    }

    private void renderLargeViewTrackList(String artistsId, String artistsName) {
        if (trackListFragment == null) {
            trackListFragment = new TrackListFragment();
            trackListFragment.selectedArtistId = artistsId;
            trackListFragment.selectedArtistsName = artistsName;
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.top_tracks_container, trackListFragment)
                    .commit();
            return;
        }
        trackListFragment.selectedArtistId = artistsId;
        trackListFragment.selectedArtistsName = artistsName;
        trackListFragment.build();
    }

    private void startTrackListActivity(String artistsId, String artistsName) {
        // Save the top ten tracks to the snappy.
        Intent intent = new Intent(getActivity(), TrackList.class);
        intent.putExtra("selectedArtistId", artistsId);
        intent.putExtra("selectedArtistsName", artistsName);
        startActivity(intent);
    }
}
