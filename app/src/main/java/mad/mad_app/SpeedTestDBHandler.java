package mad.mad_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tim on 25/09/2016.
 * This class is a database wrapper. The managers use this class to interact with the db.
 */
public class SpeedTestDBHandler extends SQLiteOpenHelper{
    private static final String TAG = SpeedTestDBHandler.class.getSimpleName();

    public static final String TBL_NAME = "tests";
    public static final String COL_ID = "_id";
    public static final String COL_PARENT_ID = "parent_id";
    public static final String COL_DATETIME = "date_time";
    public static final String COL_SPEED_DOWN = "speed_download";
    public static final String COL_SPEED_UP = "speed_upload";
    public static final String COL_CONN_TYPE = "conn_type";
    public static final String COL_CONN_SUBTYPE = "conn_subtype";

    private static final String DB_NAME = "tests.db";
    private static final int DB_VERSION = 2;

    private static final String DB_CREATE =
            "create table " + TBL_NAME + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_PARENT_ID + " integer not null, " +
                    COL_DATETIME + " integer not null, " +
                    COL_SPEED_DOWN + " real not null, " +
                    COL_SPEED_UP + " real not null, " +
                    COL_CONN_TYPE + " text not null, " +
                    COL_CONN_SUBTYPE + " text not null, " +
                    "foreign key(" + COL_PARENT_ID + ") references " + LocationGroupDBHandler.TBL_NAME + "(" + LocationGroupDBHandler.COL_ID + "));";


    public SpeedTestDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TBL_NAME);
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
