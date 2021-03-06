package uxt6.psu.com.a1000books.loader;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.content.AsyncTaskLoader;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import uxt6.psu.com.a1000books.SearchBookActivity;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;

/**
 * Created by aisyahumar on 3/6/2018.
 */

public class BookAsyncTaskLoader extends AsyncTaskLoader<List<Book>> {
    private List<Book> mData;
    private boolean mHasResult = false;
    private String keyword;
    private String action;
    //private static final Map<String, String> url = new HashMap<>();
    private String url;
    private final String urls = EndPoints.SEARCH_BOOK_URL;
    private String selection;
    private int includeOwnBook = 0;

    public BookAsyncTaskLoader(Context context, String keyword, String selection) {
        super(context);
        onContentChanged();

        action = SearchBookActivity.EXTRAS_KEYWORD;
        this.selection=selection;

        this.keyword = keyword;
    }

    public BookAsyncTaskLoader(Context context, String keyword, boolean includeOwnBook, String selection) {
        super(context);
        onContentChanged();

        action = SearchBookActivity.EXTRAS_KEYWORD;
        this.includeOwnBook = includeOwnBook?1:0;
        this.selection=selection;

        this.keyword = keyword;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        } else if (mHasResult) {
            deliverResult(mData);
        }
    }

    @Override
    public void deliverResult(final List<Book> data) {
        mData = data;
        mHasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mHasResult) {
            //onReleaseResources(mData);
            mData = null;
            mHasResult = false;
        }
    }

    private void onReleaseResources(List<Book> mData) {
    }

    @Override
    public List<Book> loadInBackground() {
        SyncHttpClient client = new SyncHttpClient();

        final List<Book> books = new ArrayList<>();
        String url = EndPoints.SEARCH_BOOK_URL + keyword;
        if(includeOwnBook!=1){
            UserPreferences prefs = new UserPreferences(getContext());
            url = url + "&id="+prefs.getReaderServerId();
        }
        if(selection!=null){
            url = url+"&filter="+selection;
        }
        System.out.println(url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

                setUseSynchronousMode(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    Log.d(BookAsyncTaskLoader.class.getSimpleName(), "onSuccess: "+result);
                    JSONObject response = new JSONObject(new String(responseBody));
                    JSONArray list = response.getJSONArray("books");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject bookObj = list.getJSONObject(i);

                        Book book = new Book(bookObj);
                        books.add(book);
                    }
                } catch (Exception e) {
                    Log.e("Book Model", "onSuccess: ");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return books;
    }
}
