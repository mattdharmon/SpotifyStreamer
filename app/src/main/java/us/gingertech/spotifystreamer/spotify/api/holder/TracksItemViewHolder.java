package us.gingertech.spotifystreamer.spotify.api.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import us.gingertech.spotifystreamer.R;

/**
 * Created by Matthew Harmon on 6/13/15.
 */
public class TracksItemViewHolder {
    @Bind(R.id.list_item_album_imageview)
    public ImageView ivAlbum;

    @Bind(R.id.list_item_album_textview)
    public TextView tvAlbum;

    @Bind(R.id.list_item_track_textview)
    public TextView tvTrack;

    public TracksItemViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
