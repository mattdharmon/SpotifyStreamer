package us.gingertech.spotifystreamer;

/**
 * Created by Matthew Harmon on 7/21/15.
 */
public interface MediaServiceListener {
    void onPrepared();
    void onCompletion();
    void onSeekComplete();
}
