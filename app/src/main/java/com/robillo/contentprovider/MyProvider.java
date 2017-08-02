package com.robillo.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by robinkamboj on 02/08/17.
 */

public class MyProvider extends ContentProvider{

    static final String PROVIDER_NAME = "com.robillo.contentprovider.MyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/mcontacts";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String id = "id";
    static final String name = "name";
    static final int uriCode = 1;

    //we will be storing key (eg. name) and value (eg. Robin Kamboj) pairs
    private static HashMap<String, String> values;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "mcontacts", uriCode);
    }

    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "MYDATABASE";
    static final String TABLE_NAME = "MYCONTACTS";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT NOT NULL);";

    @Override
    public boolean onCreate() {
        MyDbHelper helper = new MyDbHelper(getContext());
        sqLiteDatabase = helper.getWritableDatabase();
        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case uriCode:{
                queryBuilder.setProjectionMap(values);
                break;
            }
            default:{
                throw new IllegalArgumentException("Unknown URI:" + uri);
            }
        }
        Cursor cursor = queryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case uriCode:{
                return "vnd.android.cursor.dir/mcontacts";
            }
            default:{
                throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if(rowId>0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        else {
            Toast.makeText(getContext(), "INVALID", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArguments) {
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)){
            case uriCode:{
                rowsDeleted = sqLiteDatabase.delete(TABLE_NAME, selection, selectionArguments);
                break;
            }
            default:{
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArguments) {
        int rowsUpdated = 0;
        switch (uriMatcher.match(uri)){
            case uriCode:{
                rowsUpdated = sqLiteDatabase.update(TABLE_NAME, contentValues,  selection, selectionArguments);
                break;
            }
            default:{
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private static class MyDbHelper extends SQLiteOpenHelper{

        MyDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
