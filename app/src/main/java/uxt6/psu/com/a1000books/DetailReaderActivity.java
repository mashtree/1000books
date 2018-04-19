package uxt6.psu.com.a1000books;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.entity.Comment;
import uxt6.psu.com.a1000books.utility.EndPoints;

public class DetailReaderActivity extends AppCompatActivity {

    private Book book;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_photo)
    CircleImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reader);
        ButterKnife.bind(this);
        book = getIntent().getParcelableExtra(DetailBookCommentActivity.EXTRA_BOOK);
        //setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getString(R.string.reader));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new LoadReaderAsync(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_detail_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:

                break;
            case R.id.menu_call:
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString().trim()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    this.startActivity(callIntent);
                }else{
                    this.startActivity(callIntent);
                }
                break;
            case android.R.id.home:
                Intent intent = new Intent(DetailReaderActivity.this, DetailBookCommentActivity.class);
                intent.putExtra(DetailBookCommentActivity.EXTRA_BOOK, book);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return true;
    }

    private class LoadReaderAsync extends AsyncTask<Void, Void, String> {
        private final String url = EndPoints.GET_READER_BY_BOOK_URL + book.getServerId();
        private Context context;

        public LoadReaderAsync(Context c) {
            context = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(DetailReaderActivity.class.getSimpleName(), "onPreExecute: " + url);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String jsonString = null;
            try {

                if (jsonString == null || jsonString == "") {
                    jsonString = getJsonFromServer(url);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            List<Comment> comments = new ArrayList<>();

            if (jsonString != null && jsonString != "") {
                Log.d(DetailReaderActivity.class.getSimpleName(), "onPostExecute: " + jsonString);
                JSONObject response = null;
                JSONObject readerJson = null;
                try {
                    response = new JSONObject(jsonString);
                    readerJson = response.getJSONObject("readers");

                    tvName.setText(readerJson.getString("name"));
                    tvLocation.setText(getLocationDetail(readerJson.getString("location")).get(0).getAddressLine(0));
                    //tvLocation.setText(readerJson.getString("location"));
                    tvPhone.setText(readerJson.getString("phone"));
                    Picasso.with(DetailReaderActivity.this)
                            .load(readerJson.getString("photo"))
                            .placeholder(R.drawable.ic_photo_black_24dp)
                            .error(R.drawable.ic_filter_b_and_w_black_24dp)
                            .into(ivPhoto);
                    tvPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString().trim()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                context.startActivity(intent);
                                return;
                            }else{
                                context.startActivity(intent);
                            }

                        }
                    });
                    final double lat = latitude;
                    final double longit = longitude;
                    tvLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri gmmIntentUri = Uri.parse("geo:"+lat+","+longit);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
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

        /*String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*/

        return addresses;
    }

    public static String getJsonFromServer(String url) throws IOException {

        BufferedReader inputStream = null;

        URL jsonUrl = new URL(url);
        URLConnection dc = jsonUrl.openConnection();

        dc.setConnectTimeout(5000);
        dc.setReadTimeout(5000);

        inputStream = new BufferedReader(new InputStreamReader(
                dc.getInputStream()));

        // read the JSON results into a string
        String jsonResult = inputStream.readLine();
        inputStream.close();
        return jsonResult;
    }
}
