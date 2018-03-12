package uxt6.psu.com.a1000books;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateProfileActivity extends AppCompatActivity {

    @BindView(R.id.iv_photo) de.hdodenhof.circleimageview.CircleImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.edt_location) EditText edtLocation;
    @BindView(R.id.edt_phone) EditText edtPhone;
    @BindView(R.id.btn_update) Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
    }
}
