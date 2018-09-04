package pro.rasht.museum.ar.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.Classes.CircularImageView;
import pro.rasht.museum.ar.Classes.RuntimePermissionHelper;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.Classes.Upload;
import pro.rasht.museum.ar.R;
import pro.rasht.museum.ar.network.AppController;
import pro.rasht.museum.ar.network.CustomRequest;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddPlaceActivity extends AppCompatActivity {

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
    @BindView(R.id.img_map_place)
    ImageView imgMapPlace;
    @BindView(R.id.btn_add_img_place)
    Button btnAddImgPlace;
    @BindView(R.id.img_ar_place)
    ImageView imgArPlace;
    @BindView(R.id.btn_add_video_place)
    Button btnAddVideoPlace;
    @BindView(R.id.video_ar_place)
    VideoView videoArPlace;
    @BindView(R.id.btn_controller_video)
    ImageView btnControllerVideo;

    SavePref save;
    ProgressDialog dialog;
    ArrayList<String> city;
    ArrayAdapter adapter;
    //choose image and video
    private static final String IMAGE_DIRECTORY = "/demonuts";

    private int GALLERY = 1, CAMERA = 2;
    private static final int SELECT_VIDEO = 3;
    private String selectedPath;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        ButterKnife.bind(this);
        save = new SavePref(this);
        dialog = new ProgressDialog(this);


        btnNextPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isNetworkAvailable()) {
                    AppController.message(AddPlaceActivity.this, "لطفا اتصال به اینترنت خود را برسی کنید");
                    return;
                }


                if (etNamePlace.getText().toString().length() <= 1) {

                    AppController.message(AddPlaceActivity.this, "لطفا نام مکان را وارد کنید");
                    etNamePlace.requestFocus();
                    return;

                } else if (etDescPlace.getText().toString().length() <= 1) {

                    AppController.message(AddPlaceActivity.this, "لطفا توضیحات خود را وارد کنید");
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


                uploadVideo();

            }
        });

        imgMapPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPlaceActivity.this, MapPlaceActivity.class);
                startActivity(i);
            }
        });
        RunTimePermission();
        addImageVideoAr();
        addVideoAr();
        loadimgGg();


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

                        AppController.message(AddPlaceActivity.this, "تکمیل اطلاعات");
                        startActivity(new Intent(AddPlaceActivity.this, BodyActivity.class));
                        finish();

                    } else if (resp.getString("status").equals("403")) {
                        AppController.message(AddPlaceActivity.this, "شماره موبایل تکراری می باشد");
                    } else if (resp.getString("status").equals("402")) {
                        AppController.message(AddPlaceActivity.this, "خطا در سرور لطفا بعدا سعی کنید");
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
                AppController.message(AddPlaceActivity.this, "لطفا در زمان دیگری اقدام کنید");
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


    private void RunTimePermission() {

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


    private void addImageVideoAr() {

        videoArPlace.setVisibility(View.GONE);
        btnControllerVideo.setVisibility(View.GONE);

        btnAddImgPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddPlaceActivity.this);
                String[] pictureDialogItems = {
                        "انتخاب از گالری",
                        "انتخاب از دوربین"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallary();
                                        break;
                                    case 1:
                                        takePhotoFromCamera();
                                        break;
                                }
                            }
                        });

                pictureDialog.show();


            }
        });

    }


    private void addVideoAr() {

        btnAddVideoPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddPlaceActivity.this);
                String[] pictureDialogItems = {
                        "انتخاب از گالری",};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        chooseVideo();
                                        break;

                                }
                            }
                        });

                pictureDialog.show();

            }
        });

    }


    private void loadimgGg() {

        try {

            if (!save.load("imgBg", "").equals("")) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeFile(save.load("imgBg", ""), options));
                imgArPlace.setBackgroundDrawable(background);
            }

        } catch (Exception e) {

        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(AddPlaceActivity.this, "عکس ذخیره شد", Toast.LENGTH_SHORT).show();


                    //blure Gallery
                    //bitmap = new BlureImage(bitmap , 1 , 6).getBitmap();
                    BitmapDrawable background = new BitmapDrawable(bitmap);
                    imgArPlace.setBackgroundDrawable(background);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPlaceActivity.this, "لغو شد", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            //blure Camera
            //thumbnail = new BlureImage(thumbnail , 1 , 6).getBitmap();
            BitmapDrawable background = new BitmapDrawable(thumbnail);
            imgArPlace.setBackgroundDrawable(background);
            saveImage(thumbnail);
            Toast.makeText(AddPlaceActivity.this, "عکس ذخیره شد", Toast.LENGTH_SHORT).show();

        } else if (requestCode == SELECT_VIDEO) {
            videoArPlace.setVisibility(View.VISIBLE);
            btnControllerVideo.setVisibility(View.VISIBLE);
            System.out.println("SELECT_VIDEO");
            Uri selectedImageUri = data.getData();
            Log.e("1--------------" , String.valueOf(selectedImageUri.getPath()));
            selectedPath = getPath(selectedImageUri);
            Log.e("2--------------" , String.valueOf(selectedPath));
            videoArPlace.setVideoPath(selectedPath);
        }
    }


    //Save Image To FIle
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::---->" + f.getAbsolutePath());

            save.save("imgBg", f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            Log.e("TAG", "ERROR::---->" + e1.getMessage());
            e1.printStackTrace();
        }

        return "";
    }

    //Upload Video To Server
    private void uploadVideo() {

        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(AddPlaceActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                //textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                //textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }


    //for video
    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }


    //Dialog Choose Gallery and Camera
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

}
