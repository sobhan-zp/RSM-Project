package pro.rasht.museum.ar.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.Classes.CircularImageView;
import pro.rasht.museum.ar.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EnterProfileActivity extends AppCompatActivity {

    @BindView(R.id.enter_profile_image)
    CircularImageView enterProfileImage;
    @BindView(R.id.btn_next_enter_profile)
    Button btnNextEnterProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_profile);
        ButterKnife.bind(this);


        btnNextEnterProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EnterProfileActivity.this , BodyActivity.class));
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
