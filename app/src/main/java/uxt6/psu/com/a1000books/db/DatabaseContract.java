package uxt6.psu.com.a1000books.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class DatabaseContract {

    public static String TABLE_BOOK = "books";
    public static String TABLE_READER = "reader";

    public static final class BookColumns implements BaseColumns {
        //Book title
        public static String TITLE = "title";
        //Server id
        public static String SERVER_ID= "server_id";
        //Book review
        public static String REVIEW = "review";
        //Book author
        public static String AUTHOR = "author";
        //Book publisher
        public static String PUBLISHER = "publisher";
        //Book get_from
        public static String GET_FROM = "get_from";
        //Book rating
        public static String RATING = "rating";
        //Book cover name image
        public static String COVER = "cover";
        //Book review date -- upload
        public static String DATE = "created_at";
        //Book G+ shared
        public static String GPLUS = "gplus";
    }

    public static final class ReaderColumns implements BaseColumns{
        //reader name
        public static String NAME = "name";
        //reader city address
        public static String ADDRESS = "address";
        //reader phone number
        public static String PHONE = "phone";
        //reader token
        public static String TOKEN = "token";
    }

    public static final String AUTHORITY = "uxt6.psu.com.a1000books";

    public static final Uri BOOK_CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(AUTHORITY)
            .appendPath(TABLE_BOOK)
            .build();

    public static final Uri READER_CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(AUTHORITY)
            .appendPath(TABLE_READER)
            .build();

    public static String getColumnString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static long getColumnLong(Cursor cursor, String columnName){
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    public static double getColumnDouble(Cursor cursor, String columnName){
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

}