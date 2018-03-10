package uxt6.psu.com.a1000books;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;
import uxt6.psu.com.a1000books.utility.ImageSaver;
import uxt6.psu.com.a1000books.utility.VolleyMultipartRequest;

import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.SERVER_ID;

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.img_cover) ImageView imgCover;
    @BindView(R.id.tv_rating) TextView tvRating;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_author) TextView tvAuthor;
    @BindView(R.id.tv_publisher) TextView tvPublisher;
    @BindView(R.id.tv_review) TextView tvReview;
    @BindView(R.id.tv_get_from) TextView tvGetFrom;
    @BindView(R.id.bt_upload) Button btUpload;

    UserPreferences prefs;
    private Book book;

    public static final String EXTRA_BOOK = "uxt6.psu.com.a1000books.EXTRA_BOOK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);
        btUpload.setOnClickListener(this);
        book = getIntent().getParcelableExtra(EXTRA_BOOK);
        prefs = new UserPreferences(this);
        Bitmap bitmap = new ImageSaver(this)
                .setFileName(book.getCover())
                .setDirectoryName("bookCovers")
                .load();
        imgCover.setImageBitmap(bitmap);
        tvRating.setText(String.valueOf(book.getRating()));
        Log.d(BookDetailActivity.class.getSimpleName(), "onCreate: getRating:"+book.getRating()+", tvRating:"+tvRating.getText().toString());
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(tvAuthor.getText().toString().trim()+" "+book.getAuthor());
        tvPublisher.setText(tvPublisher.getText().toString().trim()+" "+book.getPublisher());
        tvReview.setText(book.getReview());
        tvGetFrom.setText(tvGetFrom.getText().toString().trim()+" "+book.getGet_from());
        filename = book.getCover();
        if(book.getServerId()>0){
            btUpload.setVisibility(View.GONE);
        }
        Log.d(BookDetailActivity.class.getSimpleName(), "onCreate: "+EndPoints.POST_BOOK_URL);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.bt_upload){
            if(book.getServerId()>0){
                Toast.makeText(BookDetailActivity.this, book.getTitle()+" was uploaded", Toast.LENGTH_LONG).show();
            }else{
                Bitmap bitmap = ((BitmapDrawable)imgCover.getDrawable()).getBitmap();
                uploadBitmap(bitmap);
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String extension = "png";
    private String filename;

    private void uploadBitmap(final Bitmap bitmap) {

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.POST_BOOK_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d(MainActivity.class.getSimpleName(), "onResponse: "+obj.toString());
                            int bookid = obj.getInt("book_id");

                            Uri uri = Uri.parse(DatabaseContract.BOOK_CONTENT_URI+"/"+book.getId());

                            ContentValues values = new ContentValues();
                            values.put(SERVER_ID, bookid);
                            getContentResolver().update(uri, values, null, null);

                            Intent intent = new Intent(BookDetailActivity.this, BookActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String title = tvTitle.getText().toString().trim();
                String author = tvAuthor.getText().toString().trim();
                String publisher = tvPublisher.getText().toString().trim();
                String review = tvReview.getText().toString().trim();
                String getFrom = tvGetFrom.getText().toString().trim();
                int rating = Integer.parseInt(tvRating.getText().toString().trim());
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("author", author);
                params.put("publisher", publisher);
                params.put("review", review);
                params.put("get_from",getFrom);
                params.put("rating",String.valueOf(rating));
                params.put("reader_id", String.valueOf(prefs.getReaderServerId()));
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                params.put("cover", new DataPart(filename, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}
