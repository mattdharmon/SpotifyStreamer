package us.gingertech.spotifystreamer.repository;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Created by Matthew Harmon on 7/21/15.
 */
public class Repository {
    private Context context;
    protected DB snappyDB;

    public Repository(Context context) {
        this.context = context;
    }

    public void openDatabase(String dbName) {
        try {
            closeDatabase();
            snappyDB = DBFactory.open(getContext(), dbName);
        } catch (SnappydbException e) {
            Logger.e(e, "Failure in closing the database.");
            e.printStackTrace();
        }
    }

    public void closeDatabase() {
        try {
            if (snappyDB != null && snappyDB.isOpen()) {
                snappyDB.close();
            }
        } catch (SnappydbException e) {
            Logger.e(e, "Failure in closing the database.");
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
