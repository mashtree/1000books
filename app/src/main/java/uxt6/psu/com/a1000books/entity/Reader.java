package uxt6.psu.com.a1000books.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import uxt6.psu.com.a1000books.db.DatabaseContract;

import static uxt6.psu.com.a1000books.db.DatabaseContract.ReaderColumns.*;

/**
 * Created by aisyahumar on 2/21/2018.
 */

public class Reader implements Parcelable {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String token;

    public Reader(){}

    public Reader(Cursor cursor){
        setId(DatabaseContract.getColumnInt(cursor, _ID));
        setName(DatabaseContract.getColumnString(cursor, NAME));
        setAddress(DatabaseContract.getColumnString(cursor, ADDRESS));
        setPhone(DatabaseContract.getColumnString(cursor, PHONE));
        setToken(DatabaseContract.getColumnString(cursor, TOKEN));
    }

    protected Reader(Parcel in) {
        setId(in.readInt());
        setName(in.readString());
        setAddress(in.readString());
        setPhone(in.readString());
        setToken(in.readString());
    }

    public static final Creator<Reader> CREATOR = new Creator<Reader>() {
        @Override
        public Reader createFromParcel(Parcel in) {
            return new Reader(in);
        }

        @Override
        public Reader[] newArray(int size) {
            return new Reader[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeString(getName());
        parcel.writeString(getAddress());
        parcel.writeString(getPhone());
        parcel.writeString(getToken());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
