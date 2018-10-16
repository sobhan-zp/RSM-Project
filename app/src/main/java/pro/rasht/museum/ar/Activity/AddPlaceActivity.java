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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.rasht.museum.ar.Classes.CircularImageView;
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
    @BindView(R.id.sw_ar_place)
    SwitchButton swArPlace;


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
    @BindView(R.id.view_line_place)
    View viewLinePlace;

    @BindView(R.id.fl_img_place)
    FrameLayout flImgPlace;
    @BindView(R.id.fl_video_place)
    FrameLayout flVideoPlace;


    @BindView(R.id.textViewResponse)
    TextView textViewResponse;
    @BindView(R.id.cv_info2_enter_profile)
    CardView cvInfo2EnterProfile;

    SavePref save;
    ProgressDialog dialog;
    ArrayList<String> city;
    ArrayAdapter adapter;

    ProgressDialog uploading;

    boolean uploadvideo;


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 99;
    private static final int PICK_IMAGE_REQUEST_PROFILE = 100;
    Bitmap bitmap;
    Bitmap bitmap2;
    String myurl = "http://192.168.1.253/myimage/Upload.php";

    //choose image and video
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2, SELECT_VIDEO = 3;
    private String selectedPath;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        save = new SavePref(this);

        //empty point map ()if else
        save.save(AppController.SAVE_USER_GEO , "0");


        ButterKnife.bind(this);
        save = new SavePref(this);
        dialog = new ProgressDialog(this);

        swArPlace.setShadowEffect(true);//disable shadow effect
        swArPlace.setEnabled(true);//disable button
        swArPlace.setEnableEffect(true);//disable the switch animation

        flImgPlace.setVisibility(View.GONE);
        flVideoPlace.setVisibility(View.GONE);
        viewLinePlace.setVisibility(View.GONE);

        swArPlace.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                if (swArPlace.isChecked()) {
                    flImgPlace.setVisibility(View.VISIBLE);
                    flVideoPlace.setVisibility(View.VISIBLE);
                    viewLinePlace.setVisibility(View.VISIBLE);
                } else {
                    flImgPlace.setVisibility(View.GONE);
                    flVideoPlace.setVisibility(View.GONE);
                    viewLinePlace.setVisibility(View.GONE);
                }
            }
        });


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

                } else if (etDescPlace.getText().toString().length() <= 1) {

                    AppController.message(AddPlaceActivity.this, "لطفا شماره موبایل خود را وارد کنید");
                    etMobilePlace.requestFocus();
                    return;

                } else if (etDescPlace.getText().toString().length() <= 1) {

                    AppController.message(AddPlaceActivity.this, "لطفا آدرس خود را وارد کنید");
                    etAddressPlace.requestFocus();
                    return;

                } else {

                    uploadVideo(
                            etNamePlace.getText().toString(),
                            etDescPlace.getText().toString(),
                            etMobilePlace.getText().toString(),
                            etAddressPlace.getText().toString()
                    );

                    //uploadImage();
                }


            }
        });

        enterPlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddPlaceActivity.this);
                String[] pictureDialogItems = {
                        "انتخاب از گالری"
                };
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallaryProfile();
                                        break;
                                }
                            }
                        });

                pictureDialog.show();

            }
        });

        imgMapPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPlaceActivity.this, MapPlaceActivity.class);
                startActivity(i);
            }
        });


        addImageVideoAr();
        addVideoAr();
        loadimgGg();
        loadVideo();


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Toast.makeText(this, "عکس انتخاب شد", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "" + bitmap2, Toast.LENGTH_LONG).show();
                imgArPlace.setImageBitmap(bitmap2);


                BitmapDrawable background = new BitmapDrawable(bitmap2);
                //imgArPlace.setBackgroundDrawable(background);


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST_PROFILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath2 = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath2);
                //Toast.makeText(this, "عکس انتخاب شد", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "" + bitmap, Toast.LENGTH_LONG).show();

                enterPlaceImage.setImageBitmap(bitmap);
                BitmapDrawable background = new BitmapDrawable(bitmap);
                enterPlaceImage.setBackgroundDrawable(background);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_VIDEO) {
            videoArPlace.setVisibility(View.VISIBLE);
            btnControllerVideo.setVisibility(View.VISIBLE);
            System.out.println("SELECT_VIDEO");
            Uri selectedImageUri = data.getData();
            try {
                selectedPath = getPath(selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }

            videoArPlace.setVideoPath(selectedPath);

            btnControllerVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (videoArPlace.isPlaying()) {
                        btnControllerVideo.setImageResource(R.drawable.ic_play);
                        videoArPlace.pause();
                    } else {

                        btnControllerVideo.setImageResource(R.drawable.ic_pause);
                        videoArPlace.start();
                    }
                }
            });


            videoArPlace.start();
        }


    }


    //Netword is True | False
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //open dialog afret click on btn image ar
    private void addImageVideoAr() {

        videoArPlace.setVisibility(View.GONE);
        btnControllerVideo.setVisibility(View.GONE);

        btnAddImgPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddPlaceActivity.this);
                String[] pictureDialogItems = {
                        "انتخاب از گالری"
                };
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //choosePhotoFromGallary();
                                        choosePhotoFromGallary();
                                        break;
                                    case 1:
                                        //takePhotoFromCamera();
                                        break;
                                }
                            }
                        });

                pictureDialog.show();


            }
        });

    }


    //open dialog afret click on btn video ar
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


    //load image proflie in image view
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
        try {

            if (!save.load("imgBgProfile", "").equals("")) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeFile(save.load("imgBgProfile", ""), options));
                enterPlaceImage.setBackgroundDrawable(background);

            }
        } catch (Exception e) {

        }
    }

    //load video proflie in video view
    private void loadVideo() {
        try {

            if (!save.load("vid", "").equals("")) {

                videoArPlace.setVisibility(View.VISIBLE);
                btnControllerVideo.setVisibility(View.VISIBLE);
                videoArPlace.setVideoPath(save.load("vid", ""));
                videoArPlace.start();
            }
        } catch (Exception e) {

        }
    }


    //Upload Video To Server
    private void uploadVideo(String name, String desc, String mobile, String add) {

        class UploadVideo extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(AddPlaceActivity.this, "در حال ارسال رسانه و ارسال", "لطفا صبر کنید...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //uploading.dismiss();


                save.save("vid", selectedPath);
                save.save(AppController.SAVE_VIDEO_URL, s);
                uploadvideo = true;

                //save.load(AppController.SAVE_USER_ID,"0"),

                uploadImageAr(
                        name,
                        desc,
                        mobile,
                        add
                );
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

    //Upload Image To Server
    public void uploadImageAr(String name, String desc, String mobile, String add) {


        RequestQueue requestQueue = Volley.newRequestQueue(AddPlaceActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                //Log.i("Myresponse",""+s);
                //Toast.makeText(AddPlaceActivity.this, ""+s, Toast.LENGTH_LONG).show();

                save.save(AppController.SAVE_IMAGE_URL, s);

                uploadImageProfile(
                        name,
                        desc,
                        mobile,
                        add
                );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart", "" + error);
                Toast.makeText(AddPlaceActivity.this, "" + error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                String images = getStringImage(bitmap2);
                Log.i("Mynewsam", "" + images);
                param.put("image", images);
                return param;
            }
        };

        requestQueue.add(stringRequest);


    }

    //Upload Image Model_Gallery To Server
    public void uploadImageProfile(String name, String desc, String mobile, String add) {


        RequestQueue requestQueue = Volley.newRequestQueue(AddPlaceActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                //Log.i("Myresponse",""+s);
                //Toast.makeText(AddPlaceActivity.this, ""+s, Toast.LENGTH_LONG).show();

                //save.save(AppController.SAVE_IMAGE_URL , s);

                addPlace(
                        save.load(AppController.SAVE_USER_ID, "10"),
                        "1",
                        save.load(AppController.SAVE_USER_GEO, "0"),
                        name,
                        desc,
                        mobile,
                        add,
                        s
                );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart", "" + error);
                Toast.makeText(AddPlaceActivity.this, "" + error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                String images = getStringImage(bitmap);
                Log.i("Mynewsam", "" + images);
                param.put("image", images);
                return param;
            }
        };

        requestQueue.add(stringRequest);


    }

    //add place point To server
    private void addPlace(String idu, String isar, String geo, String name,
                          String des, String mobile, String add, String img) {

        Map<String, String> params = new HashMap<>();
        params.put("idu", idu);
        params.put("isar", isar);
        params.put("geo", geo);
        params.put("name", name);
        params.put("des", des);
        params.put("mobile", mobile);
        params.put("add", add);
        params.put("img", img);
        params.put("arimage", save.load(AppController.SAVE_IMAGE_URL, "null"));
        params.put("arvideo", save.load(AppController.SAVE_VIDEO_URL, "null"));

        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, AppController.URL_POINT_ADD, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject resp = response;
                //Log.e("TAG--------OK", resp.toString());

                try {
                    if (resp.getString("status").equals("200")) {

                        AppController.message(AddPlaceActivity.this, "انتخاب شما ارسال شد ", Toast.LENGTH_LONG);
                        uploadvideo = false;

                    } else if (resp.getString("status").equals("402")) {
                        AppController.message(AddPlaceActivity.this, "خطای در ذخیره اطلاعات بوجود آمده است");
                    } else {
                        AppController.message(AddPlaceActivity.this, "لطفا مجدد سعی کنید");
                    }

                    uploading.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (uploading.isShowing()) uploading.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG--------Error", "Error: " + error.getMessage());
                AppController.message(AddPlaceActivity.this, "لطفا در زمان دیگری اقدام کنید");
                if (uploading.isShowing()) uploading.dismiss();
            }
        });
        jsonObjReq.setShouldCache(false);
        //myRequestQueue.getCache().clear();
        AppController.getInstance().addToRequestQueue(jsonObjReq, "addPlace");
    }


    //compress image
    public String getStringImage(Bitmap bitmap) {
        Log.i("MyHitesh", "" + bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
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

        String path = "";
        try {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.close();

        return path;
    }


    //Dialog Choose Gallery and Camera
    private void choosePhotoFromGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void choosePhotoFromGallaryProfile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_PROFILE);
    }

    //Dialog Choose Gallery and Camera
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


}
