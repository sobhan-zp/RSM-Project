package pro.rasht.museum.ar.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements ImageLoadingListener {

    private static final int REQUEST_CODE = 1234;
    SharedPreferences prefs;


    @BindView(R.id.btn_login_login)
    Button btnLoginLogin;


    private GoogleApiClient client;

    //background
    private KenBurnsView mImg;
    private ProgressBar mProgressBar;
    private ImageLoaderConfiguration config;
    private File cacheDir;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Start code for Intro App
        prefs = getSharedPreferences("pro.rasht.ar", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {

            prefs.edit().putBoolean("firstrun", false).commit();
        }
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // End code for Intro App



        // Start code for Background Page
        mImg = (KenBurnsView) findViewById(R.id.img);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);
        loadImage();
        // End code for Background Page


        btnLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , MainActivity.class));
            }
        });

    }


    // Start code for Background Page
    private void loadImage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String cacheDirName = "." + getString(R.string.app_name);
            cacheDir = new File(Environment.getExternalStorageDirectory(), cacheDirName);
        } else {
            cacheDir = getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .threadPoolSize(5)
                .build();

        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();

        imageLoader.init(config);

        imageLoader.displayImage("http://loper.ir/rasht-pro/bg_login.png", mImg, options, this);

    }


    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }


    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        Toast.makeText(getApplicationContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mProgressBar.setVisibility(View.GONE);
        mImg.setVisibility(View.VISIBLE);
    }


    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Toast.makeText(this, "Tutorial finished", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pro.rasht.ar/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pro.rasht.ar/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }
    // End code for Intro App


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



}
