package pro.rasht.museum.ar.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.Adapter.Place_ExpandableRecyclerViewAdapter;
import pro.rasht.museum.ar.Classes.CircularImageView;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;
import pro.rasht.museum.ar.network.CustomRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlaceActivity extends AppCompatActivity {

    @BindView(R.id.enter_place_image)
    CircularImageView enterPlaceImage;
    @BindView(R.id.et_name_place)
    EditText etNamePlace;
    @BindView(R.id.et_desc_place)
    EditText etDescPlace;
    @BindView(R.id.et_address_place)
    EditText etAddressPlace;
    @BindView(R.id.et_mobile_place)
    EditText etMobilePlace;


    @BindView(R.id.btn_next_place)
    Button btnNextPlace;

    SavePref save;
    ProgressDialog dialog;
    ArrayList<String> city;
    ArrayAdapter adapter;
    @BindView(R.id.img_map_place)
    ImageView imgMapPlace;
    @BindView(R.id.rv_place)
    RecyclerView rvPlace;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        ButterKnife.bind(this);
        save = new SavePref(this);
        dialog = new ProgressDialog(this);

        btnNextPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isNetworkAvailable()) {
                    AppController.message(PlaceActivity.this, "لطفا اتصال به اینترنت خود را برسی کنید");
                    return;
                }


                if (etNamePlace.getText().toString().length() <= 1) {

                    AppController.message(PlaceActivity.this, "لطفا نام مکان را وارد کنید");
                    etNamePlace.requestFocus();
                    return;

                } else if (etDescPlace.getText().toString().length() <= 1) {

                    AppController.message(PlaceActivity.this, "لطفا توضیحات خود را وارد کنید");
                    etDescPlace.requestFocus();
                    return;

                } else {

                    save.save(AppController.SAVE_USER_Name, etNamePlace.getText().toString());
                    save.save(AppController.SAVE_USER_Family, etDescPlace.getText().toString());
                    save.save(AppController.SAVE_USER_CITY, etAddressPlace.getText().toString());
                    save.save(AppController.SAVE_USER_MOBILE, etMobilePlace.getText().toString());

                    dialog.setMessage("ورود...");
                    dialog.show();
                    signup(
                            etNamePlace.getText().toString(),
                            etDescPlace.getText().toString(),
                            etMobilePlace.getText().toString(),
                            etAddressPlace.getText().toString()
                            //String.valueOf(etCityPlace.getSelectedItemPosition()),
                    );
                }


            }
        });

        imgMapPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlaceActivity.this, MapPlaceActivity.class);
                startActivity(i);
            }
        });

        initiateExpander();

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void signup(String fname, String lname, String phone, String city) {

        Map<String, String> params = new HashMap<>();
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("phone", phone);
        params.put("city", city);


        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, AppController.URL_SIGNUP, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resp = response;
                //Log.d("TAG--------OK", resp.toString());

                try {
                    if (resp.getString("status").equals("200")) {

                        save.save(AppController.SAVE_LOGIN, "1");
                        //save.save(AppController.SAVE_USER_ID, resp.getString("id"));

                        AppController.message(PlaceActivity.this, "تکمیل اطلاعات");
                        startActivity(new Intent(PlaceActivity.this, BodyActivity.class));
                        finish();

                    } else if (resp.getString("status").equals("403")) {
                        AppController.message(PlaceActivity.this, "شماره موبایل تکراری می باشد");
                    } else if (resp.getString("status").equals("402")) {
                        AppController.message(PlaceActivity.this, "خطا در سرور لطفا بعدا سعی کنید");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (dialog.isShowing()) dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG--------Error", "Error: " + error.getMessage());
                AppController.message(PlaceActivity.this, "لطفا در زمان دیگری اقدام کنید");
                if (dialog.isShowing()) dialog.dismiss();
            }
        });
        jsonObjReq.setShouldCache(false);
        //myRequestQueue.getCache().clear();
        AppController.getInstance().addToRequestQueue(jsonObjReq, "REGISTER");

    }


    private void fillcity() {
        city = new ArrayList<>();

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //tCityPlace.setAdapter(adapter);

        city.add("آستارا");
        city.add("آستانه اشرفیه");
        city.add("املش");
        city.add("بندر انزلی");
        city.add("تالش");
        city.add("رشت");
        city.add("رضوان‌شهر");
        city.add("رودسر");
        city.add("رودبار");
        city.add("شفت");
        city.add("صومعه‌سرا");
        city.add("فومن");
        city.add("کوچصفهان");
        city.add("لاهیجان");
        city.add("لنگرود");
        city.add("ماسال");
        city.add("آذربايجان شرقي");
        city.add("آذربايجان غربي");
        city.add("اردبيل");
        city.add("اصفهان");
        city.add("البرز");
        city.add("ايلام");
        city.add("بوشهر");
        city.add("تهران");
        city.add("چهارمحال بختياري");
        city.add("خراسان جنوبي");
        city.add("خراسان رضوي");
        city.add("خراسان شمالي");
        city.add("خوزستان");
        city.add("زنجان");
        city.add("سمنان");
        city.add("سيستان و بلوچستان");
        city.add("فارس");
        city.add("قزوين");
        city.add("قم");
        city.add("كردستان");
        city.add("كرمان");
        city.add("كرمانشاه");
        city.add("كهكيلويه و بويراحمد");
        city.add("گلستان");
        city.add("گيلان");
        city.add("لرستان");
        city.add("مازندران");
        city.add("مركزي");
        city.add("هرمزگان");
        city.add("همدان");
        city.add("يزد");

        adapter.notifyDataSetChanged();
    }


    //Expander
    private void initiateExpander() {

        ArrayList<String> parentList = new ArrayList<>();
        ArrayList<ArrayList> childListHolder = new ArrayList<>();

        parentList.add("Fruits & Vegetables");
        parentList.add("Beverages & Health");
        parentList.add("Home & Kitchen");

        ArrayList<String> childNameList = new ArrayList<>();
        childNameList.add("Apple");
        childNameList.add("Mango");
        childNameList.add("Banana");

        childListHolder.add(childNameList);

        childNameList = new ArrayList<>();
        childNameList.add("Red bull");
        childNameList.add("Maa");
        childNameList.add("Horlicks");

        childListHolder.add(childNameList);

        childNameList = new ArrayList<>();
        childNameList.add("Knife");
        childNameList.add("Vessels");
        childNameList.add("Spoons");

        childListHolder.add(childNameList);

        Place_ExpandableRecyclerViewAdapter expandableCategoryRecyclerViewAdapter =
                new Place_ExpandableRecyclerViewAdapter(getApplicationContext(), parentList,
                        childListHolder);

        rvPlace.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvPlace.setAdapter(expandableCategoryRecyclerViewAdapter);
    }


}
