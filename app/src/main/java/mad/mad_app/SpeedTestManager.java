package mad.mad_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tim on 25/09/2016.
 * This class interacts with the DB. All interaction should go through this class
 */
public class SpeedTestManager {
    private static final String TAG = SpeedTestManager.class.getSimpleName();

    private static final String[] allColumns = {
            SpeedTestDBHandler.COL_ID, SpeedTestDBHandler.COL_PARENT_ID,
            SpeedTestDBHandler.COL_DATETIME, SpeedTestDBHandler.COL_SPEED,
            SpeedTestDBHandler.COL_CONN_TYPE, SpeedTestDBHandler.COL_CONN_SUBTYPE
    };

    private SQLiteDatabase db;
    private SpeedTestDBHandler handler;

    public SpeedTestManager(Context context) {
        handler = new SpeedTestDBHandler(context);
    }

    public void open() throws SQLException {
        db = handler.getWritableDatabase();
    }

    public void close() {
        handler.close();
    }

    public List<SpeedTest> getAllForParent(Long parentId) {
        if(parentId == null) return null;

        ArrayList<SpeedTest> result = new ArrayList<>();

        Cursor cursor = db.query(SpeedTestDBHandler.TBL_NAME, allColumns,
                SpeedTestDBHandler.COL_PARENT_ID + " = " + parentId, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            SpeedTest test = fromCursor(cursor);
            result.add(test);
            cursor.moveToNext();
        }

        cursor.close();
        return result;
    }

    public List<SpeedTest> getAll() {
        ArrayList<SpeedTest> result = new ArrayList<>();

        Cursor cursor = db.query( SpeedTestDBHandler.TBL_NAME, allColumns,
                                    null, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            SpeedTest test = fromCursor(cursor);
            result.add(test);
            cursor.moveToNext();
        }

        cursor.close();
        return result;
    }

    public Long insert(SpeedTest test) {
        if(test == null) return null;

        Long result = null;

        ContentValues values = new ContentValues();
        values.put(SpeedTestDBHandler.COL_PARENT_ID, test.getParentId());
        Date testTime = test.getDateTime();
        values.put(SpeedTestDBHandler.COL_DATETIME, testTime == null ? null : testTime.getTime());
        values.put(SpeedTestDBHandler.COL_SPEED, test.getSpeedKbps());
        values.put(SpeedTestDBHandler.COL_CONN_TYPE, test.getConnType());
        values.put(SpeedTestDBHandler.COL_CONN_SUBTYPE, test.getConnSubType());

        long insertId = db.insert(SpeedTestDBHandler.TBL_NAME, null, values);
        if(insertId != -1) {
            result = insertId;
        }

        return result;
    }

    public void delete(SpeedTest test) {
        if(test != null && test.getId() != null) {
            db.delete(SpeedTestDBHandler.TBL_NAME, SpeedTestDBHandler.COL_ID + " = " + test.getId(), null);
        }
    }

    private SpeedTest fromCursor(Cursor cursor) {
        if(cursor == null) return null;

        SpeedTest test = new SpeedTest();
        test.setId(cursor.getLong(0));
        test.setParentId(cursor.getLong(1));
        test.setDateTime(cursor.getLong(2));
        test.setSpeedKbps(cursor.getDouble(3));
        test.setConnType(cursor.getString(4));
        test.setConnSubType(cursor.getString(5));

        return test;
    }
}
