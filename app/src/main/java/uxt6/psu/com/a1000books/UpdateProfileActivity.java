package uxt6.psu.com.a1000books;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @BindView(R.id.edt_location) EditText edtLocation;
    @BindView(R.id.edt_phone) EditText edtPhone;
    @BindView(R.id.btn_update) Button btnUpdate;
    @BindView(R.id.edt_password) EditText edtPassword;

    public static final int REQUEST_UPDATE = 100;
    public static final int RESULT_UPDATE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);

        btnUpdate.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_update){
            boolean error = false;
            if(edtLocation.getText().toString().isEmpty()){
                error = true;
                edtLocation.setError(getString(R.string.empty_field));
            }

            if(edtPhone.getText().toString().isEmpty()){
                error = true;
                edtPhone.setError(getString(R.string.empty_field));
            }

            if(!error){
                Bitmap bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
                updateProfile(bitmap);
                finish();
            }
        }else if(id==R.id.iv_photo){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //EXTERNAL_CONTENT_URI
            startActivityForResult(i, 100);
        }
    }

    UserPreferences prefs = new UserPreferences(this);
    private void updateProfile(final Bitmap bitmap) {

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.POST_COMMENT_URL,
                new Response.Listener<NetworkResponse>() {
                    List<Comment> comments = new ArrayList<>();
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String json = new String(response.data);
                        try {
                            JSONObject obj = new JSONObject(json);
                            JSONArray array = obj.getJSONArray("readers");
                            prefs.setPicture(array.getString(5));
                            prefs.setPhoneNumber(array.getString(4));
                            prefs.setAddress(array.getString(3));
                            prefs.doCommit();
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
                params.put("location", edtLocation.getText().toString().trim());
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
                params.put("cover", new DataPart(filename, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
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
