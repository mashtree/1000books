package uxt6.psu.com.a1000books;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uxt6.psu.com.a1000books.adapter.CommentAdapter;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.entity.Comment;
import uxt6.psu.com.a1000books.utility.EndPoints;

public class DetailBookCommentActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "uxt6.psu.com.a1000books.EXTRA_BOOK";
    private Book book;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_author) TextView tvAuthor;
    @BindView(R.id.tv_publisher) TextView tvPublisher;
    @BindView(R.id.tv_rate) TextView tvRate;
    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.profile_image) CircleImageView profileImg;
    @BindView(R.id.edt_comment) EditText edtComment;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.lv_comments) ListView lvComments;
    @BindView(R.id.expandableTextView) ExpandableTextView expandableTextView;
    @BindView(R.id.button_toggle) ImageButton buttonToggle;
    @BindView(R.id.btn_submit_comment) Button btnSubmitComment;
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

        new LoadCommentAsync().execute();

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        expandableTextView.setAnimationDuration(1000L);

// set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(new OvershootInterpolator());

// or set them separately
        expandableTextView.setExpandInterpolator(new OvershootInterpolator());
        expandableTextView.setCollapseInterpolator(new OvershootInterpolator());



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
                try {
                    response = new JSONObject(jsonString);
                    bookJson = response.getJSONObject("books");
                    commentsJson = response.getJSONArray("comments");
                    for (int i = 0; i < commentsJson.length(); i++) {
                        JSONObject commentObj = commentsJson.getJSONObject(i);

                        Comment comment = new Comment(commentObj);
                        comments.add(comment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(bookJson!=null){
                    try {
                        tvTitle.setText(bookJson.getString("title"));
                        tvAuthor.setText("by "+bookJson.getString("author"));
                        tvRate.setText(" "+bookJson.getString("rating"));
                        //tvReview.setText(bookJson.getString("review"));
                        expandableTextView.setText(bookJson.getString("review").substring(0, 400)+"...");
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
                                buttonToggle.setImageResource(expandableTextView.isExpanded() ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);
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
                                    buttonToggle.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                                }
                                else
                                {
                                    expandableTextView.expand();
                                    buttonToggle.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
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
                                    view.setText(books.getString("review").substring(0, 400)+"...");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(DetailBookCommentActivity.class.getSimpleName(), "ExpandableTextView collapsed");
                            }
                        });
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
}
