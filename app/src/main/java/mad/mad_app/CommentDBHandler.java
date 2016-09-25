package mad.mad_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tim on 25/09/2016.
 * This class is a database wrapper. The managers use this class to interact with the db.
 */
public class CommentDBHandler extends SQLiteOpenHelper{
    private static final String TAG = CommentDBHandler.class.getSimpleName();

    public static final String TBL_NAME = "comments";
    public static final String COL_ID = "_id";
    public static final String COL_PARENT_ID = "parent_id";
    public static final String COL_COMMENT = "comment";
    public static final String COL_IMAGE = "image";

    private static final String DB_NAME = "comments.db";
    private static final int DB_VERSION = 0;

    private static final String DB_CREATE =
            "create table " + TBL_NAME + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_PARENT_ID + " integer, " +
                    COL_COMMENT + " text, " +
                    COL_IMAGE + " BLOB, " +
                    "foreign key(" + COL_PARENT_ID + ") references " + SpeedTestDBHandler.TBL_NAME + "(" + SpeedTestDBHandler.COL_ID + "));";


    public CommentDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
    }
}
