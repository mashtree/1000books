package uxt6.psu.com.a1000books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import uxt6.psu.com.a1000books.settings.UserPreferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.tv_name) EditText tvName;
    @BindView(R.id.tv_city) EditText tvCity;
    @BindView(R.id.tv_phone) EditText tvPhone;
    @BindView(R.id.btnSubmit) Button btnSubmit;

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
        }
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
