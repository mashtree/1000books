package uxt6.psu.com.a1000books;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import uxt6.psu.com.a1000books.db.BookHelper;
import uxt6.psu.com.a1000books.db.DBHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.settings.UserPreferences;

public class BookActivity extends AppBaseActivity
implements View.OnClickListener{

    @BindView(R.id.rv_your_book) RecyclerView rvYourBook;
    @BindView(R.id.fab_add) FloatingActionButton fabAddBook;
    @BindView(R.id.progressbar) ProgressBar progressBar;

    private Cursor list;
    private BookCursorAdapter adapter;
    private boolean filtered = false;
    private BookHelper helper;
    private UserPreferences prefs;
    private int startList = 0;
    private int perPage = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ButterKnife.bind(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        filtered = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_display_uploaded),false);

        if (savedInstanceState != null) {
            filtered = savedInstanceState.getBoolean("filtered");
            startList = savedInstanceState.getInt("start_list");
            perPage = savedInstanceState.getInt("list_per_page");
        }

        helper = new BookHelper(BookActivity.this);
        helper.open();
        rvYourBook.setLayoutManager(new LinearLayoutManager(this));
        rvYourBook.setHasFixedSize(true);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        fabAddBook.setOnClickListener(this);

        adapter = new BookCursorAdapter(this);
        adapter.setListBooks(list);
        rvYourBook.setAdapter(adapter);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //new LoadNoteAsync().execute();
        fabAddBook.setVisibility(View.GONE);

        prefs = new UserPreferences(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!isExistYourPreference()){
            goToRegistrationPage();
        }
        if(helper.getDatabase()==null){
            DBHelper.getInstance(this).getWritableDatabase(new DBHelper.OnDBReadyListener() {
                @Override
                public DBHelper.EntityHelper onDBReady(SQLiteDatabase db) {
                    helper.setDatabase(db);
                    onAsyncLoadBooks(filtered);
                    return null;
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fab_add){
            Intent intent = new Intent(BookActivity.this, FormAddUpdateBookActivity.class);
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

                    String selection = DatabaseContract.BookColumns.SERVER_ID+">?";
                    Log.d(BookActivity.class.getSimpleName(), "onPostExecute: true "+filtered+" "+selection);
                    //return getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI,null, selection, null, order);
                    return helper.queryByUploaded(selection, new String[]{String.valueOf(0)});
                }else{
                    Log.d(BookActivity.class.getSimpleName(), "onPostExecute: false "+filtered);
                    //return getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI,null, null, null, order);
                    return helper.queryProvider();
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
                if(list==null){//.getCount()==0){
                    showSnackbarMessage("Try again in a fiew seconds");
                }else{
                    Log.d(BookActivity.class.getSimpleName(), "onPostExecute: "+books.getCount());
                }
            }

        }.execute(filtered);
    }

    /*private class LoadNoteAsync extends AsyncTask<Void,Void,Cursor> {
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
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==FormAddUpdateBookActivity.REQUEST_ADD){
            if(resultCode==FormAddUpdateBookActivity.RESULT_ADD){
                //new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }else if(requestCode==FormAddUpdateBookActivity.REQUEST_UPDATE) {
            if (resultCode == FormAddUpdateBookActivity.RESULT_UPDATE) {
                //new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);

            } else if (resultCode == FormAddUpdateBookActivity.RESULT_DELETE) {
                //new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==BookDetailActivity.REQUEST_UPLOAD){
            if(resultCode==BookDetailActivity.RESULT_UPLOAD){
                //new LoadNoteAsync().execute();
                Toast.makeText(BookActivity.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
            }
        }

        //new LoadNoteAsync().execute();
        onAsyncLoadBooks(filtered);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //helper.close();
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvYourBook, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_book, menu);
        if(filtered){
            menu.getItem(2).setIcon(R.drawable.ic_sd_storage_white);
        }else{
            menu.getItem(2).setIcon(R.drawable.ic_public_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.menu_add:
                Intent intent = new Intent(BookActivity.this, FormAddUpdateBookActivity.class);
                startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_ADD);
                break;
            case R.id.menu_local:
                filtered = !filtered;
                if(filtered){
                    item.setIcon(R.drawable.ic_sd_storage_white);
                }else{
                    item.setIcon(R.drawable.ic_public_white);
                }
                onAsyncLoadBooks(filtered);
                break;
            case R.id.menu_search:
                Intent intentSearch = new Intent(BookActivity.this, SearchBookActivity.class);
                startActivity(intentSearch);
                break;
            case R.id.menu_refresh:
                onAsyncLoadBooks(filtered);
                break;
            /*case R.id.menu_settings:
                Intent intentSetting = new Intent(BookActivity.this, SettingActivity.class);
                startActivity(intentSetting);
                break;*/
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("filtered", filtered);
        outState.putInt("start_list", startList);
        outState.putInt("list_per_page", perPage);
    }

    /**
     * checking the preferences
     * @return
     */
    private boolean isExistYourPreference(){
        //String name = pref.getString(getString(R.string.your_name), "");
        String name = prefs.getReaderName();
        if(name.length()>0) return true;
        return false;
    }

    /**
     * go to your book activity
     */
    private void goToRegistrationPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
