package uxt6.psu.com.a1000books;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.db.BookHelper;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.utility.BaseDialog;
import uxt6.psu.com.a1000books.utility.ImageSaver;

import static uxt6.psu.com.a1000books.MainActivity.getMimeType;
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
    @BindView(R.id.spinner_rating) Spinner spRating;
    @BindView(R.id.img_cover) ImageView imgCover;
    @BindView(R.id.ratingBar) RatingBar ratingBar;

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
        imgCover.setOnClickListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRating.setAdapter(adapter);

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
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        String actionBarTitle = null;
        String btTitle = null;

        if(isEdit){
            actionBarTitle = getString(R.string.edit);
            btTitle = getString(R.string.update);
            edtTitle.setText(book.getTitle());
            edtAuthor.setText(book.getAuthor());
            edtPublisher.setText(book.getPublisher());
            edtGetFrom.setText(book.getGet_from());
            edtReview.setText(book.getReview());
            Bitmap bitmap = new ImageSaver(this)
                    .setFileName(book.getCover())
                    .setDirectoryName("bookCovers")
                    .load();
            imgCover.setImageBitmap(bitmap);
            ratingBar.setRating(book.getRating());
            for (int i = 0; i < spRating.getCount(); i++) {
                if (Integer.parseInt(spRating.getItemAtPosition(i).toString())==book.getRating()) {
                    spRating.setSelection(i);
                    break;
                }
            }

        }else{
            actionBarTitle = getString(R.string.add);
            btTitle = getString(R.string.save);
        }
        spRating.setVisibility(View.GONE);
        //btnCancel.setOnClickListener(this);

        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnOk.setText(btTitle);
    }

    @Override
    public void onClick(View view){
        int id = view.getId();
        if(id==R.id.btn_ok) {
            onSaveBook();
        }else if(id==R.id.img_cover){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //EXTERNAL_CONTENT_URI
            startActivityForResult(i, 100);
        }
    }

    private void onSaveBook(){
        String title = edtTitle.getText().toString().trim();
        String author = edtAuthor.getText().toString().trim();
        String publisher = edtPublisher.getText().toString().trim();
        String getFrom = edtGetFrom.getText().toString().trim();
        String review = edtReview.getText().toString().trim();
        int rating = Integer.parseInt(spRating.getSelectedItem().toString());
        int rate = (int) ratingBar.getRating();
        Log.d(FormAddUpdateBookActivity.class.getSimpleName(), "onClick: selected item = "+String.valueOf(rating));

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
            isEmpty = true;
            edtReview.setError(getString(R.string.at_least_200));
        }

        if (!isEmpty) {
            ContentValues values = new ContentValues();
            values.put(TITLE, title);
            values.put(SERVER_ID, 0);
            values.put(AUTHOR, author);
            values.put(PUBLISHER, publisher);
            values.put(GET_FROM, getFrom);
            values.put(REVIEW, review);
            values.put(RATING, rate);
            values.put(COVER, title+filename+"."+extension);
            imgCover.buildDrawingCache();
            Bitmap bitmap = imgCover.getDrawingCache();
            new ImageSaver(this)
                    .setFileName(title+filename+"."+extension)
                    .setDirectoryName("bookCovers")
                    .save(bitmap);
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

    private String extension = "png";
    private String filename;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();

            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //displaying selected image to imageview
                imgCover.setImageBitmap(bitmap);
                extension = getMimeType(this, imageUri);
                File file= new File(imageUri.getPath());
                filename = file.getName();
                //Log.d(MainActivity.class.getSimpleName(), "onActivityResult: file extension = "+filename);
                //calling the method uploadBitmap to upload image
                //uploadBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(helper!=null){
            //helper.close();
        }
    }

    /**
     * options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(isEdit){
            getMenuInflater().inflate(R.menu.activity_form_update,menu);
        }else{
            getMenuInflater().inflate(R.menu.activity_form_add,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * handle option menu items action
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            // when user press the delete menu
            case R.id.action_delete:
                isDelete = true;
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case R.id.menu_save:
                onSaveBook();
                break;
            case R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    final int ALERT_DIALOG_CLOSE = 10;
    final int ALERT_DIALOG_DELETE = 20;

    // back button
    @Override
    public void onBackPressed(){
        if(isEdit){
            showAlertDialog(ALERT_DIALOG_CLOSE);
        }
    }

    /**
     * displaying alert dialog, the title and message depend on the argument
     * @param type
     */
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

        // alert dialog
        BaseDialog.getInstance(this)
                .setDialogTitle(dialogTitle)
                .setDialogMessage(dialogMessage)
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
