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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

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
    @BindView(R.id.tv_password) EditText tvPassword;
    @BindView(R.id.tv_city) EditText tvCity;
    @BindView(R.id.tv_email) EditText tvEmail;
    @BindView(R.id.tv_phone) EditText tvPhone;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.imageView) CircleImageView imageView;
    @BindView(R.id.tv_signin) TextView tvSignin;
    @BindView(R.id.ib_location) ImageButton ibLocation;
    @BindView(R.id.tv_location) TextView tvLocation;

    UserPreferences prefs;
    public static int PLACE_PICKER_REQUEST = 101;
    public static int IMAGE_PICKER_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = new UserPreferences(this);

        //if(isExistYourPreference()){
        //    goToYourBook();
        //}
        btnSubmit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        //tvCity.setOnClickListener(this);
        ibLocation.setOnClickListener(this);
        tvSignin.setOnClickListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //if(isExistYourPreference()){
        //    goToYourBook();
        //}
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btnSubmit){
            String name = tvName.getText().toString().trim();
            String password = tvPassword.getText().toString().trim();
            String email = tvEmail.getText().toString().trim();
            String addr = tvLocation.getText().toString().trim();
            String phone = tvPhone.getText().toString().trim();

            boolean error = false;

            if(TextUtils.isEmpty(name)){
                error = true;
                tvName.setError(getString(R.string.empty_field));
            }

            if(TextUtils.isEmpty(password)){
                error = true;
                tvPassword.setError(getString(R.string.empty_field));
            }

            if(TextUtils.isEmpty(email)){
                error = true;
                tvPassword.setError(getString(R.string.empty_field));
            }

            boolean isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

            if(!isEmail){
                error = true;
                tvEmail.setError(getString(R.string.email_constraint));
            }

            if(TextUtils.isEmpty(addr)){
                error = true;
                tvCity.setError(getString(R.string.empty_field));
            }

            if(TextUtils.isEmpty(phone)){
                error = true;
                tvPhone.setError(getString(R.string.empty_field));
            }
            Log.d(MainActivity.class.getSimpleName(), "onClick: btnSubmit "+error);
            if(!error){
                prefs.setReaderName(name)
                        .setAddress(addr)
                        .setPassword(password)
                        .setPhoneNumber(phone)
                        .doCommit();

                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                uploadBitmap(bitmap);
                //goToYourBook();
            }

        }else if(id==R.id.imageView){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //EXTERNAL_CONTENT_URI
            startActivityForResult(i, IMAGE_PICKER_REQUEST);
        }else if(id==R.id.tv_signin){
            Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(iLogin);
        //}else if(id==R.id.tv_city){
        }else if(id==R.id.ib_location){
            /*Uri gmmIntentUri = Uri.parse("geo:0.0,0.0");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivityForResult(mapIntent, 101);*/

            PlacePicker.IntentBuilder builder;
            builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    *
    * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String extension = "png";
    private String filename;
    private final int MAX_HEIGHT = 400;
    private final int MAX_WIDTH = 300;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();

            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //resize bitmap here
                int imgHeight = bitmap.getHeight();
                int imgWidth = bitmap.getWidth();
                Log.d(TAG, "onActivityResult: image height="+imgHeight+" image size="+bitmap.getByteCount());
                Bitmap newBitmap = bitmap;
                if(imgHeight>MAX_HEIGHT || imgWidth>MAX_WIDTH){

                    newBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*MAX_HEIGHT/imgHeight, MAX_HEIGHT, false);
                    Log.d(TAG, "onActivityResult: img size "+newBitmap.getByteCount());
                }
                //displaying selected image to imageview
                imageView.setImageBitmap(newBitmap);
                extension = getMimeType(this, imageUri);
                File file= new File(imageUri.getPath());
                filename = file.getName();

                //Log.d(MainActivity.class.getSimpleName(), "onActivityResult: file extension = "+filename);
                //calling the method uploadBitmap to upload image
                //uploadBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s %s", place.getName(), place.getLatLng().toString());
                latLong = place.getLatLng().toString();
                tvLocation.setText(place.getAddress());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private String latLong;

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
        Log.d(MainActivity.class.getSimpleName(), "uploadBitmap: "+EndPoints.POST_READER_URL);
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.POST_READER_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            Log.d(MainActivity.class.getSimpleName(), "onResponse: "+new String(response.data).toString());
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d(MainActivity.class.getSimpleName(), "onResponse: "+obj.toString());
                            prefs.setToken(obj.getString("reg_number"))
                                    .setPicture(EndPoints.ROOT_URL+"readers/"+obj.getString("photo"))
                                    .setReaderServerId(obj.getInt("reader_id"))
                                    .doCommit();
                            //Toast.makeText(getApplicationContext(), prefs.getToken()+"-"+prefs.getPicture(), Toast.LENGTH_SHORT).show();
                            Log.d(MainActivity.class.getSimpleName(), "uploadBitmap: "+prefs.getReaderServerId()+"-"+prefs.getToken()+"-"+prefs.getPicture());
                            goToYourBook(MainActivity.this);
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
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", tvName.getText().toString().trim());
                params.put("password", tvPassword.getText().toString().trim());
                params.put("email", tvEmail.getText().toString().trim());
                //params.put("location", tvCity.getText().toString().trim());
                params.put("location", latLong);
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
    public static void goToYourBook(Context context){
        Intent intent = new Intent(context, BookActivity.class);
        context.startActivity(intent);
    }
}
