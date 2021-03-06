package us.gingertech.spotifystreamer.repository;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.SnappydbException;

import us.gingertech.spotifystreamer.R;

/**
 * Created by Matthew Harmon on 7/22/15.
 */
public class StateRepository extends Repository{
    private static final String DATABASE_NAME = "state";

    public StateRepository(Context context) {
        super(context);
    }

    public boolean isLargeScreen() {
        boolean results = false;
        try {
            openDatabase(DATABASE_NAME);
            results = snappyDB.getBoolean(getContext().getString(R.string.db_is_large_screen));
        } catch (SnappydbException e) {
            Logger.e(e, "Failure to retrieve state.");
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return results;
    }
}
