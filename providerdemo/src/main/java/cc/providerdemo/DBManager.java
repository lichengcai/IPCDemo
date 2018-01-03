package cc.providerdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lichengcai on 2018/1/2.
 */

public class DBManager {
    private static DbOpenHelper mHelper;

    public static DbOpenHelper getHelper(Context context) {
        if (mHelper == null) {
            mHelper = new DbOpenHelper(context);
        }

        return mHelper;
    }


    public static Cursor query(SQLiteDatabase db, String table, String[] columns,
                               String selection, String[] selectionArgs,
                               String groupBy, String having, String orderBy) {
        Cursor cursor = null;
        if (db != null) {
            cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        return cursor;
    }

}
