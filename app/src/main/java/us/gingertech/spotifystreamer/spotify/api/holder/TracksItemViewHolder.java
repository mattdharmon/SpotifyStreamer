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
public class TracksItemViewHolder {
    @InjectView(R.id.list_item_album_imageview)
    public ImageView ivAlbum;

    @InjectView(R.id.list_item_album_textview)
    public TextView tvAlbum;

    @InjectView(R.id.list_item_track_textview)
    public TextView tvTrack;

    public TracksItemViewHolder(View view) {
        ButterKnife.inject(this, view);
    }
}
