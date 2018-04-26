package uxt6.psu.com.a1000books;

import android.app.LoaderManager;
import android.os.Bundle;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.adapter.BookListAdapter;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.loader.BookAsyncTaskLoader;

public class SearchBookActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<List<Book>>, View.OnClickListener {

    @BindView(R.id.rv_book) RecyclerView rvBooks;
    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.edt_keyword) EditText edtKeyword;
    @BindView(R.id.btn_search) Button btnSearch;
    @BindView(R.id.rb_title) RadioButton rbTitle;
    @BindView(R.id.rb_author) RadioButton rbAuthor;
    @BindView(R.id.tv_info) TextView tvInfo;

    public static final String EXTRAS_KEYWORD = "uxt6.psu.com.a1000books.KEYWORD";
    public static final String EXTRAS_INCLUDE = "uxt6.psu.com.a1000books.INCLUDE";
    private BookListAdapter adapter;
    private boolean filtered = false;

    private String selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ButterKnife.bind(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        filtered = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_search_include_your_book),false);

        adapter = new BookListAdapter(this);
        //adapter.notifyDataSetChanged();
        rvBooks.setHasFixedSize(true);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
        btnSearch.setOnClickListener(this);
        //setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(savedInstanceState!=null){
            //searchBook(savedInstanceState.getString(SearchBookActivity.EXTRAS_KEYWORD));
            edtKeyword.setText(savedInstanceState.getString(SearchBookActivity.EXTRAS_KEYWORD));
        }

        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle instanceState){
        super.onRestoreInstanceState(instanceState);
        edtKeyword.setText(instanceState.getString(SearchBookActivity.EXTRAS_KEYWORD));
        //searchBook(instanceState.getString(SearchBookActivity.EXTRAS_KEYWORD));
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_search){
            String keyword = edtKeyword.getText().toString().trim();
            if(TextUtils.isEmpty(keyword)){
                edtKeyword.setError(getString(R.string.empty_field));
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(EXTRAS_KEYWORD, keyword);
            bundle.putBoolean(EXTRAS_INCLUDE, filtered);
            getLoaderManager().restartLoader(0, bundle, SearchBookActivity.this);
        }
    }

    private void searchBook(String keyword){
        if(TextUtils.isEmpty(keyword)){
            edtKeyword.setError(getString(R.string.empty_field));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(EXTRAS_KEYWORD, keyword);
        getLoaderManager().restartLoader(0, bundle, SearchBookActivity.this);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {
        String keyword = "";
        if(bundle!=null){
            keyword = bundle.getString(SearchBookActivity.EXTRAS_KEYWORD);
            filtered = bundle.getBoolean(SearchBookActivity.EXTRAS_INCLUDE);
        }
        if(rbTitle.isChecked()){
            selection = DatabaseContract.BookColumns.TITLE;
        }else{
            selection = DatabaseContract.BookColumns.AUTHOR;
        }
        return new BookAsyncTaskLoader(this, keyword, filtered, selection);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Book>> loader, List<Book> books) {
        progressBar.setVisibility(View.GONE);
        adapter.setmData(books);
        adapter.notifyDataSetChanged();
        tvInfo.setText("Found: "+books.size()+" book(s)");
        //rvBooks.setAdapter(adapter);


        System.out.println("ON LOAD FINISHED "+adapter.getItemCount()+"-"+rvBooks.getChildCount());
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Book>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_search_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //if(item.getItemId()==R.id.menu_settings){

        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SearchBookActivity.EXTRAS_KEYWORD, edtKeyword.getText().toString().trim());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
