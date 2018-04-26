package uxt6.psu.com.a1000books.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class  DatabaseHelper extends SQLiteOpenHelper {

    interface OnDBReadyListener{
        void onDBReady(SQLiteDatabase db);
    }

    public static final String DATABASE_NAME = "dbbooks";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_BOOK = String.format(
            "CREATE TABLE %s " + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT, " //should NOT NULL
                    +" %s TEXT)", //should NOT NULL
            DatabaseContract.TABLE_BOOK,
            DatabaseContract.BookColumns._ID,
            DatabaseContract.BookColumns.TITLE,
            DatabaseContract.BookColumns.SERVER_ID,
            DatabaseContract.BookColumns.AUTHOR,
            DatabaseContract.BookColumns.PUBLISHER,
            DatabaseContract.BookColumns.GET_FROM,
            DatabaseContract.BookColumns.REVIEW,
            DatabaseContract.BookColumns.COVER,
            DatabaseContract.BookColumns.RATING,
            DatabaseContract.BookColumns.GPLUS,
            DatabaseContract.BookColumns.DATE
    );

    private static final String SQL_CREATE_TABLE_READER = String.format(
            "CREATE TABLE %s " + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +" %s TEXT NOT NULL, "
                    +" %s TEXT NOT NULL, "
                    +" %s TEXT NOT NULL, "
                    +" %s TEXT NOT NULL)",
            DatabaseContract.TABLE_READER,
            DatabaseContract.ReaderColumns._ID,
            DatabaseContract.ReaderColumns.NAME,
            DatabaseContract.ReaderColumns.ADDRESS,
            DatabaseContract.ReaderColumns.PHONE,
            DatabaseContract.ReaderColumns.TOKEN
    );

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DatabaseContract.TABLE_BOOK);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}