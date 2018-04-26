package uxt6.psu.com.a1000books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.entity.Comment;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;

public class YourProfileActivity extends AppBaseActivity {

    @BindView(R.id.iv_photo) de.hdodenhof.circleimageview.CircleImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_location) TextView tvLocation;
    @BindView(R.id.tv_phone) TextView tvPhone;
    @BindView(R.id.btn_edit) Button btnEdit;
    private UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        ButterKnife.bind(this);
        prefs = new UserPreferences(this);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YourProfileActivity.this, UpdateProfileActivity.class);
                startActivityForResult(intent, UpdateProfileActivity.REQUEST_UPDATE);
            }
        });

        new LoadReaderAsync().execute(prefs.getToken());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==UpdateProfileActivity.REQUEST_UPDATE){
            if(resultCode==UpdateProfileActivity.RESULT_UPDATE){
                Log.d(YourProfileActivity.class.getSimpleName(), "onActivityResult: update profile");

                Toast.makeText(YourProfileActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();

            }
        }
        //new LoadReaderAsync().execute(prefs.getToken());
        onAsyncLoadProfile();
    }

    @SuppressLint("StaticFieldLeak")
    private void onAsyncLoadProfile(){
        new AsyncTask<String,Void,String>(){
            private final String url = EndPoints.GET_READER_URL + "&id=";
            @Override
            protected void onPreExecute () {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground (String...voids){
                String jsonString = null;
                try {

                    if (jsonString == null || jsonString == "") {
                        jsonString = DetailBookCommentActivity.getJsonFromServer(url + voids[0]);
                        Log.d(DetailBookCommentActivity.class.getSimpleName(), "doInBackground: " + url + voids[0]);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block

                    e.printStackTrace();
                }
                return jsonString;
            }

            @Override
            protected void onPostExecute (String jsonString){
                super.onPostExecute(jsonString);
                Log.d(YourProfileActivity.class.getSimpleName(), "onPostExecute: "+(jsonString==null));
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
                    Log.d(YourProfileActivity.class.getSimpleName(), "onPostExecute: readerJson"+(readerJson==null));
                    if (readerJson != null) {
                        try {
                            tvName.setText(readerJson.getString("name"));
                            tvLocation.setText(getLocationDetail(readerJson.getString("location")).get(0).getAddressLine(0));
                            //tvLocation.setText(readerJson.getString("location"));
                            tvPhone.setText(readerJson.getString("phone"));
                            Log.d(DetailBookCommentActivity.class.getSimpleName(), "onPostExecute: location " + getLocationDetail(readerJson.getString("location")).get(0).getAddressLine(0));

                            Picasso.with(YourProfileActivity.this)
                                    .load(new UserPreferences(YourProfileActivity.this).getPicture())
                                    .placeholder(R.drawable.ic_photo_black_24dp)
                                    .error(R.drawable.ic_filter_b_and_w_black_24dp)
                                    .into(ivPhoto);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }.execute(prefs.getToken());
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
                        tvName.setText(readerJson.getString("name"));
                        tvLocation.setText(getLocationDetail(readerJson.getString("location")).get(0).getAddressLine(0));
                        //tvLocation.setText(readerJson.getString("location"));
                        tvPhone.setText(readerJson.getString("phone"));
                        Log.d(DetailBookCommentActivity.class.getSimpleName(), "onPostExecute: location "+getLocationDetail(readerJson.getString("location")).get(0).getAddressLine(0));

                        Picasso.with(YourProfileActivity.this)
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
}
