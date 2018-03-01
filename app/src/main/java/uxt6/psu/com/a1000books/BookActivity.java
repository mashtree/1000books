package uxt6.psu.com.a1000books;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.adapter.BookAdapter;
import uxt6.psu.com.a1000books.db.DatabaseContract;

public class BookActivity extends AppCompatActivity
implements View.OnClickListener{

    @BindView(R.id.rv_your_book)
    RecyclerView rvYourBook;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAddBook;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    private Cursor list;
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ButterKnife.bind(this);

        rvYourBook.setLayoutManager(new LinearLayoutManager(this));
        rvYourBook.setHasFixedSize(true);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        fabAddBook.setOnClickListener(this);

        adapter = new BookAdapter(this);
        adapter.setListBooks(list);
        rvYourBook.setAdapter(adapter);

        new LoadNoteAsync().execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fab_add){
            showSnackbarMessage("test");
            Intent intent = new Intent(BookActivity.this, FormAddUpdateBookActivity.class);
            startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_ADD);
        }
    }

    private class LoadNoteAsync extends AsyncTask<Void,Void,Cursor> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI,null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor books){
            super.onPostExecute(books);
            progressBar.setVisibility(View.GONE);

            list = books;
            adapter.setListBooks(list);
            adapter.notifyDataSetChanged();
            rvYourBook.setAdapter(adapter);
            showSnackbarMessage(books.getCount()+" data");
            if(list.getCount()==0){
                showSnackbarMessage("No data");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==FormAddUpdateBookActivity.REQUEST_ADD){
            if(resultCode==FormAddUpdateBookActivity.RESULT_ADD){
                new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }else if(requestCode==FormAddUpdateBookActivity.REQUEST_UPDATE) {
            if (resultCode == FormAddUpdateBookActivity.RESULT_UPDATE) {
                new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);

            } else if (resultCode == FormAddUpdateBookActivity.RESULT_DELETE) {
                new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            }
        }

        new LoadNoteAsync().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvYourBook, message, Snackbar.LENGTH_SHORT).show();
    }
}
