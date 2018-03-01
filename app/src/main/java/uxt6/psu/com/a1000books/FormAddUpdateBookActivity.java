package uxt6.psu.com.a1000books;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.db.BookHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.utility.BaseDialog;

import static uxt6.psu.com.a1000books.db.DatabaseContract.BookColumns.*;

/**
 * implementing interface BaseDialog.BaseDialogListener that contains code to be run
 */
public class FormAddUpdateBookActivity extends AppCompatActivity implements View.OnClickListener, BaseDialog.BaseDialogListener{

    @BindView(R.id.edt_title) EditText edtTitle;
    @BindView(R.id.edt_author) EditText edtAuthor;
    @BindView(R.id.edt_publisher) EditText edtPublisher;
    @BindView(R.id.edt_get_from) EditText edtGetFrom;
    @BindView(R.id.edt_review) EditText edtReview;
    @BindView(R.id.btn_ok) Button btnOk;
    //@BindView(R.id.btn_cancel) Button btnCancel;

    public static String EXTRA_BOOK = "extra_note";
    public static String EXTRA_POSITION = "extra_position";

    private boolean isEdit = false;
    private boolean isDelete = false;

    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;

    private Book book;
    private int position;
    private BookHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_update);
        ButterKnife.bind(this);

        btnOk.setOnClickListener(this);

        helper = new BookHelper(this);
        helper.open();

        Uri uri = getIntent().getData();
        if(uri!=null){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if(cursor!=null){
                if(cursor.moveToFirst()) book = new Book(cursor);
                cursor.close();
                isEdit = true;
            }
        }

        String actionBarTitle = null;
        String btTitle = null;

        if(isEdit){
            actionBarTitle = "Edit";
            btTitle = "Update";
            edtTitle.setText(book.getTitle());
            edtAuthor.setText(book.getAuthor());
            edtPublisher.setText(book.getPublisher());
            edtGetFrom.setText(book.getGet_from());
            edtReview.setText(book.getReview());
        }else{
            actionBarTitle = "Add";
            btTitle = "Save";
        }

        //btnCancel.setOnClickListener(this);

        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnOk.setText(btTitle);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        if(id==R.id.btn_ok) {
            String title = edtTitle.getText().toString().trim();
            String author = edtAuthor.getText().toString().trim();
            String publisher = edtPublisher.getText().toString().trim();
            String getFrom = edtGetFrom.getText().toString().trim();
            String review = edtReview.getText().toString().trim();

            boolean isEmpty = false;

            if (title.isEmpty()) {
                isEmpty = true;
                edtTitle.setError(getString(R.string.empty_field));
            }

            if (author.isEmpty()) {
                isEmpty = true;
                edtAuthor.setError(getString(R.string.empty_field));
            }

            if (publisher.isEmpty()) {
                isEmpty = true;
                edtPublisher.setError(getString(R.string.empty_field));
            }

            if (review.isEmpty()) {
                isEmpty = true;
                edtReview.setError(getString(R.string.empty_field));
            }

            if (review.length() < 200) {
                //isEmpty = true;
                //edtReview.setError(getString(R.string.at_least_200));
            }

            if (!isEmpty) {
                ContentValues values = new ContentValues();
                values.put(TITLE, title);
                values.put(AUTHOR, author);
                values.put(PUBLISHER, publisher);
                values.put(GET_FROM, getFrom);
                values.put(REVIEW, review);
                values.put(RATING, "0");
                values.put(COVER, "");
                if (isEdit) {
                    getContentResolver().update(getIntent().getData(), values, null, null);
                    setResult(RESULT_UPDATE);
                    finish();
                } else {
                    values.put(DATE, getCurrentDate());
                    getContentResolver().insert(DatabaseContract.BOOK_CONTENT_URI, values);
                    setResult(RESULT_ADD);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(helper!=null){
            helper.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(isEdit){
            getMenuInflater().inflate(R.menu.menu_forms,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete:
                isDelete = true;
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    final int ALERT_DIALOG_CLOSE = 10;
    final int ALERT_DIALOG_DELETE = 20;

    @Override
    public void onBackPressed(){
        if(isEdit){
            showAlertDialog(ALERT_DIALOG_CLOSE);
        }
    }

    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle = null, dialogMessage = null;
        if(isDialogClose){
            dialogTitle = getString(R.string.cancel);
            dialogMessage = getString(R.string.cancel_update_confirm);
        }else{
            dialogMessage = getString(R.string.delete_confirm);
            dialogTitle = getString(R.string.delete);
        }

        // creating and displaying AlertDialog subclass
        // creating an instance and passing this activity that implements BaseDialog.BaseDialogListener
        BaseDialog.getInstance(this)
                // set dialog's title
                .setDialogTitle(dialogTitle)
                // set dialog's message
                .setDialogMessage(dialogMessage)
                // displaying the dialog
                .show();
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * implement callback method,
     */
    @Override
    public void onPositiveClick() {
        if(isEdit && !isDelete){
            finish();
        }else if(isDelete){
            getContentResolver().delete(getIntent().getData(),null,null);
            setResult(RESULT_DELETE,null);
            finish();
        }
    }

    /**
     * implement callback method
     */
    @Override
    public void onNegativeClick() {
        if(isDelete){
            isDelete = false;
        }
    }
}
