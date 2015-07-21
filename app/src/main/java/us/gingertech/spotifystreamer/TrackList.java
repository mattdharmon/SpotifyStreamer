package us.gingertech.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class TrackList extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.top_tracks_container, new TrackListFragment())
                    .commit();
        }
    }
}
