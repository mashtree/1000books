package uxt6.psu.com.a1000books;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.db.BookHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;
import uxt6.psu.com.a1000books.utility.ImageSaver;

import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.AUTHOR;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.COVER;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.DATE;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.GET_FROM;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.PUBLISHER;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.RATING;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.REVIEW;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.SERVER_ID;
import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.TITLE;


public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv_email) EditText tvEmail;
    @BindView(R.id.tv_password) EditText tvPassword;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.login_progress_bar) ProgressBar progressBar;
    private UserPreferences prefs;

    private BookHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        helper = new BookHelper(this);
        helper.open();
        prefs = new UserPreferences(this);


        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                login();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void login(){
        String email = tvEmail.getText().toString().trim();
        String password = tvPassword.getText().toString().trim();
        boolean error = false;
        if(email.isEmpty()){
            error = true;
            tvEmail.setError(getString(R.string.empty_field));
        }

        boolean isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if(!isEmail){
            error = true;
            tvEmail.setError(getString(R.string.email_constraint));
        }

        if(password.isEmpty()){
            error = true;
            tvEmail.setError(getString(R.string.empty_field));
        }

        if(!error){
            doLogin(email, password);
        }
    }

    private String doLogin(String uemail, String upassword){

        final String email = uemail;
        final String password = upassword;
        RequestQueue queue = Volley.newRequestQueue(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();
        ImageLoader.getInstance().init(config);
        String url = EndPoints.POST_DO_LOGIN;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String result) {
                        // response
                        List<Book> books = new ArrayList<>();
                        Log.d("Response", result);
                        try {
                            //String result = new String(responseBody);
                            System.out.println(result);
                            JSONObject response = new JSONObject(result);
                            JSONObject obj = response.getJSONObject("reader");

                            prefs.setToken(obj.getString("reg_number"))
                                    .setReaderName(obj.getString("name"))
                                    .setPicture(obj.getString("photo"))
                                    .setReaderServerId(obj.getInt("reader_id"))
                                    .setAddress(obj.getString("location"))
                                    .doCommit();

                            JSONArray list = response.getJSONArray("books");
                            int size = list.length();
                            int count = 1;
                            for (int i = 0; i < list.length(); i++) {
                                final JSONObject bookObj = list.getJSONObject(i);
                                ImageLoader imageLoader = ImageLoader.getInstance();

                                imageLoader.loadImage(bookObj.getString("cover"), new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        // Do whatever you want with Bitmap
                                        try {
                                            new ImageSaver(LoginActivity.this)
                                                    .setFileName(getThisFileName(bookObj.getString("cover")))
                                                    .setDirectoryName("bookCovers")
                                                    .save(loadedImage);
                                            ContentValues values = new ContentValues();
                                            values.put(TITLE, bookObj.getString("title"));
                                            values.put(SERVER_ID, bookObj.getInt("id"));
                                            values.put(AUTHOR, bookObj.getString("author"));
                                            values.put(PUBLISHER, bookObj.getString("publisher"));
                                            values.put(GET_FROM, bookObj.getString("get_from"));
                                            values.put(REVIEW, bookObj.getString("review"));
                                            values.put(RATING, bookObj.getInt("rating"));
                                            values.put(COVER, getThisFileName(bookObj.getString("cover")));
                                            values.put(DATE, bookObj.getString("created_at"));
                                            getContentResolver().insert(DatabaseContract.BOOK_CONTENT_URI, values);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                //Book book = new Book(bookObj);
                                //books.add(book);
                                progressBar.setProgress((count/size)*100);
                                count++;
                            }
                        } catch (Exception e) {
                            Log.e(LoginActivity.class.getSimpleName(), "doLogin: postRequest");
                            e.printStackTrace();
                        }
                        //go to dashboard
                        MainActivity.goToYourBook(LoginActivity.this);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(postRequest);
        return null;
    }

    private String getThisFileName(String str){
        String[] img = str.split("/");
        return img[img.length-1];
    }
}
