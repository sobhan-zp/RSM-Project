package pro.rasht.museum.ar.Activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.Classes.ImageUtil;
import pro.rasht.museum.ar.Classes.RuntimePermissionHelper;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.Fragment.Items_Viewpager_market;
import pro.rasht.museum.ar.Fragment.NonSwipeableViewPager;
import pro.rasht.museum.ar.Model.HoloModel;
import pro.rasht.museum.ar.Model.PointModel;
import pro.rasht.museum.ar.Model.Target;
import pro.rasht.museum.ar.Model.VrModel;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.img_profile_body)
    ImageView imgProfileBody;
    @BindView(R.id.tv_profile_body)
    TextView tvProfileBody;
    @BindView(R.id.img_music_body)
    ImageView imgMusicBody;
    @BindView(R.id.tv_music_body)
    TextView tvMusicBody;
    @BindView(R.id.img_gallery_body)
    ImageView imgGalleryBody;
    @BindView(R.id.tv_gallery_body)
    TextView tvGalleryBody;
    @BindView(R.id.img_home_body)
    ImageView imgHomeBody;
    @BindView(R.id.tv_home_body)
    TextView tvHomeBody;
    @BindView(R.id.viewpager)
    NonSwipeableViewPager pager;

    Context context;
    SavePref save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runTimePermission();
        save = new SavePref(this);
        ButterKnife.bind(this);
        NetworkError();

        loadPoint();
        loadAr();
        loadVr();
        loadHolo();


        //tak,il etelaat
        if (save.load(AppController.SAVE_COMPLETE_PROFILE,"0") == "0"){

            imgProfileBody.setColorFilter(MainActivity.this.getResources().getColor(R.color.material_red_A700));


        }else{

            imgProfileBody.setColorFilter(MainActivity.this.getResources().getColor(R.color.black));

        }


        pager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        Items_Viewpager_market adapter_articles = new Items_Viewpager_market(getSupportFragmentManager());
        pager.setAdapter(adapter_articles);
        pager.setCurrentItem(0, false);
        pager.setOffscreenPageLimit(0);


        //defult property View
        tvProfileBody.setVisibility(View.VISIBLE);






    }



    public void onCLickHome(View v) {
        setVisibiltyBottomBar(tvHomeBody);
        Animation animation = new TranslateAnimation(0, 0, 15, 0);
        Animation animation1 = new TranslateAnimation(0, 0, 70, 0);
        animation.setDuration(300);
        animation1.setDuration(200);
        imgHomeBody.startAnimation(animation);
        tvHomeBody.startAnimation(animation1);

        pager.setCurrentItem(0);
    }

    public void onCLickGallery(View v) {
        setVisibiltyBottomBar(tvGalleryBody);
        Animation animation = new TranslateAnimation(0, 0, 15, 0);
        Animation animation1 = new TranslateAnimation(0, 0, 70, 0);
        animation.setDuration(300);
        animation1.setDuration(200);
        imgGalleryBody.startAnimation(animation);
        tvGalleryBody.startAnimation(animation1);

        pager.setCurrentItem(1);
    }

    public void onCLickMusic(View v) {
        setVisibiltyBottomBar(tvMusicBody);
        Animation animation = new TranslateAnimation(0, 0, 15, 0);
        Animation animation1 = new TranslateAnimation(0, 0, 70, 0);
        animation.setDuration(300);
        animation1.setDuration(200);
        imgMusicBody.startAnimation(animation);
        tvMusicBody.startAnimation(animation1);

        pager.setCurrentItem(2);
    }

    public void onCLickProfile(View v) {
        setVisibiltyBottomBar(tvProfileBody);
        Animation animation = new TranslateAnimation(0, 0, 15, 0);
        Animation animation1 = new TranslateAnimation(0, 0, 70, 0);
        animation.setDuration(300);
        animation1.setDuration(200);
        imgProfileBody.startAnimation(animation);
        tvProfileBody.startAnimation(animation1);

        pager.setCurrentItem(4);
    }



    //permission method
    public void runTimePermission(){

        if (Build.VERSION.SDK_INT >= 23) {
            RuntimePermissionHelper runtimePermissionHelper = RuntimePermissionHelper.getInstance(this);
            if (runtimePermissionHelper.isAllPermissionAvailable()) {
                // All permissions available. Go with the flow
            } else {
                // Few permissions not granted. Ask for ungranted permissions
                runtimePermissionHelper.setActivity(this);
                runtimePermissionHelper.requestPermissionsIfDenied();
            }
        } else {
            // SDK below API 23. Do nothing just go with the flow.
        }


    }




    //method set visible bottom bar
    public void setVisibiltyBottomBar(TextView tv) {
        tvHomeBody.setVisibility(View.GONE);
        tvGalleryBody.setVisibility(View.GONE);
        tvMusicBody.setVisibility(View.GONE);
        tvProfileBody.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void loadPoint() {
        JsonArrayRequest req = new JsonArrayRequest(AppController.URL_POINT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("TAG---------OK", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);

                                PointModel holoModel = new PointModel(
                                        object.getString("id"),
                                        object.getString("id_user"),
                                        object.getString("isar"),
                                        object.getString("isvr"),
                                        object.getString("isholo"),
                                        object.getString("geo"),
                                        object.getString("fullname"),
                                        object.getString("description"),
                                        object.getString("phone"),
                                        object.getString("address"),
                                        object.getString("image"),
                                        object.getString("voice"),
                                        object.getString("ishistory")
                                );


                                AppController.POINTMODEL.add(holoModel);
                            }
                            AppController.POINTMODEL_NUMBERS = response.length();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG------------Error", "Error: " + error.getMessage());
            }
        });
        req.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(req, "loadVr");



        Toast.makeText(MainActivity.this, ""+AppController.POINTMODEL.size(), Toast.LENGTH_SHORT).show();
    }
    private void loadAr() {
        JsonArrayRequest req = new JsonArrayRequest(AppController.URL_AR,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("TAG---------OK", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);
                                String url = object.getString("url");

                                Target target = new Target(
                                        String.valueOf(i),
                                        url.substring( url.lastIndexOf('/')+1, url.length() ),
                                        object.getString("url"),
                                        object.getString("value")
                                );

                                new ImageUtil(MainActivity.this, target.getUrl(), target.getName());

                                AppController.TARGET.add(target);
                            }
                            AppController.TARGET_NUMBERS = response.length();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG------------Error", "Error: " + error.getMessage());
            }
        });
        req.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(req, "loadAr");
    }
    private void loadVr() {
        JsonArrayRequest req = new JsonArrayRequest(AppController.URL_VR,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("TAG---------OK", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);

                                VrModel vrModel = new VrModel(
                                        object.getString("id"),
                                        object.getString("id_user"),
                                        object.getString("id_point"),
                                        object.getString("url")
                                );

                                AppController.VRMODEL.add(vrModel);
                            }
                            AppController.VRMODEL_NUMBERS = response.length();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG------------Error", "Error: " + error.getMessage());
            }
        });
        req.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(req, "loadVr");
    }
    private void loadHolo() {
        JsonArrayRequest req = new JsonArrayRequest(AppController.URL_HOLO,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("TAG---------OK", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = response.getJSONObject(i);

                                HoloModel holoModel = new HoloModel(
                                        object.getString("id"),
                                        object.getString("id_user"),
                                        object.getString("id_point"),
                                        object.getString("url")
                                );

                                AppController.HOLOMODEL.add(holoModel);
                            }
                            AppController.HOLOMODEL_NUMBERS = response.length();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG------------Error", "Error: " + error.getMessage());
            }
        });
        req.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(req, "loadVr");
    }




    public void NetworkError(){

        if (!isNetworkAvailable()) {
            AppController.message(MainActivity.this, "لطفا اتصال به اینترنت خود را برسی کنید");
            Intent i = new Intent(MainActivity.this , NetworkErrorActivity.class);
            startActivity(i);
            return;
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

