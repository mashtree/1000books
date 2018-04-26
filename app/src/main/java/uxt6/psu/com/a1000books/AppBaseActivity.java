package uxt6.psu.com.a1000books;
/*
* This is a simple and easy approach to reuse the same
* navigation drawer on your other activities. Just create
* a base layout that conains a DrawerLayout, the
* navigation drawer and a FrameLayout to hold your
* content view. All you have to do is to extend your
* activities from this class to set that navigation
* drawer. Happy hacking :)
* P.S: You don't need to declare this Activity in the
* AndroidManifest.xml. This is just a base class.
*/
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import uxt6.psu.com.a1000books.settings.UserPreferences;
import uxt6.psu.com.a1000books.utility.EndPoints;

/**
 * credits for this code and app_base_layout.xml to:
 * https://gist.github.com/anandbose/7d6efb35c900eaba3b26
 */

public abstract class AppBaseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    private FrameLayout view_stub; //This is the framelayout to keep your content view
    private NavigationView navigation_view; // The new navigation view from Android Design Library. Can inflate menu resources. Easy
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Menu drawerMenu;
    private CircleImageView yourImage;
    private TextView yourName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.app_base_layout);// The base layout that contains your navigation drawer.
        view_stub = (FrameLayout) findViewById(R.id.view_stub);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerMenu = navigation_view.getMenu();
        for(int i = 0; i < drawerMenu.size(); i++) {
            drawerMenu.getItem(i).setOnMenuItemClickListener(this);
        }

        UserPreferences prefs = new UserPreferences(this);

        Log.d(AppBaseActivity.class.getSimpleName(), "onCreate: "+prefs.getPicture());
        View view = navigation_view.getHeaderView(0);
        yourImage = (CircleImageView) view.findViewById(R.id.your_image);
        yourName = (TextView) view.findViewById(R.id.your_name);
        if(prefs.getPicture()!=null &&  prefs.getPicture()!=""){
            Picasso.with(this)
                    .load(prefs.getPicture())
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .error(R.drawable.ic_filter_b_and_w_black_24dp)
                    .into(yourImage);
            yourName.setText(prefs.getReaderName());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Override all setContentView methods to put the content view to the FrameLayout view_stub
     * so that, we can make other activity implementations looks like normal activity subclasses.
     */
    @Override
    public void setContentView(int layoutResID) {
        if (view_stub != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = inflater.inflate(layoutResID, view_stub, false);
            view_stub.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (view_stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view_stub.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view_stub != null) {
            view_stub.addView(view, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_your_book:
                intent = new Intent(AppBaseActivity.this, BookActivity.class);
                break;
            case R.id.nav_search:
                intent = new Intent(AppBaseActivity.this, SearchBookActivity.class);
                break;
            //case R.id.nav_want_to_read:
            //    intent = new Intent(AppBaseActivity.this, WantToReadActivity.class);
            //    break;
            //case R.id.nav_message:
            //    intent = new Intent(AppBaseActivity.this, MessageActivity.class);
            //    break;
            case R.id.nav_your_profile:
                intent = new Intent(AppBaseActivity.this, YourProfileActivity.class);
                break;
            case R.id.menu_settings:
                intent = new Intent(AppBaseActivity.this, SettingActivity.class);
                break;
            default:
                // do nothing at this moment
                break;
        }
        startActivity(intent);
        return false;
    }
}