package us.gingertech.spotifystreamer.domain;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

/**
 * Created by Matthew Harmon on 7/22/15.
 */
public class StateDomain extends Domain{
    private static final String DATABASE_NAME = "state";

    public StateDomain(Context context) {
        super(context);
    }


    public void isLargeScreen() {
        isLargeScreen(true);
    }

    public void isLargeScreen(boolean isLargeScreen) {
        try {
            openDatabase(DATABASE_NAME);
            snappyDB.putBoolean("isLargeScreen", isLargeScreen);
            closeDatabase();
        } catch (SnappydbException e) {
            Logger.e(e, "Failure to save screen size.");
            e.printStackTrace();
        }
    }
}
