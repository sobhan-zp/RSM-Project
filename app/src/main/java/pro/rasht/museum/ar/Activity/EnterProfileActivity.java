package pro.rasht.museum.ar.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import pro.rasht.museum.ar.Classes.CircularImageView;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;
import pro.rasht.museum.ar.network.CustomRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EnterProfileActivity extends AppCompatActivity {

    @BindView(R.id.enter_profile_image)
    CircularImageView enterProfileImage;
    @BindView(R.id.btn_next_enter_profile)
    Button btnNextEnterProfile;
    @BindView(R.id.et_name_profile)
    EditText etNameProfile;
    @BindView(R.id.et_family_profile)
    EditText etfamilyProfile;
    @BindView(R.id.et_state_profile)
    EditText etStateProfile;
    @BindView(R.id.et_city_profile)
    Spinner etCityProfile;
    @BindView(R.id.et_phone_profile)
    EditText etPhoneProfile;
    @BindView(R.id.et_email_profile)
    EditText etEmailProfile;

    SavePref save;
    ProgressDialog dialog;
    ArrayList<String> city;
    ArrayAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_profile);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        save = new SavePref(this);



        btnNextEnterProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(EnterProfileActivity.this, MainActivity.class));

                if (!isNetworkAvailable()) {
                    AppController.message(EnterProfileActivity.this, "لطفا اتصال به اینترنت خود را برسی کنید");
                    return;
                }


                if (etNameProfile.getText().toString().length() <= 1) {

                    AppController.message(EnterProfileActivity.this, "لطفا نام خود را وارد کنید");
                    etNameProfile.requestFocus();
                    return;

                } else if (etfamilyProfile.getText().toString().length() <= 1) {

                    AppController.message(EnterProfileActivity.this, "لطفا نام خانوادگی را وارد کنید");
                    etfamilyProfile.requestFocus();
                    return;

                } else if (etStateProfile.getText().toString().length() <= 1) {

                    AppController.message(EnterProfileActivity.this, "لطفا استان را وارد کنید");
                    etStateProfile.requestFocus();
                    return;


                /*} else if (!etPhoneProfile.getText().toString().matches("(\\+98|0)?9\\d{9}")) {


                    AppController.message(EnterProfileActivity.this, "لطفا شماره موبایل را درست وارد کنید");
                    etPhoneProfile.requestFocus();
                    return;*/


                } else if (etEmailProfile.getText().toString().length() > 0 && !AppController.isValidEmail(etEmailProfile.getText().toString())) {


                    AppController.message(EnterProfileActivity.this, "لطفا ایمیل را درست وارد کنید");
                    etEmailProfile.requestFocus();
                    return;

                } else {

                    save.save(AppController.SAVE_USER_Name, etNameProfile.getText().toString());
                    save.save(AppController.SAVE_USER_Family, etfamilyProfile.getText().toString());
                    save.save(AppController.SAVE_USER_STATE, etStateProfile.getText().toString());
                    save.save(AppController.SAVE_USER_CITY, city.get(etCityProfile.getSelectedItemPosition()));
                    save.save(AppController.SAVE_USER_MOBILE, etPhoneProfile.getText().toString());
                    save.save(AppController.SAVE_USER_EMAIL, etEmailProfile.getText().toString());



                    dialog.setMessage("ورود...");
                    dialog.show();

                    signup(
                            etNameProfile.getText().toString(),
                            etfamilyProfile.getText().toString(),
                            etPhoneProfile.getText().toString(),
                            String.valueOf(etCityProfile.getSelectedItemPosition()),
                            etStateProfile.getText().toString(),
                            etEmailProfile.getText().toString()
                    );
                }


            }
        });

        fillcity();
    }



    private void signup(String fname, String lname, String phone,String city ,  String state , String email) {

        Map<String, String> params = new HashMap<>();
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("phone", phone);
        params.put("city", city);
        params.put("state", state);
        params.put("email", email);



        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, AppController.URL_SIGNUP, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resp = response;
                //Log.d("TAG--------OK", resp.toString());

                try {
                    if (resp.getString("status").equals("200")) {

                        save.save(AppController.SAVE_LOGIN, "1");
                        save.save(AppController.SAVE_COMPLETE_PROFILE, "1");
                        //save.save(AppController.SAVE_USER_ID, resp.getString("id"));

                        AppController.message(EnterProfileActivity.this, "تکمیل اطلاعات");
                        startActivity(new Intent(EnterProfileActivity.this, MainActivity.class));
                        finish();

                    } else if (resp.getString("status").equals("403")) {
                        AppController.message(EnterProfileActivity.this, "شماره موبایل تکراری می باشد");
                    } else if (resp.getString("status").equals("402")) {
                        AppController.message(EnterProfileActivity.this, "خطا در سرور لطفا بعدا سعی کنید");
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
                AppController.message(EnterProfileActivity.this, "لطفا در زمان دیگری اقدام کنید");
                if (dialog.isShowing()) dialog.dismiss();
            }
        });
        jsonObjReq.setShouldCache(false);
        //myRequestQueue.getCache().clear();
        AppController.getInstance().addToRequestQueue(jsonObjReq, "REGISTER");

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



    private void fillcity() {
        city = new ArrayList<>();

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etCityProfile.setAdapter(adapter);

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


}
