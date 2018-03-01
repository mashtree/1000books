package uxt6.psu.com.a1000books.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uxt6.psu.com.a1000books.entity.Reader;

import static uxt6.psu.com.a1000books.db.DatabaseContract.ReaderColumns.*;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class ReaderHelper {
    private static String DATABASE_TABLE = DatabaseContract.TABLE_BOOK;
    private Context context;
    private DatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public ReaderHelper(Context context){
        this.context = context;
    }

    public ReaderHelper open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public List<Reader> query(){
        List<Reader> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE,null,null,null,null,null,
                DatabaseContract.BookColumns._ID+" DESC",null);
        cursor.moveToFirst();
        Reader reader;
        if(cursor.getCount()>0){
            do{
                reader = new Reader();
                reader.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                reader.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                reader.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS)));
                reader.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(PHONE)));
                reader.setToken(cursor.getString(cursor.getColumnIndexOrThrow(TOKEN)));

                arrayList.add(reader);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }

        cursor.close();
        return arrayList;
    }

    public long insert(Reader reader){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(NAME, reader.getName());
        initialValues.put(ADDRESS, reader.getAddress());
        initialValues.put(PHONE, reader.getPhone());
        initialValues.put(TOKEN, reader.getToken());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    public long update(Reader reader){
        ContentValues args = new ContentValues();
        args.put(NAME, reader.getName());
        args.put(ADDRESS, reader.getAddress());
        args.put(PHONE, reader.getPhone());
        args.put(TOKEN, reader.getToken());

        return database.update(DATABASE_TABLE,args, _ID+
                "= '"+reader.getId()+"'",null
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
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int updateProvider(String id, ContentValues values){
        return database.update(DATABASE_TABLE, values, _ID+"=?", new String[]{id});
    }

    public int deleteProvider(String id){
        return database.delete(DATABASE_TABLE, _ID+"=?", new String[]{id});
    }
}
