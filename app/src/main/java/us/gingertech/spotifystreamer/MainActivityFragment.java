package us.gingertech.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import kaaes.spotify.webapi.android.models.Artist;
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
    private String query;
    private ArrayList<Artist> artists;

    @InjectView(R.id.list_view_search)
    protected ListView lvArtists;

    @InjectView(R.id.edittext_artist_search)
    protected EditText etArtiestSearch;

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
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.inject(this, rootView);

        // Attach the listeners
        lvArtists.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            // Generate a basic list of spotify  "A list" artists. Ha Ha, I made a punny.
            query = "A";
            searchArtists();
        }

        // Have it build the adaptor
        if (savedInstanceState != null) {
            etArtiestSearch.setText(query);
            lvArtists.setAdapter(artistsAdapter);
        }

        return rootView;
    }

    @Override
    public void onTaskCompleted(ArrayList<Artist> artists) {
        // Bind the adapters.
//        this.artists = artists;
        artistsAdapter = new ArtistsAdapter(getActivity(), artists);
        lvArtists.setAdapter(artistsAdapter);
    }

    @Override
    public void onTaskFailure(String error) {
        Toast toast = Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT);
        toast.show();
    }

    @OnItemSelected(R.id.list_view_search)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Build the top tracks of the selected artist.
        String artistsId = (String) view.getTag();
        Intent intent = new Intent(getActivity(), TrackList.class)
            .putExtra(Intent.EXTRA_UID, artistsId);
        startActivity(intent);
    }

    @OnTextChanged(R.id.edittext_artist_search)
    public void onTextChanged(CharSequence text) {
        // If there is no difference, in the texts,
        // save an HTTP request.
        if (query.equals(text.toString())) {
            return;
        }

        // Set the query variable and perform a search.
        query = text.toString();
        searchArtists();
    }

    private void searchArtists() {
        new FetchArtistsAsyncTask(this).execute(query);
    }
}
