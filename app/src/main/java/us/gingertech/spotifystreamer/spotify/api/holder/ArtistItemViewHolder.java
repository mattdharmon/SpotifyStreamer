package us.gingertech.spotifystreamer.spotify.api.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.gingertech.spotifystreamer.R;

/**
 * Created by Matthew Harmon on 6/13/15.
 */
public class ArtistItemViewHolder {
    @InjectView(R.id.list_item_artist_imageview)
    public ImageView image;

    @InjectView(R.id.list_item_artist_textview)
    public TextView text;

    public ArtistItemViewHolder(View view){
        ButterKnife.inject(this, view);
    }
}
