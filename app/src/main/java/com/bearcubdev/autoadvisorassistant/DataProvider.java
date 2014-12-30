package com.bearcubdev.autoadvisorassistant;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Home on 11/4/2014.
 */
public class DataProvider extends ContentProvider{

    //fields for database
    private static final String TAG = DataProvider.class.getSimpleName();
    public static final String DB_NAME = "saa.db";
    public static final int DB_VERSION = 1;
    //tables
    public static final String TABLE = "openTickets";

    //columns
    public static final String C_ID = "_id"; // special for id
    public static final String C_RO_NUMBER = "roNumber";
    public static final String C_NAME = "name";
    public static final String C_PHONE_NUMBER = "phoneNumber";
    public static final String C_STATUS = "status";
    public static final String C_CREATED_AT = "createdAt";
    public static final String C_DISMISSED_AT = "dismissedAt";
    public static final String C_COLOR = "color";
    public static final String C_LAST_STATUS = "lastStatus";

    //fields for content provider
    public static final String PROVIDER_NAME = "com.bearcubdev.autoadvisorassistant.DataProvider";
    public static final String AUTHORITY = "content://" + PROVIDER_NAME + "/" + TABLE;
    public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);



    //uri matcher
    public static final int CONTENT = 1;
    public static final int CONTENT_ID = 2;



    //setup uri matcher
    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI(PROVIDER_NAME, TABLE, CONTENT);
        sURIMatcher.addURI(PROVIDER_NAME, TABLE + "/#", CONTENT_ID);
    }

    public DbHelper dbHelper;
    public SQLiteDatabase db;
    Context context;


    @Override
    public boolean onCreate() {
        context = getContext();
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

        if(db==null)
            return false;
        else
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE);
        Cursor cursor;

        cursor = queryBuilder.query(db,projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "uri = " + uri);

        long id = db.insert(TABLE, null, values);

        Log.d(TAG, "id = " +id);

        //if row added successfully
        if(id>0){
            Uri newUri = Uri.withAppendedPath(uri, Long.toString(id));
            getContext().getContentResolver().notifyChange(newUri,null);
            Log.d(TAG, "inserted the row! woot woot!");
            return newUri;
        }
        throw new SQLException("Failed to add a new record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count = 0;

        //get the id
        String id = uri.getLastPathSegment();
        count = db.delete(TABLE, C_ID + "=" + id, null );

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int count;

        String id = uri.getLastPathSegment();
        count = db.update(TABLE, values, C_ID + "=" + id, null);

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }


    public class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String statusTable = String.format("create table %s (%s integer, %s integer, %s text, %s text, %s text, %s text, %s text, %s text, %s integer primary key)",
                    TABLE, C_CREATED_AT, C_DISMISSED_AT, C_COLOR, C_STATUS, C_LAST_STATUS, C_RO_NUMBER, C_NAME, C_PHONE_NUMBER, C_ID);

            Log.d(TAG, "onCreate sql: " + statusTable);

            db.execSQL(statusTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists" + TABLE);
            this.onCreate(db);
        }
    }
}
