package us.gingertech.spotifystreamer.spotify.api.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import us.gingertech.spotifystreamer.R;
import us.gingertech.spotifystreamer.spotify.api.holder.ArtistItemViewHolder;

/**
 * Created by Matthew Harmon on 6/12/15.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {

    public ArtistsAdapter(Context context, ArrayList<Artist> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the view Holder
        if (view == null) {
            view = LayoutInflater
                .from(getContext())
                .inflate(R.layout.item_view_artist, parent, false);
        }

        ArtistItemViewHolder viewHolder = new ArtistItemViewHolder(view);

        // Grab the current artist in the list.
        Artist artist = getItem(position);

        // Set the tag with the id, so I can use it for queries.
        view.setTag(artist.id);

        // Bind the values to the view items.
        viewHolder.text
            .setText(artist.name);

        if (artist.images.isEmpty()) {
            return view;
        }

        // Grab the image url.
        String imageUrl = artist
            .images
            .get(0)
            .url;

        Picasso.with(getContext())
            .load(imageUrl)
            .fit()
            .into(viewHolder.image);

        return view;
    }
}
