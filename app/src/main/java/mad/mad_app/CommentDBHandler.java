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

    public static final String TBL_NAME = "commentsList";
    public static final String COL_ID = "_id";
    public static final String COL_PARENT_ID = "parent_id";
    public static final String COL_DATETIME = "datetime";
    public static final String COL_COMMENT = "comment";
    public static final String COL_IMAGE = "image_path";
    public static final String COL_PARENT_TYPE_CODE = "parent_type_code";

    private static final String DB_NAME = "commentsList.db";
    private static final int DB_VERSION = 6;

    private static final String DB_CREATE =
            "create table " + TBL_NAME + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_PARENT_ID + " integer not null, " +
                    COL_DATETIME + " integer, " +
                    COL_COMMENT + " text, " +
                    COL_IMAGE + " text, " +
                    COL_PARENT_TYPE_CODE + " text);";


    public CommentDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TBL_NAME);
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
        onCreate(db);
    }
}
