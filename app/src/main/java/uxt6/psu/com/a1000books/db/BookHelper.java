package uxt6.psu.com.a1000books.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uxt6.psu.com.a1000books.entity.Book;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.*;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class BookHelper {
    private static String DATABASE_TABLE = DatabaseContract.TABLE_BOOK;
    private Context context;
    private DatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public BookHelper(Context context){
        this.context = context;
    }

    public BookHelper open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public List<Book> query(){
        List<Book> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE,null,null,null,null,null,
                DatabaseContract.BookColumns._ID+" DESC",null);
        cursor.moveToFirst();
        Book book;
        if(cursor.getCount()>0){
            do{
                book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                book.setCover(cursor.getString(cursor.getColumnIndexOrThrow(COVER)));
                book.setReview(cursor.getString(cursor.getColumnIndexOrThrow(REVIEW)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(AUTHOR)));
                book.setPublisher(cursor.getString(cursor.getColumnIndexOrThrow(PUBLISHER)));
                book.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(RATING)));
                book.setCreated_at(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));

                arrayList.add(book);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }

        cursor.close();
        return arrayList;
    }

    public long insert(Book book){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(TITLE, book.getTitle());
        initialValues.put(AUTHOR, book.getAuthor());
        initialValues.put(PUBLISHER, book.getPublisher());
        initialValues.put(REVIEW, book.getReview());
        initialValues.put(COVER, book.getCover());
        initialValues.put(GET_FROM, book.getGet_from());
        initialValues.put(RATING, book.getRating());
        initialValues.put(DATE, book.getCreated_at());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    public long update(Book book){
        ContentValues args = new ContentValues();
        args.put(TITLE, book.getTitle());
        args.put(AUTHOR, book.getAuthor());
        args.put(PUBLISHER, book.getPublisher());
        args.put(REVIEW, book.getReview());
        args.put(COVER, book.getCover());
        args.put(GET_FROM, book.getGet_from());
        args.put(RATING, book.getRating());
        args.put(DATE, book.getCreated_at());

        return database.update(DATABASE_TABLE,args, _ID+
                "= '"+book.getId()+"'",null
        );
    }

    public int delete(int id){
        return database.delete(DatabaseContract.TABLE_BOOK, _ID+"='"+id+"'",null);
    }

    /**
     * tambahan
     */

    public Cursor queryByIdProvider(String id){
        return database.query(DATABASE_TABLE,null
                ,_ID+"=?"
                ,new String[]{id}
                ,null, null, null, null);
    }

    public Cursor queryProvider(){
        return database.query(DATABASE_TABLE,null
                ,null
                ,null
                ,null, null
                ,_ID+" DESC");
    }

    public long insertProvider(ContentValues values){
        long r = database.insert(DATABASE_TABLE, null, values);
        Log.d(BookHelper.class.getSimpleName(), "insertProvider: "+r);
        return r;
    }

    public int updateProvider(String id, ContentValues values){
        return database.update(DATABASE_TABLE, values, _ID+"=?", new String[]{id});
    }

    public int deleteProvider(String id){
        return database.delete(DATABASE_TABLE, _ID+"=?", new String[]{id});
    }
}
