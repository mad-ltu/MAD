package mad.mad_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 25/09/2016.
 * This class interacts with the DB. All interaction should go through this class
 */
public class LocationGroupManager {
    private static final String TAG = LocationGroupManager.class.getSimpleName();

    private static final String[] allColumns = {
            LocationGroupDBHandler.COL_ID, LocationGroupDBHandler.COL_NAME,
            LocationGroupDBHandler.COL_LAT, LocationGroupDBHandler.COL_LON
    };

    private SQLiteDatabase db;
    private LocationGroupDBHandler handler;

    public LocationGroupManager(Context context) {
        handler = new LocationGroupDBHandler(context);
    }

    public void open() throws SQLException {
        db = handler.getWritableDatabase();
    }

    public void close() {
        handler.close();
    }

    public LocationGroup get(long id) {
        LocationGroup result = null;

        Cursor cursor = db.query(LocationGroupDBHandler.TBL_NAME, allColumns,
                LocationGroupDBHandler.COL_ID + " = " + id,
                null, null, null, null);
        if(cursor.moveToFirst()) {
            result = fromCursor(cursor);
        }

        cursor.close();
        return result;
    }

    public List<LocationGroup> getAll() {
        ArrayList<LocationGroup> result = new ArrayList<>();

        Cursor cursor = db.query( LocationGroupDBHandler.TBL_NAME, allColumns,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            LocationGroup test = fromCursor(cursor);
            result.add(test);
            cursor.moveToNext();
        }

        cursor.close();
        return result;
    }

    public Long insert(LocationGroup locationGroup) {
        if(locationGroup == null) return null;

        Long result = null;

        ContentValues values = new ContentValues();
        values.put(LocationGroupDBHandler.COL_NAME, locationGroup.getName());
        values.put(LocationGroupDBHandler.COL_LAT, locationGroup.getLat());
        values.put(LocationGroupDBHandler.COL_LON, locationGroup.getLon());

        long insertId = db.insert(LocationGroupDBHandler.TBL_NAME, null, values);
        if(insertId != -1) {
            result = insertId;
        }

        return result;
    }

    public void delete(LocationGroup locationGroup) {
        if(locationGroup != null && locationGroup.getId() != null) {
            db.delete(LocationGroupDBHandler.TBL_NAME, LocationGroupDBHandler.COL_ID + " = " + locationGroup.getId(), null);
        } else {
            Log.d(TAG, "Trying to delete a null record!");
        }
    }

    public void clearDB() {
        Log.w(TAG, "DELETING ALL RECORDS FROM LOCATION DB!");

        db.execSQL("delete from " + LocationGroupDBHandler.TBL_NAME);
    }

    private LocationGroup fromCursor(Cursor cursor) {
        if(cursor == null) return null;

        LocationGroup locationGroup = new LocationGroup();
        locationGroup.setId(cursor.getLong(0));
        locationGroup.setName(cursor.getString(1));
        locationGroup.setLat(cursor.getDouble(2));
        locationGroup.setLon(cursor.getDouble(3));


        return locationGroup;
    }
}

