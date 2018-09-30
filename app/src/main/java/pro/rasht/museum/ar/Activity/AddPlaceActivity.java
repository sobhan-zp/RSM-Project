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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

    @BindView(R.id.textViewResponse)
    TextView textViewResponse;

    SavePref save;
    ProgressDialog dialog;
    ArrayList<String> city;
    ArrayAdapter adapter;

    ProgressDialog uploading;

    boolean uploadvideo;

    //choose image and video
    private static final String IMAGE_DIRECTORY = "/demonuts";

    private int GALLERY = 1, CAMERA = 2 , SELECT_VIDEO = 3;
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

                    uploadVideo(
                            etNamePlace.getText().toString(),
                            etDescPlace.getText().toString(),
                            etMobilePlace.getText().toString(),
                            etAddressPlace.getText().toString()
                    );
                }




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
        params.put("arimage", save.load(AppController.SAVE_IMAGE_URL,"null"));
        params.put("arvideo", save.load(AppController.SAVE_VIDEO_URL,"null"));

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
            selectedPath = getPath(selectedImageUri);
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
                addPlace(
                        "10",
                        "1",
                        save.load(AppController.SAVE_GEO,"0"),
                        name,
                        desc,
                        mobile,
                        add,
                        "http"
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

}
