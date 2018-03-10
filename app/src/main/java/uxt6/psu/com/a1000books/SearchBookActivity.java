package uxt6.psu.com.a1000books;

import android.app.LoaderManager;
import android.os.Bundle;
import android.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.adapter.BookListAdapter;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.loader.BookAsyncTaskLoader;

public class SearchBookActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<List<Book>>, View.OnClickListener {

    @BindView(R.id.rv_book) RecyclerView rvBooks;
    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.edt_keyword) EditText edtKeyword;
    @BindView(R.id.btn_search) Button btnSearch;

    public static final String EXTRAS_KEYWORD = "uxt6.psu.com.a1000books.KEYWORD";
    private BookListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        ButterKnife.bind(this);

        adapter = new BookListAdapter(this);
        //adapter.notifyDataSetChanged();
        rvBooks.setHasFixedSize(true);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
        btnSearch.setOnClickListener(this);
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
            getLoaderManager().restartLoader(0, bundle, SearchBookActivity.this);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {
        String keyword = "";
        if(bundle!=null){
            keyword = bundle.getString(SearchBookActivity.EXTRAS_KEYWORD);
        }
        return new BookAsyncTaskLoader(this, keyword);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Book>> loader, List<Book> books) {
        progressBar.setVisibility(View.GONE);
        adapter.setmData(books);
        adapter.notifyDataSetChanged();
        rvBooks.setAdapter(adapter);


        System.out.println("ON LOAD FINISHED "+adapter.getItemCount()+"-"+rvBooks.getChildCount());
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Book>> loader) {

    }
}
