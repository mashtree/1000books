package uxt6.psu.com.a1000books;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.entity.Comment;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;
import uxt6.psu.com.a1000books.utility.VolleyMultipartRequest;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.iv_photo) de.hdodenhof.circleimageview.CircleImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_location) TextView tvLocation;
    @BindView(R.id.edt_phone) EditText edtPhone;
    @BindView(R.id.btn_update) Button btnUpdate;
    @BindView(R.id.edt_password) EditText edtPassword;
    @BindView(R.id.ib_location) ImageButton ibLocation;

    public static final int REQUEST_UPDATE = 100;
    public static final int RESULT_UPDATE = 101;
    UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
        prefs = new UserPreferences(this);
        new LoadReaderAsync().execute(prefs.getToken());
        btnUpdate.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
        ibLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_update){
            boolean error = false;
            /*if(tvLocation.getText().toString().isEmpty()){
                error = true;
                tvLocation.setError(getString(R.string.empty_field));
            }

            if(edtPhone.getText().toString().isEmpty()){
                error = true;
                edtPhone.setError(getString(R.string.empty_field));
            }

            if(!error){*/
            if(((BitmapDrawable)ivPhoto.getDrawable()).getBitmap()!=null){
                Bitmap bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
                updateProfile(bitmap);
            }else{
                Toast.makeText(UpdateProfileActivity.this, "still loading your photo", Toast.LENGTH_LONG).show();
            }

            //}
        }else if(id==R.id.iv_photo){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //EXTERNAL_CONTENT_URI
            startActivityForResult(i, 100);
        }else if(id==R.id.ib_location){
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

    private class LoadReaderAsync extends AsyncTask<String,Void,String> {
        private final String url = EndPoints.GET_READER_URL+"&id=";
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... voids) {
            String jsonString = null;
            try {

                if (jsonString == null || jsonString == "") {
                    jsonString = DetailBookCommentActivity.getJsonFromServer(url+voids[0]);
                    Log.d(DetailBookCommentActivity.class.getSimpleName(), "doInBackground: "+url+voids[0]);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String jsonString){
            super.onPostExecute(jsonString);

            if (jsonString != null && jsonString != "") {
                // do something here
                //System.out.println(jsonString);
                JSONObject response = null;
                JSONObject readerJson = null;
                try {
                    response = new JSONObject(jsonString);
                    readerJson = response.getJSONArray("readers").getJSONObject(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(readerJson!=null){
                    try {
                        latLong = readerJson.getString("location");
                        tvName.setText(readerJson.getString("name"));
                        tvLocation.setText(getLocationDetail(readerJson.getString("location")).get(0).getAddressLine(0));
                        Log.d(DetailBookCommentActivity.class.getSimpleName(), "onPostExecute: photo "+readerJson.getString("photo"));

                        Picasso.with(UpdateProfileActivity.this)
                                .load(readerJson.getString("photo"))
                                .placeholder(R.drawable.ic_photo_black_24dp)
                                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                                .into(ivPhoto);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private Double latitude;
    private Double longitude;
    private List<Address> getLocationDetail(String langLong){
        String[] str = langLong.split(":");
        String[] loc = str[1].replace("(","").replace(")","").trim().split(",");
        latitude = Double.parseDouble(loc[0].trim());
        longitude = Double.parseDouble(loc[1].trim());
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }

    private String latLong;

    private void updateProfile(final Bitmap bitmap) {

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.EDIT_READER_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String json = new String(response.data);
                        try {
                            JSONObject obj = new JSONObject(json);
                            JSONObject array = obj.getJSONObject("readers");
                            prefs.setPicture(array.getString("photo"));
                            prefs.setPhoneNumber(array.getString("phone"));
                            prefs.setAddress(array.getString("location"));
                            prefs.doCommit();
                            finish();

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
            * any string
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("id", prefs.getToken());
                params.put("name", tvName.getText().toString().trim());
                params.put("location", latLong);
                params.put("phone", edtPhone.getText().toString().trim());
                params.put("password", edtPassword.getText().toString().trim());
                return params;
            }

            /*
            * Here we are passing image
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                params.put("photo", new DataPart(filename, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        setResult(RESULT_UPDATE);
        finish();
    }

    private String extension = "png";
    private String filename;
    private final int MAX_HEIGHT = 400;
    private final int MAX_WIDTH = 300;
    private final String TAG = UpdateProfileActivity.class.getSimpleName();
    public static int PLACE_PICKER_REQUEST = 101;
    public static int IMAGE_PICKER_REQUEST = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();

            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int imgHeight = bitmap.getHeight();
                int imgWidth = bitmap.getWidth();
                Log.d(TAG, "onActivityResult: image height="+imgHeight+" image size="+bitmap.getByteCount());
                Bitmap newBitmap = bitmap;
                if(imgHeight>MAX_HEIGHT || imgWidth>MAX_WIDTH){

                    newBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*MAX_HEIGHT/imgHeight, MAX_HEIGHT, false);
                    Log.d(TAG, "onActivityResult: img size "+newBitmap.getByteCount());
                }
                //displaying selected image to imageview
                ivPhoto.setImageBitmap(bitmap);
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

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}
