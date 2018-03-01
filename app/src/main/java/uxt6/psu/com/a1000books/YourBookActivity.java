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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.adapter.ListBookAdapter;
import uxt6.psu.com.a1000books.db.DatabaseContract;

public class YourBookActivity extends AppBaseActivity implements View.OnClickListener{

    private Cursor listBooks;
    private ListBookAdapter adapterList;
    @BindView(R.id.rv_your_book)
    RecyclerView rvYourBook;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAddBook;
    @BindView(R.id.tv_nodata)
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_book);
        adapterList = new ListBookAdapter(this);
        ButterKnife.bind(this);

        rvYourBook.setLayoutManager(new LinearLayoutManager(this));
        rvYourBook.setHasFixedSize(true);

        fabAddBook.setOnClickListener(this);

        adapterList.setListBooks(listBooks);
        rvYourBook.setAdapter(adapterList);

        new LoadLocalBookAsync().execute();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id==R.id.fab_add){
            Intent intent = new Intent(YourBookActivity.this, FormAddUpdateBookActivity.class);
            startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_ADD);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==FormAddUpdateBookActivity.REQUEST_ADD){
            if(resultCode==FormAddUpdateBookActivity.RESULT_ADD){
                Toast.makeText(this, getString(R.string.add_success), Toast.LENGTH_LONG).show();
            }
        }else if(requestCode==FormAddUpdateBookActivity.REQUEST_UPDATE){
            if(resultCode==FormAddUpdateBookActivity.RESULT_UPDATE){
                Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_LONG).show();
            }else if(resultCode==FormAddUpdateBookActivity.RESULT_DELETE){
                Toast.makeText(this, getString(R.string.delete_success), Toast.LENGTH_LONG).show();
            }
        }

        new LoadLocalBookAsync().execute();
    }

    private void showRecyclerList(Cursor data){
        listBooks = data;
        rvYourBook.setLayoutManager(new LinearLayoutManager(this));
        adapterList.setListBooks(listBooks);
        rvYourBook.setAdapter(adapterList);
        adapterList.notifyDataSetChanged();
        Log.d(YourBookActivity.class.getSimpleName(),
                "showRecyclerList: "+data.getCount());
    }

    private class LoadLocalBookAsync extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor = getContentResolver().query(DatabaseContract.BOOK_CONTENT_URI, null, null, null, null);

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor){
            super.onPostExecute(cursor);
            //listBooks = cursor;

            //adapterList.setListBooks(listBooks);
            //adapterList.notifyDataSetChanged();
            showRecyclerList(cursor);

            if(cursor==null){
                rvYourBook.setVisibility(View.INVISIBLE);
                tvNoData.setVisibility(View.VISIBLE);
            }else{
                rvYourBook.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.GONE);
                Log.d(YourBookActivity.class.getSimpleName(), "onPostExecute: cursor size="+cursor.getCount());
            }
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvYourBook, message, Snackbar.LENGTH_SHORT).show();
    }
}
