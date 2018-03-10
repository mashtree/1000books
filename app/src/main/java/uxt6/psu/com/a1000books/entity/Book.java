package uxt6.psu.com.a1000books.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import uxt6.psu.com.a1000books.db.DatabaseContract;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.*;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class Book implements Parcelable {

    private int id;
    private String title;
    private int serverId;
    private String review;
    private String author;
    private String publisher;
    private String get_from;
    private int rating;
    private String cover;
    private String created_at;
    private String reader;

    private final String TAG = "BOOK";

    public Book(){}

    public Book(JSONObject object){
        try{
            id = object.getInt("id");
            title = object.getString("title");
            serverId = object.getInt("id");
            review = object.getString("review");
            author = object.getString("author");
            publisher = object.getString("publisher");
            get_from = object.getString("get_from");
            rating = object.getInt("rating");
            cover = object.getString("cover");
            created_at = object.getString("created_at");
            reader = object.getString("reader_name");
        }catch(Exception e){
            Log.e(TAG, "Error on constructor");
            e.printStackTrace();
        }

    }

    public Book(Cursor cursor){
        id = DatabaseContract.getColumnInt(cursor, _ID);
        title = DatabaseContract.getColumnString(cursor, TITLE);
        serverId = DatabaseContract.getColumnInt(cursor, SERVER_ID);
        review = DatabaseContract.getColumnString(cursor, REVIEW);;
        author = DatabaseContract.getColumnString(cursor, AUTHOR);;
        publisher = DatabaseContract.getColumnString(cursor, PUBLISHER);;
        get_from = DatabaseContract.getColumnString(cursor, GET_FROM);;
        rating = DatabaseContract.getColumnInt(cursor, RATING);;
        cover = DatabaseContract.getColumnString(cursor, COVER);;
        created_at = DatabaseContract.getColumnString(cursor, DATE);;
        reader = "";
    }

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        serverId = in.readInt();
        author = in.readString();;
        publisher = in.readString();;
        review = in.readString();;
        get_from = in.readString();;
        cover = in.readString();;
        rating = in.readInt();;
        created_at = in.readString();;
        reader = "";
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeInt(serverId);
        parcel.writeString(author);
        parcel.writeString(publisher);
        parcel.writeString(review);
        parcel.writeString(get_from);
        parcel.writeString(cover);
        parcel.writeString(created_at);
        parcel.writeInt(rating);
        parcel.writeString(reader);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGet_from() {
        return get_from;
    }

    public void setGet_from(String get_from) {
        this.get_from = get_from;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }
}
