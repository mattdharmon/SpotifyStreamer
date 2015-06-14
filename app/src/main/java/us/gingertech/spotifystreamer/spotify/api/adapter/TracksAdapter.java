package us.gingertech.spotifystreamer.spotify.api.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;
import us.gingertech.spotifystreamer.R;
import us.gingertech.spotifystreamer.spotify.api.holder.TracksItemViewHolder;

/**
 * Created by Matthew Harmon on 6/13/15.
 */
public class TracksAdapter extends ArrayAdapter<Track> {

    public TracksAdapter(Context context, ArrayList<Track> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View view,ViewGroup parent) {
        // Get the view Holder
        if (view == null) {
            view = LayoutInflater
                .from(getContext())
                .inflate(R.layout.item_view_track, parent, false);
        }

        // Get the view holder
        TracksItemViewHolder viewHolder = new TracksItemViewHolder(view);

        Track track = getItem(position);

        viewHolder.tvAlbum
                .setText(track.album.name);

        viewHolder.tvTrack
                .setText(track.name);

        if (track.album.images.isEmpty()) {
            return view;
        }
        // Replace the image with a place holder if images array is empty.
        if (track.album.images.isEmpty()) {
            Integer imageUrl = R.drawable.no_picture;
            Picasso.with(getContext())
                    .load(imageUrl)
                    .fit()
                    .into(viewHolder.ivAlbum);
            return view;
        }

        // Get the image of the album.
        String imageUrl = track
                .album
                .images
                .get(0)
                .url;

        Picasso
            .with(getContext())
            .load(imageUrl)
            .fit()
            .into(viewHolder.ivAlbum);

        return view;
    }
}
