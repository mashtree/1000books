package uxt6.psu.com.a1000books;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourProfileActivity extends AppBaseActivity {

    @BindView(R.id.iv_photo) de.hdodenhof.circleimageview.CircleImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_location) TextView tvLocation;
    @BindView(R.id.tv_phone) TextView tvPhone;
    @BindView(R.id.btn_edit) Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        ButterKnife.bind(this);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YourProfileActivity.this, UpdateProfileActivity.class);
                startActivityForResult(intent, UpdateProfileActivity.REQUEST_UPDATE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==UpdateProfileActivity.REQUEST_UPDATE){
            if(resultCode==UpdateProfileActivity.RESULT_UPDATE){
                //new LoadNoteAsync().execute();
                Toast.makeText(YourProfileActivity.this, getString(R.string.add_success), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
