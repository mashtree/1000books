package uxt6.psu.com.a1000books;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;
import uxt6.psu.com.a1000books.utility.VolleyMultipartRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.tv_name) EditText tvName;
    @BindView(R.id.tv_city) EditText tvCity;
    @BindView(R.id.tv_phone) EditText tvPhone;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.imageView) CircleImageView imageView;

    UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = new UserPreferences(this);

        if(isExistYourPreference()){
            goToYourBook();
        }
        btnSubmit.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btnSubmit){
            String name = tvName.getText().toString().trim();
            String addr = tvCity.getText().toString().trim();
            String phone = tvPhone.getText().toString().trim();

            boolean error = false;

            if(TextUtils.isEmpty(name)){
                error = true;
                tvName.setError(getString(R.string.empty_field));
            }

            if(TextUtils.isEmpty(addr)){
                error = true;
                tvCity.setError(getString(R.string.empty_field));
            }

            if(TextUtils.isEmpty(phone)){
                error = true;
                tvPhone.setError(getString(R.string.empty_field));
            }

            if(!error){
                prefs.setReaderName(name)
                        .setAddress(addr)
                        .setPhoneNumber(phone)
                        .doCommit();
                goToYourBook();
            }

            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            uploadBitmap(bitmap);

        }else if(id==R.id.imageView){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //EXTERNAL_CONTENT_URI
            startActivityForResult(i, 100);
        }
    }

    /*
    * The method is taking Bitmap as an argument
    * then it will return the byte[] array for the given bitmap
    * and we will send this array to the server
    * here we are using PNG Compression with 80% quality
    * you can give quality between 0 to 100
    * 0 means worse quality
    * 100 means best quality
    * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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
                imageView.setImageBitmap(bitmap);
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

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    private void uploadBitmap(final Bitmap bitmap) {

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.POST_READER_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d(MainActivity.class.getSimpleName(), "onResponse: "+obj.toString());
                            prefs.setToken(obj.getString("reg_number"))
                                    .setPicture(EndPoints.ROOT_URL+"readers/"+obj.getString("photo"))
                                    .setReaderServerId(obj.getInt("reader_id"))
                                    .doCommit();
                            //Toast.makeText(getApplicationContext(), prefs.getToken()+"-"+prefs.getPicture(), Toast.LENGTH_SHORT).show();
                            Log.d(MainActivity.class.getSimpleName(), "uploadBitmap: "+prefs.getReaderServerId()+"-"+prefs.getToken()+"-"+prefs.getPicture());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", tvName.getText().toString().trim());
                params.put("location", tvCity.getText().toString().trim());
                params.put("phone", tvPhone.getText().toString().trim());
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(filename + "."+extension, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    /**
     * go to your book activity
     */
    private void goToYourBook(){
        Intent intent = new Intent(this, BookActivity.class);
        startActivity(intent);
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
}
