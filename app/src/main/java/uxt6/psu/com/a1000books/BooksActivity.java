package uxt6.psu.com.a1000books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.adapter.BookCursorAdapter;
import uxt6.psu.com.a1000books.db.DBHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;

public class BooksActivity extends AppBaseActivity implements View.OnClickListener{
    @BindView(R.id.rv_your_book)
    RecyclerView rvYourBook;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAddBook;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    private Cursor list;
    private BookCursorAdapter adapter;

    SQLiteDatabase myDb;
    long currentRow;
    private boolean filtered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ButterKnife.bind(this);

        rvYourBook.setLayoutManager(new LinearLayoutManager(this));
        rvYourBook.setHasFixedSize(true);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        fabAddBook.setOnClickListener(this);

        adapter = new BookCursorAdapter(this);
        adapter.setListBooks(list);
        rvYourBook.setAdapter(adapter);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //new BooksActivity.LoadNoteAsync().execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        // get writable database
        /*DBHelper.getInstance(this).getWritableDatabase(new DBHelper.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                myDb = db;
            }
        });*/
        //if (myDb!=null){
            //new LoadNoteAsync().execute();
        onAsyncLoadBooks(filtered);
        //}
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fab_add){
            //showSnackbarMessage("test");
            Intent intent = new Intent(BooksActivity.this, FormAddUpdateBookActivity.class);
            startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_ADD);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void onAsyncLoadBooks(boolean filtered){
        new AsyncTask<Boolean, Void, Cursor>(){
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            protected Cursor doInBackground(Boolean... params){
                boolean filtered = params[0];
                String order = DatabaseContract.BookColumns._ID+" DESC";
                if(filtered){
                    String selection = DatabaseContract.BookColumns.SERVER_ID+">0";
                    return getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI,null, selection, null, order);
                }else{
                    return getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI,null, null, null, order);
                }
            }
            @Override
            protected void onPostExecute(Cursor books){
                super.onPostExecute(books);
                progressBar.setVisibility(View.GONE);

                list = books;
                adapter.setListBooks(list);
                adapter.notifyDataSetChanged();
                //showSnackbarMessage(books.getCount()+" data");
                if(list.getCount()==0){
                    showSnackbarMessage("No data");
                }
            }

        }.execute(filtered);
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
            //showSnackbarMessage(books.getCount()+" data");
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
                new BooksActivity.LoadNoteAsync().execute();
                Toast.makeText(BooksActivity.this, getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }else if(requestCode==FormAddUpdateBookActivity.REQUEST_UPDATE) {
            if (resultCode == FormAddUpdateBookActivity.RESULT_UPDATE) {
                new BooksActivity.LoadNoteAsync().execute();
                Toast.makeText(BooksActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);

            } else if (resultCode == FormAddUpdateBookActivity.RESULT_DELETE) {
                new BooksActivity.LoadNoteAsync().execute();
                Toast.makeText(BooksActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            }
        }

        //new BooksActivity.LoadNoteAsync().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvYourBook, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_book, menu);
        if(filtered){
            menu.getItem(R.id.menu_local).setIcon(R.drawable.ic_public_white);
        }else{
            menu.getItem(R.id.menu_local).setIcon(R.drawable.ic_sd_storage_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.menu_add:
                Intent intent = new Intent(BooksActivity.this, FormAddUpdateBookActivity.class);
                startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_ADD);
                break;
            case R.id.menu_local:
                filtered = !filtered;
                if(filtered){
                    item.setIcon(R.drawable.ic_public_white);
                }else{
                    item.setIcon(R.drawable.ic_sd_storage_white);
                }
                onAsyncLoadBooks(filtered);
                break;
            case R.id.menu_search:

                break;
            case R.id.menu_settings:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
