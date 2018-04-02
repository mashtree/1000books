package uxt6.psu.com.a1000books.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import uxt6.psu.com.a1000books.R;

/**
 * Created by aisyahumar on 2/22/2018.
 */

public class UserPreferences {

    private SharedPreferences prefs;
    private Context context;
    private SharedPreferences.Editor editor;

    public UserPreferences(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        this.context = context;
    }

    public UserPreferences setReaderServerId(int id){
        editor.putInt(context.getString(R.string.your_id),id);
        return this;
    }

    public UserPreferences setReaderName(String reader){
        editor.putString(context.getString(R.string.your_name),reader);
        return this;
    }

    public UserPreferences setPassword(String password){
        editor.putString(context.getString(R.string.your_password),password);
        return this;
    }

    public UserPreferences setAddress(String address){
        editor.putString(context.getString(R.string.your_city),address);
        return this;
    }

    public UserPreferences setPhoneNumber(String phone){
        editor.putString(context.getString(R.string.your_phone),phone);
        return this;
    }

    public UserPreferences setToken(String token){
        editor.putString(context.getString(R.string.your_regnumber), token);
        return this;
    }

    public UserPreferences setPicture(String url){
        editor.putString(context.getString(R.string.your_picture), url);
        return this;
    }

    public void doCommit(){
        editor.commit();
    }

    public int getReaderServerId(){
        return prefs.getInt(context.getString(R.string.your_id),0);
    }

    public String getReaderName(){
        return prefs.getString(context.getString(R.string.your_name),"");
    }

    public String getPassword(){
        return prefs.getString(context.getString(R.string.your_password),"");
    }

    public String getReaderAddress(){
        return prefs.getString(context.getString(R.string.your_city),"");
    }

    public String getReaderPhone(){
        return prefs.getString(context.getString(R.string.your_phone),"");
    }

    public String getToken(){
        return prefs.getString(context.getString(R.string.your_regnumber),"");
    }

    public String getPicture(){
        return prefs.getString(context.getString(R.string.your_picture),"");
    }
}
