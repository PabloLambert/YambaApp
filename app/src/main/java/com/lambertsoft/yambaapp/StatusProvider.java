package com.lambertsoft.yambaapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StatusProvider extends ContentProvider {
    private static final String TAG = StatusProvider.class.getName();
    private DBHelper dbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);
        sUriMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM);
    }

    public StatusProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;

        switch (sUriMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                // so we count deleted rows
                where = (selection == null) ? "1" : selection;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(StatusContract.TABLE, where, selectionArgs);

        if(ret>0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "deleted records: " + ret);
        return ret;


    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_DIR);
                return StatusContract.STATUS_TYPE_DIR;
            case StatusContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_ITEM);
                return StatusContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal Uri" + uri);

        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri ret = null;

        // Assert correct uri // 1
        if (sUriMatcher.match(uri) != StatusContract.STATUS_DIR) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase(); // 2
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE); // 3

        // Was insert successful?
        if (rowId != -1) { // 4
            long id = values.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id); // 5
            Log.d(TAG, "inserted uri: " + ret);

            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null); // 6
        }

        return ret;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        Log.d(TAG, "onCreated");
          return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {


        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();   // 1
        qb.setTables( StatusContract.TABLE );   // 2

        switch (sUriMatcher.match(uri)) { // 3
            case StatusContract.STATUS_DIR:
                break;
            case StatusContract.STATUS_ITEM:
                qb.appendWhere(StatusContract.Column.ID + "="
                        + uri.getLastPathSegment()); // 4
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder))
                ? StatusContract.DEFAULT_SORT
                : sortOrder; // 5

        SQLiteDatabase db = dbHelper.getReadableDatabase(); // 6
        Cursor cursor = qb.query(db, projection, selection, selectionArgs,
                null, null, orderBy); // 7

        // register for uri changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // 8

        Log.d(TAG, "queried records: "+cursor.getCount());
        return cursor; // 9
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        String where;

        switch (sUriMatcher.match(uri)) { // 1
            case StatusContract.STATUS_DIR:
                // so we count updated rows
                where = selection;  // 2
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )"); // 3
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
                // 4
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(StatusContract.TABLE, values,
                where, selectionArgs); // 5

        if(ret>0) { // 6
            // Notify that data for this URI has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "updated records: " + ret);
        return ret;


    }
}
