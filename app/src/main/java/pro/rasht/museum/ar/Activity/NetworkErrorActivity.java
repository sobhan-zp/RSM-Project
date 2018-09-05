package pro.rasht.museum.ar.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.R;

public class NetworkErrorActivity extends AppCompatActivity {

    @BindView(R.id.tv_try_wifi_network)
    TextView tvTryWifiNetwork;
    @BindView(R.id.tv_try_data_network)
    TextView tvTryDataNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);
        ButterKnife.bind(this);


        tvTryWifiNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                Intent i = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(i);

            }
        });


        tvTryDataNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));

            }
        });


      /*  final boolean[] zp = new boolean[0];

        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {


            public void onTick(long millisUntilFinished) {

                    if (isNetworkAvailable()){

                        zp[0] = true;
                        Intent i = new Intent(NetworkErrorActivity.this , MainActivity.class);
                        startActivity(i);
                        finish();
                    }



            }

            public void onFinish() {
                *//*if (isNetworkAvailable()) {
                    Intent i = new Intent(NetworkErrorActivity.this , MainActivity.class);
                    startActivity(i);
                    finish();
                }*//*
            }

        }.start();*/

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
