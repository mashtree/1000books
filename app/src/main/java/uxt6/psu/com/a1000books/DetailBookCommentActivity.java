package uxt6.psu.com.a1000books;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uxt6.psu.com.a1000books.adapter.CommentAdapter;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.entity.Comment;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;
import uxt6.psu.com.a1000books.utility.VolleyMultipartRequest;

public class DetailBookCommentActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "uxt6.psu.com.a1000books.EXTRA_BOOK";
    private Book book;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_author) TextView tvAuthor;
    @BindView(R.id.tv_publisher) TextView tvPublisher;
    @BindView(R.id.tv_rate) TextView tvRate;
    @BindView(R.id.iv_cover) ImageView ivCover;
    @BindView(R.id.profile_image) CircleImageView profileImg;
    @BindView(R.id.edt_comment) EditText edtComment;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.lv_comments) ListView lvComments;
    @BindView(R.id.expandableTextView) ExpandableTextView expandableTextView;
    @BindView(R.id.button_toggle) ImageButton buttonToggle;
    @BindView(R.id.btn_submit_comment) Button btnSubmitComment;
    @BindView(R.id.btn_want_to_read) Button btnWantToRead;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book_comment_2);
        ButterKnife.bind(this);
        book = getIntent().getParcelableExtra(EXTRA_BOOK);
        adapter = new CommentAdapter(lvComments.getContext());
        adapter.notifyDataSetChanged();
        lvComments.setAdapter(adapter);
        btnWantToRead.setVisibility(View.GONE);
        new LoadCommentAsync().execute();

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        expandableTextView.setAnimationDuration(1000L);

// set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(new OvershootInterpolator());

// or set them separately
        expandableTextView.setExpandInterpolator(new OvershootInterpolator());
        expandableTextView.setCollapseInterpolator(new OvershootInterpolator());

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailBookCommentActivity.this, DetailReaderActivity.class);
                intent.putExtra(DetailBookCommentActivity.EXTRA_BOOK, book);
                startActivity(intent);
            }
        });

        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtComment.getText().toString())){
                    edtComment.setError(getString(R.string.empty_field));
                    return;
                }else{
                    sendComment();
                }
            }
        });

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getString(R.string.detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private class LoadCommentAsync extends AsyncTask<Void,Void,String> {
        private final String url = EndPoints.GET_BOOK_URL+book.getServerId();
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String jsonString = null;
            try {

                if (jsonString == null || jsonString == "") {
                    jsonString = getJsonFromServer(url);
                    Log.d(DetailBookCommentActivity.class.getSimpleName(), "doInBackground: "+url);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String jsonString){
            super.onPostExecute(jsonString);
            List<Comment> comments = new ArrayList<>();
            if (jsonString != null && jsonString != "") {
                // do something here
                //System.out.println(jsonString);
                JSONObject response = null;
                JSONObject bookJson = null;
                JSONArray commentsJson = null;
                int star = 0;
                try {
                    response = new JSONObject(jsonString);
                    bookJson = response.getJSONObject("books");
                    commentsJson = response.getJSONArray("comments");
                    for (int i = 0; i < commentsJson.length(); i++) {
                        JSONObject commentObj = commentsJson.getJSONObject(i);
                        star += commentObj.getInt("review_rating");
                        Comment comment = new Comment(commentObj);
                        comments.add(comment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(comments.size()>0){
                    star = star/comments.size();
                    tvRate.setText(String.valueOf(star)+"/"+comments.size());
                }else{
                    tvRate.setText("0/0");
                }

                if(bookJson!=null){
                    try {
                        tvTitle.setText(bookJson.getString("title"));
                        tvAuthor.setText("by "+bookJson.getString("author"));
                        //tvRate.setText(" "+bookJson.getString("rating"));
                        //tvReview.setText(bookJson.getString("review"));
                        if(bookJson.getString("review").length()>=400){
                            expandableTextView.setText(bookJson.getString("review").substring(0, 400)+"...");
                        }else{
                            expandableTextView.setText(bookJson.getString("review"));
                            //buttonToggle.setVisibility(View.GONE);
                        }
                        Log.d(DetailBookCommentActivity.class.getSimpleName(), "onPostExecute: covers "+bookJson.getString("cover"));
                        Picasso.with(DetailBookCommentActivity.this)
                                .load(bookJson.getString("reader_photo"))
                                .placeholder(R.drawable.ic_photo_black_24dp)
                                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                                .into(profileImg);

                        Picasso.with(DetailBookCommentActivity.this)
                                .load(bookJson.getString("cover"))
                                .placeholder(R.drawable.ic_photo_black_24dp)
                                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                                .into(ivCover);
                        //System.out.println(bookJson.getString("reader_photo"));
                        // toggle the ExpandableTextView
                        buttonToggle.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                expandableTextView.toggle();
                                buttonToggle.setImageResource(expandableTextView.isExpanded() ? R.drawable.ic_keyboard_arrow_up_black : R.drawable.ic_keyboard_arrow_down_black);
                            }
                        });

                        // but, you can also do the checks yourself
                        buttonToggle.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                if (expandableTextView.isExpanded())
                                {
                                    expandableTextView.collapse();
                                    buttonToggle.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                                }
                                else
                                {
                                    expandableTextView.expand();
                                    buttonToggle.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                                }
                            }
                        });
                        final JSONObject books = bookJson;
                        // listen for expand / collapse events
                        expandableTextView.addOnExpandListener(new ExpandableTextView.OnExpandListener()
                        {

                            @Override
                            public void onExpand(final ExpandableTextView view)
                            {
                                try {
                                    view.setText(books.getString("review"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(DetailBookCommentActivity.class.getSimpleName(), "ExpandableTextView expanded");
                            }

                            @Override
                            public void onCollapse(final ExpandableTextView view)
                            {
                                try {
                                    if(books.getString("review").length()>399) {
                                        view.setText(books.getString("review").substring(0, 400) + "...");
                                    }else{
                                        view.setText(books.getString("review"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(DetailBookCommentActivity.class.getSimpleName(), "ExpandableTextView collapsed");
                            }
                        });
                        UserPreferences prefs = new UserPreferences(DetailBookCommentActivity.this);
                        Log.d(DetailBookCommentActivity.class.getSimpleName(), "onPostExecute: "+(bookJson.getInt("reader_id")==prefs.getReaderServerId()));
                        if(bookJson.getInt("reader_id")==prefs.getReaderServerId()){
                            btnSubmitComment.setVisibility(View.GONE);
                            ratingBar.setVisibility(View.GONE);
                            edtComment.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            //Log.d(DetailBookCommentActivity.class.getSimpleName(), "onPostExecute: comments size"+comments.size());
            if(comments.size()==0){
                showSnackbarMessage("No data comments");
            }else{
                adapter.setmData(comments);
                //lvComments.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static String getJsonFromServer(String url) throws IOException {

        BufferedReader inputStream = null;

        URL jsonUrl = new URL(url);
        URLConnection dc = jsonUrl.openConnection();

        dc.setConnectTimeout(5000);
        dc.setReadTimeout(5000);

        inputStream = new BufferedReader(new InputStreamReader(
                dc.getInputStream()));

        // read the JSON results into a string
        String jsonResult = inputStream.readLine();
        inputStream.close();
        return jsonResult;
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(lvComments, message, Snackbar.LENGTH_SHORT).show();
    }

    private void sendComment() {

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.POST_COMMENT_URL,
                new Response.Listener<NetworkResponse>() {
                    List<Comment> comments = new ArrayList<>();
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Intent intent = new Intent(DetailBookCommentActivity.this, DetailBookCommentActivity.class);
                        intent.putExtra(DetailBookCommentActivity.EXTRA_BOOK, book);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        /*int star = 0;
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            JSONArray commentsJson = obj.getJSONArray("comments");
                            for (int i = 0; i < commentsJson.length(); i++) {
                                JSONObject commentObj = commentsJson.getJSONObject(i);
                                star += commentObj.getInt("review_rating");

                                Comment comment = new Comment(commentObj);
                                comments.add(comment);
                            }
                            if(comments.size()==0){
                                showSnackbarMessage("No data comments");
                            }else{
                                adapter.setmData(comments);
                                //lvComments.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                lvComments.setAdapter(adapter);
                                //lvComments.setSelection(adapter.getCount() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(comments.size()>0) {
                            star = star / comments.size();
                            tvRate.setText(String.valueOf(star) + "/" + comments.size());
                        }else{
                            tvRate.setText("0/0");
                        }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            *
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int bookId = book.getServerId();
                int readerid = new UserPreferences(DetailBookCommentActivity.this).getReaderServerId();
                Log.d(DetailBookCommentActivity.class.getSimpleName(), "getParams: readerid="+readerid);
                String comment = edtComment.getText().toString().trim();
                int rating = (int) ratingBar.getRating();
                if(rating<1) rating =1;
                Map<String, String> params = new HashMap<>();
                params.put("book_id", String.valueOf(bookId));
                params.put("reader_id", String.valueOf(readerid));
                params.put("comment", comment);
                params.put("review_rating", String.valueOf(rating));
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                //params.put("cover", new DataPart(filename, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_detail_book_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_search){

        }
        return super.onOptionsItemSelected(item);
    }
}
