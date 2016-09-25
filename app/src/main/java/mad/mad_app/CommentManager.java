package mad.mad_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 25/09/2016.
 * This class interacts with the DB. All interaction should go through this class
 */
public class CommentManager {
    private static final String TAG = CommentManager.class.getSimpleName();

    private static final String[] allColumns = {
            CommentDBHandler.COL_ID, CommentDBHandler.COL_PARENT_ID,
            CommentDBHandler.COL_COMMENT, CommentDBHandler.COL_IMAGE
    };

    private SQLiteDatabase db;
    private CommentDBHandler handler;

    public CommentManager(Context context) {
        this.handler = new CommentDBHandler(context);
    }

    public void open() throws SQLException {
        db = handler.getWritableDatabase();
    }

    public void close() {
        handler.close();
    }

    public List<Comment> getAll() {
        ArrayList<Comment> result = new ArrayList<>();

        Cursor cursor = db.query(CommentDBHandler.TBL_NAME, allColumns,
                                    null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            Comment comment = fromCursor(cursor);
            result.add(comment);
            cursor.moveToNext();
        }

        cursor.close();
        return result;
    }

    public Long insert(Comment comment) {
        if(comment == null) return null;

        Long result = null;

        ContentValues values = new ContentValues();
        values.put(CommentDBHandler.COL_PARENT_ID, comment.getParentId());
        values.put(CommentDBHandler.COL_COMMENT, comment.getComment());
        values.put(CommentDBHandler.COL_IMAGE, comment.getImageData());

        long insertId = db.insert(CommentDBHandler.TBL_NAME, null, values);
        if(insertId != -1) {
            result = insertId;
        }

        return result;
    }

    public void update(Comment comment) {
        if(comment != null && comment.getId() != null && comment.getParentId() != null) {
            ContentValues values = new ContentValues();
            values.put(CommentDBHandler.COL_COMMENT, comment.getComment());
            values.put(CommentDBHandler.COL_IMAGE, comment.getImageData());

            String where = CommentDBHandler.COL_ID + " = " + comment.getId() +
                    " AND " + CommentDBHandler.COL_PARENT_ID + " = " + comment.getParentId();

            db.update(CommentDBHandler.TBL_NAME, values, where, null);
        }
    }

    public void delete(Comment comment) {
        if(comment != null && comment.getId() != null) {
            db.delete(CommentDBHandler.TBL_NAME, CommentDBHandler.COL_ID + " = " + comment.getId(), null);
        }
    }

    private Comment fromCursor(Cursor cursor) {
        if(cursor == null) return null;

        Comment result = new Comment();
        result.setId(cursor.getLong(0));
        result.setParentId(cursor.getLong(1));
        result.setComment(cursor.getString(2));
        result.setImageData(cursor.getBlob(3));

        return result;
    }
}
