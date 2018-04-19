package uxt6.psu.com.a1000books;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.plus.PlusShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.db.BookHelper;
import uxt6.psu.com.a1000books.db.DBHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.entity.Comment;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;
import uxt6.psu.com.a1000books.utility.ImageSaver;
import uxt6.psu.com.a1000books.utility.VolleyMultipartRequest;

import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.SERVER_ID;

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.img_cover) ImageView imgCover;
    //@BindView(R.id.tv_rating) TextView tvRating;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_author) TextView tvAuthor;
    @BindView(R.id.tv_publisher) TextView tvPublisher;
    @BindView(R.id.tv_review) TextView tvReview;
    @BindView(R.id.tv_get_from) TextView tvGetFrom;
    @BindView(R.id.bt_upload) Button btUpload;

    UserPreferences prefs;
    private Book book;
    private BookHelper helper;

    public static final int REQUEST_UPLOAD = 400;
    public static final int RESULT_UPLOAD = 401;
    public static final int REQUEST_PLUS = 500;
    private int id;

    public static final String EXTRA_BOOK = "uxt6.psu.com.a1000books.EXTRA_BOOK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);
        btUpload.setOnClickListener(this);
        id = getIntent().getIntExtra(EXTRA_BOOK, 0);
        prefs = new UserPreferences(this);
        helper = new BookHelper(this);
        helper.open();
        Log.d(BookDetailActivity.class.getSimpleName(), "onCreate: "+prefs.getReaderName()+"-"+EndPoints.POST_BOOK_URL+" book_id&reader_id="+id+"&"+prefs.getReaderServerId());
        getSupportActionBar().setTitle(getString(R.string.detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(helper.getDatabase()==null){
            DBHelper.getInstance(this).getWritableDatabase(new DBHelper.OnDBReadyListener() {
                @Override
                public DBHelper.EntityHelper onDBReady(SQLiteDatabase db) {
                    helper.setDatabase(db);
                    onRetriveDetailBook(id);
                    return null;
                }
            });
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void onRetriveDetailBook(int id){
        new AsyncTask<Integer, Void, Book>(){

            @Override
            protected Book doInBackground(Integer... params) {
                int id = params[0];
                Log.d("onRetrieveDetailBook", "doInBackground: id="+id);
                String selection = DatabaseContract.BookColumns._ID+"="+id;
                String[] selectionArgs = new String[]{String.valueOf(id)};
                //Cursor cursor = getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI,null,
                //        selection, null, null);
                Cursor cursor = helper.queryByIdProvider(String.valueOf(id));
                if(cursor.moveToFirst()){
                    return new Book(cursor);
                }else{
                    return null;
                }
            }

            protected void onPostExecute(Book res){
                book = res;
                Bitmap bitmap = new ImageSaver(BookDetailActivity.this)
                        .setFileName(book.getCover())
                        .setDirectoryName("bookCovers")
                        .load();
                imgCover.setImageBitmap(bitmap);
                //tvRating.setText(String.valueOf(book.getRating()));
                ratingBar.setRating(book.getRating());
                Log.d(BookDetailActivity.class.getSimpleName(), "onCreate: getRating:"+book.getRating()+", tvRating:"+ratingBar.getRating());
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(tvAuthor.getText().toString().trim()+" "+book.getAuthor());
                tvPublisher.setText(tvPublisher.getText().toString().trim()+" "+book.getPublisher());
                tvReview.setText(book.getReview());
                tvGetFrom.setText(tvGetFrom.getText().toString().trim()+" "+book.getGet_from());
                filename = book.getCover();
                if(book.getServerId()>0){
                    btUpload.setVisibility(View.GONE);
                }
            }
        }.execute(id);
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
                            Log.d(BookDetailActivity.class.getSimpleName(), "onResponse: "+new String(response.data).toString());
                            JSONObject obj = new JSONObject(new String(response.data));

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
                /*String title = tvTitle.getText().toString().trim();
                String author = tvAuthor.getText().toString().trim();
                String publisher = tvPublisher.getText().toString().trim();
                String review = tvReview.getText().toString().trim();
                String getFrom = tvGetFrom.getText().toString().trim();
                //int rating = Integer.parseInt(tvRating.getText().toString().trim());
                int rating = (int) ratingBar.getRating();*/
                String title = book.getTitle();
                String author = book.getAuthor();
                String publisher = book.getPublisher();
                String review = book.getReview();
                String getFrom = book.getGet_from();
                //int rating = Integer.parseInt(tvRating.getText().toString().trim());
                int rating = book.getRating();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_detail_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_upload){
            if(book.getServerId()>0){
                Toast.makeText(BookDetailActivity.this, book.getTitle()+" was uploaded", Toast.LENGTH_LONG).show();
            }else{
                Bitmap bitmap = ((BitmapDrawable)imgCover.getDrawable()).getBitmap();
                uploadBitmap(bitmap);
                setResult(RESULT_UPLOAD);
                finish();
            }
        }else if(item.getItemId()==R.id.menu_settings){

        }else if(item.getItemId()==R.id.menu_gplus){
            if(book.getServerId()>0){
                StringBuilder sb = new StringBuilder();
                sb.append(book.getTitle());
                sb.append("\n");
                sb.append("by "+book.getAuthor()+"\n");
                sb.append(book.getReview());
                gplusUpload(sb.toString());
            }else{
                Toast.makeText(BookDetailActivity.this, "the book need to be uploaded", Toast.LENGTH_LONG).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private void gplusUpload(String params){
        final String text = params;
        new AsyncTask<Void, Void, String>(){
            private final String url = EndPoints.GET_BOOK_URL+book.getServerId();
            @Override
            protected String doInBackground(Void... params){
                String jsonString = null;
                try {

                    if (jsonString == null || jsonString == "") {
                        jsonString = DetailBookCommentActivity.getJsonFromServer(url);
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
                return jsonString;
            }

            @Override
            public void onPostExecute(String jsonString){
                JSONObject response = null;
                JSONObject bookJson = null;
                JSONArray commentsJson = null;
                try {
                    response = new JSONObject(jsonString);
                    bookJson = response.getJSONObject("books");

                    Intent shareIntent = new PlusShare.Builder(BookDetailActivity.this)
                            .setType("text/plain")
                            .setText(text)
                            .setContentUrl(Uri.parse(bookJson.getString("cover")))
                            .getIntent();

                    startActivityForResult(shareIntent, REQUEST_PLUS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute();
    }
}
