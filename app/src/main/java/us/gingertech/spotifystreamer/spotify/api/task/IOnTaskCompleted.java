package us.gingertech.spotifystreamer.spotify.api.task;

import retrofit.RetrofitError;

/**
 * Created by Matthew Harmon on 6/13/15.
 */
public interface IOnTaskCompleted<T> {
    void onTaskCompleted(T results);

    void onTaskFailure(String error);
}
