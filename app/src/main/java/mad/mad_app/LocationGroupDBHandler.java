package mad.mad_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tim on 25/09/2016.
 * This class is a database wrapper. The managers use this class to interact with the db.
 */
public class LocationGroupDBHandler extends SQLiteOpenHelper {
    private static final String TAG = LocationGroupDBHandler.class.getSimpleName();

    public static final String TBL_NAME = "locations";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_LAT = "lat";
    public static final String COL_LON = "lon";

    private static final String DB_NAME = "locations.db";
    private static final int DB_VERSION = 0;

    private static final String DB_CREATE =
            "create table " + TBL_NAME + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_NAME + " text not null, " +
                    COL_LAT + " real not null, " +
                    COL_LON + " real not null);";


    public LocationGroupDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing for right now
    }
}

