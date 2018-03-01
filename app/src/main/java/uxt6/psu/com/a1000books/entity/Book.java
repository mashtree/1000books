package uxt6.psu.com.a1000books.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import uxt6.psu.com.a1000books.db.DatabaseContract;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.*;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class Book implements Parcelable {

    private int id;
    private String title;
    private String review;
    private String author;
    private String publisher;
    private String get_from;
    private int rating;
    private String cover;
    private String created_at;

    public Book(){}

    public Book(Cursor cursor){
        id = DatabaseContract.getColumnInt(cursor, _ID);
        title = DatabaseContract.getColumnString(cursor, TITLE);
        review = DatabaseContract.getColumnString(cursor, REVIEW);;
        author = DatabaseContract.getColumnString(cursor, AUTHOR);;
        publisher = DatabaseContract.getColumnString(cursor, PUBLISHER);;
        get_from = DatabaseContract.getColumnString(cursor, GET_FROM);;
        rating = DatabaseContract.getColumnInt(cursor, RATING);;
        cover = DatabaseContract.getColumnString(cursor, COVER);;
        created_at = DatabaseContract.getColumnString(cursor, DATE);;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();;
        publisher = in.readString();;
        review = in.readString();;
        get_from = in.readString();;
        cover = in.readString();;
        rating = in.readInt();;
        created_at = in.readString();;
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
        parcel.writeString(author);
        parcel.writeString(publisher);
        parcel.writeString(review);
        parcel.writeString(get_from);
        parcel.writeString(cover);
        parcel.writeString(created_at);
        parcel.writeInt(rating);
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
}
