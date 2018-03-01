package uxt6.psu.com.a1000books.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uxt6.psu.com.a1000books.db.BookHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;

/**
 * Created by aisyahumar on 2/22/2018.
 */

public class BookProvider extends ContentProvider {

    private static final int BOOK = 1;
    private static final int BOOK_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //content://com.still.penn.mynotesapp/note
        sUriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.TABLE_BOOK, BOOK);
        //content://com.still.penn.mynotesapp/note/id
        sUriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.TABLE_BOOK+"/#",
                BOOK_ID);
    }

    private BookHelper bookHelper;

    @Override
    public boolean onCreate() {
        bookHelper = new BookHelper(getContext());
        bookHelper.open();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case BOOK:
                cursor = bookHelper.queryProvider();
                break;
            case BOOK_ID:
                cursor = bookHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }

        if(cursor!=null){
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long added;
        switch (sUriMatcher.match(uri)){
            case BOOK:
                added = bookHelper.insertProvider(contentValues);
                break;
            default:
                added = 0;
                break;
        }

        if(added>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return Uri.parse(DatabaseContract.BOOK_CONTENT_URI+"/"+added);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deleted;
        switch (sUriMatcher.match(uri)){
            case BOOK_ID:
                deleted = bookHelper.deleteProvider(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }

        if(deleted>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int updated;
        switch (sUriMatcher.match(uri)){
            case BOOK_ID:
                updated = bookHelper.updateProvider(uri.getLastPathSegment(),contentValues);
                break;
            default:
                updated = 0;
                break;
        }

        if(updated>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }
}

