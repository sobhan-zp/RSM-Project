package pro.rasht.museum.ar;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import pro.rasht.museum.ar.ar.ArBeyondarGLSurfaceView;
import pro.rasht.museum.ar.ar.ArFragmentSupport;
import pro.rasht.museum.ar.ar.OnTouchBeyondarViewListenerMod;
import pro.rasht.museum.ar.network.Model.BaseGeo;
import pro.rasht.museum.ar.network.PlaceResponse;
import pro.rasht.museum.ar.network.PoiResponse;
import pro.rasht.museum.ar.network.RetrofitInterface;
import pro.rasht.museum.ar.network.poi.Geometry;
import pro.rasht.museum.ar.network.poi.Northeast;
import pro.rasht.museum.ar.network.poi.OpeningHours;
import pro.rasht.museum.ar.network.poi.Photo;
import pro.rasht.museum.ar.network.poi.Result;
import pro.rasht.museum.ar.network.poi.Southwest;
import pro.rasht.museum.ar.network.poi.Viewport;
import pro.rasht.museum.ar.utils.UtilsCheck;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Amal Krishnan on 10-04-2017.
 */

public class PoiBrowserActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks
    ,GoogleApiClient.OnConnectionFailedListener,OnClickBeyondarObjectListener,
        OnTouchBeyondarViewListenerMod{

    private final static String TAG="PoiBrowserActivity";

    private TextView textView;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LayoutInflater layoutInflater;
    private ArFragmentSupport arFragmentSupport;
    private World world;
    private List<BaseGeo> baseGeoList = new ArrayList<>();

    @BindView(R.id.poi_place_detail)
    CardView poi_cardview;
    @BindView(R.id.poi_place_close_btn)
    ImageButton poi_cardview_close_btn;
    @BindView(R.id.poi_place_name)
    TextView poi_place_name;
    @BindView(R.id.poi_place_address)
    TextView poi_place_addr;
    @BindView(R.id.poi_place_image)
    ImageView poi_place_image;
    @BindView(R.id.poi_place_ar_direction)
    Button poi_place_ar_btn;
    @BindView(R.id.poi_place_maps_direction)
    Button poi_place_maps_btn;
    @BindView(R.id.poi_brwoser_progress)
    ProgressBar poi_browser_progress;
    @BindView(R.id.seekBar)
    SeekBar seekbar;
    @BindView(R.id.seekbar_cardview)
    CardView seekbar_cardview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_browser);
        ButterKnife.bind(this);

        seekbar_cardview.setVisibility(View.GONE);
        poi_browser_progress.setVisibility(View.GONE);
        poi_cardview.setVisibility(View.GONE);

        if(!UtilsCheck.isNetworkConnected(this)){
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.poi_layout),
                    getString(R.string.internet_on), Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }

        arFragmentSupport = (ArFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.poi_cam_fragment);
        arFragmentSupport.setOnClickBeyondarObjectListener(this);
        arFragmentSupport.setOnTouchBeyondarViewListener(this);


        textView=(TextView) findViewById(R.id.loading_text);

        Set_googleApiClient(); //Sets the GoogleApiClient

        poi_cardview_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekbar_cardview.setVisibility(View.VISIBLE);
                poi_cardview.setVisibility(View.GONE);
                poi_place_image.setImageResource(android.R.color.transparent);
                poi_place_name.setText(" ");
                poi_place_addr.setText(" ");
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(i==0){
                    Poi_list_call(300);
                }else{
                    Poi_list_call((i+1)*300);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress()==0){
                    Toast.makeText(PoiBrowserActivity.this, getString(R.string.redius_300_poiActivity), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PoiBrowserActivity.this, getString(R.string.redius_poiActivity)+(seekBar.getProgress()+1)*300+ getString(R.string.meters_poiActivity), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void Poi_list_call(int radius){
        poi_browser_progress.setVisibility(View.VISIBLE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<PoiResponse> call = apiService.listPOI(String.valueOf(mLastLocation.getLatitude())+","+
                String.valueOf(mLastLocation.getLongitude()),radius,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<PoiResponse>() {
            @Override
            public void onResponse(Call<PoiResponse> call, Response<PoiResponse> response) {

                poi_browser_progress.setVisibility(View.GONE);
                seekbar_cardview.setVisibility(View.VISIBLE);

                List<Result> poiResult=response.body().getResults();

                Log.e("MAZIAR----------------" , "------------------------------------------------");



               /* for (int i = 0; i < poiResult.size(); i++) {
                    Result result = poiResult.get(i);
                    Log.e("DETAIL----" , "" + result.getName());
                }*/


                poiResult.clear();


                Result result = new Result();
                Geometry geometry = new Geometry(
                        new pro.rasht.museum.ar.network.poi.Location(37.283696f,49.571167f),
                        new Viewport(
                                new Northeast(
                                        (float) (49.573973f + (radius/Math.abs(Math.cos(Math.toRadians(37.283696f)))*111)),
                                        (37.283696f + (radius/111))),
                                new Southwest(
                                        (float) (49.573973f - (radius/Math.abs(Math.cos(Math.toRadians(37.283696f)))*111)),
                                        (37.283696f - (radius/111)))
                        )
                );

                List<Photo> photoList = new ArrayList<>();
                List<String> htmlList = new ArrayList<>();
                htmlList.add("<a href=\"https://maps.google.com/maps/contrib/109471291971103535103/photos\">Naser Tavala</a>");
                photoList.add(new Photo(1152,
                        htmlList,
                        "https://www.codeproject.com/KB/GDI-plus/ImageProcessing2/flip.jpg",
                        2048));

                List<String> typeList = new ArrayList<>();
                typeList.add("local_government_office");
                typeList.add("point_of_interest");
                typeList.add("establishment");


                result.setGeometry(geometry);
                result.setIcon("https://maps.gstatic.com/mapfiles/place_api/icons/civic_building-71.png");
                result.setId("c49dcd1fc8bad9bd06ce617826d2279aac62165e");
                result.setName("اداره کل هواشناسی استان گیلان");
                result.setOpeningHours(new OpeningHours(true));
                result.setPhotos(photoList);

                result.setRating(3.9f);
                result.setReference("https://www.codeproject.com/KB/GDI-plus/ImageProcessing2/flip.jpg");
                result.setScope("GOOGLE");
                result.setTypes(typeList);
                result.setVicinity("Rasht, رشت .بلوار معلم, Moalem Boulevard");


                result.setPlaceId("0");
                result.setGeometry(geometry);
                poiResult.add(result);










                Result result1 = new Result();

                Geometry geometry1 = new Geometry(
                        new pro.rasht.museum.ar.network.poi.Location(37.282735f, 49.573973f),
                        new Viewport(
                                new Northeast(
                                        (float) (49.573973f + (radius/Math.abs(Math.cos(Math.toRadians(37.282735f)))*111)),
                                        (37.282735f + (radius/111))),
                                new Southwest(
                                        (float) (49.573973f - (radius/Math.abs(Math.cos(Math.toRadians(37.282735f)))*111)),
                                        (37.282735f - (radius/111)))
                        )
                );

                htmlList.add("<a href=\"https://maps.google.com/maps/contrib/109471291971103535103/photos\">Naser Tavala</a>");
                photoList.add(new Photo(1152,
                        htmlList,
                        "https://www.codeproject.com/KB/GDI-plus/ImageProcessing2/flip.jpg",
                        2048));

                typeList.add("local_government_office");
                typeList.add("point_of_interest");
                typeList.add("establishment");


                result1.setGeometry(geometry);
                result1.setIcon("https://maps.gstatic.com/mapfiles/place_api/icons/civic_building-71.png");
                result1.setId("c49dcd1fc8bad9bd06ce617826d2279aac62165e");
                result1.setName("اداره کل هواشناسی استان گیلان");
                result1.setOpeningHours(new OpeningHours(true));
                result1.setPhotos(photoList);

                result1.setRating(3.9f);
                result1.setReference("https://www.codeproject.com/KB/GDI-plus/ImageProcessing2/flip.jpg");
                result1.setScope("GOOGLE");
                result1.setTypes(typeList);
                result1.setVicinity("Rasht, رشت .بلوار معلم, Moalem Boulevard");


                result1.setPlaceId("1");
                result1.setGeometry(geometry1);
                poiResult.add(result1);












                BaseGeo baseGeo = new BaseGeo();
                baseGeo.setPlaceid("ChIJS54XFpzYH0ARMakgPC8CIfw");
                baseGeo.setName("نام");
                baseGeo.setDetail("جزِیات");
                baseGeo.setAddress("آدرس");
                baseGeo.setImg("https://www.codeproject.com/KB/GDI-plus/ImageProcessing2/flip.jpg");

                baseGeoList.add(baseGeo);

                BaseGeo baseGeo1 = new BaseGeo();
                baseGeo1.setPlaceid("ChIJS54XFpzYH0ARMakgPC8CIfc");
                baseGeo1.setName("نام 2");
                baseGeo1.setDetail("جزِیات 2");
                baseGeo1.setAddress("آدرس 2");

                baseGeo1.setImg("https://titan.uio.no/sites/default/files/thumbnails/image/as17-148-22727.jpg");

                baseGeoList.add(baseGeo1);


                Log.e("MAZIAR----------------" , "------------------------------------------------");

                Configure_AR(poiResult);
            }

            @Override
            public void onFailure(Call<PoiResponse> call, Throwable t) {
                poi_browser_progress.setVisibility(View.GONE);
            }
        });

    }

    void Poi_details_call(String placeid){

        poi_browser_progress.setVisibility(View.VISIBLE);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<PlaceResponse> call = apiService.getPlaceDetail(baseGeoList.get(Integer.parseInt(placeid)).getPlaceid(),
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {

                seekbar_cardview.setVisibility(View.GONE);
                poi_cardview.setVisibility(View.VISIBLE);
                poi_browser_progress.setVisibility(View.GONE);

                final pro.rasht.museum.ar.network.place.Result result=response.body().getResult();

                poi_place_name.setText(baseGeoList.get(Integer.parseInt(placeid)).getName());
                poi_place_addr.setText(baseGeoList.get(Integer.parseInt(placeid)).getAddress());

                Log.e("TAG-----------",baseGeoList.get(Integer.parseInt(placeid)).getImg());

                poi_place_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(PoiBrowserActivity.this).load(baseGeoList.get(Integer.parseInt(placeid)).getImg()).into(poi_place_image);



                poi_place_maps_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent;
                        try{
                            Uri.Builder builder = new Uri.Builder();
                            builder.scheme("http")
                                    .authority("maps.google.com")
                                    .appendPath("maps")
                                    .appendQueryParameter("saddr", mLastLocation.getLatitude()+","+mLastLocation.getLongitude())
                                    .appendQueryParameter("daddr",result.getGeometry().getLocation().getLat()+","+
                                                    result.getGeometry().getLocation().getLng());

                            intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse( builder.build().toString()));
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            Log.d(TAG, "onClick: mapNav Exception caught");
                            Toast.makeText(PoiBrowserActivity.this, getString(R.string.error_openmap_poiActivity), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                poi_place_ar_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(PoiBrowserActivity.this,ArCamActivity.class);

                        try {
                            intent.putExtra("SRC", "Current Location");
                            intent.putExtra("DEST",  result.getGeometry().getLocation().getLat()+","+
                                    result.getGeometry().getLocation().getLng());
                            intent.putExtra("SRCLATLNG",  mLastLocation.getLatitude()+","+mLastLocation.getLongitude());
                            intent.putExtra("DESTLATLNG", result.getGeometry().getLocation().getLat()+","+
                                    result.getGeometry().getLocation().getLng());
                            startActivity(intent);
                            finish();
                        }catch (NullPointerException npe){
                            Log.d(TAG, "onClick: The IntentExtras are Empty");
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                poi_browser_progress.setVisibility(View.GONE);
            }
        });

    }

    public class PoiPhotoAsync extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            poi_place_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            poi_place_image.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];

            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private void Configure_AR(List<Result> pois){

        layoutInflater=getLayoutInflater();

        world=new World(getApplicationContext());
        world.setGeoPosition(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        world.setDefaultImage(R.drawable.ar_sphere_default);

        arFragmentSupport.getGLSurfaceView().setPullCloserDistance(25);

        GeoObject geoObjects[]=new GeoObject[pois.size()];

        for(int i=0;i<pois.size();i++) {
            GeoObject poiGeoObj=new GeoObject(1000*(i+1));
            //ArObject2 poiGeoObj=new ArObject2(1000*(i+1));

//            poiGeoObj.setImageUri(getImageUri(this,textAsBitmap(pois.get(i).getName(),10.0f, Color.WHITE)));
            poiGeoObj.setGeoPosition(pois.get(i).getGeometry().getLocation().getLat(),
                    pois.get(i).getGeometry().getLocation().getLng());
            poiGeoObj.setName(pois.get(i).getPlaceId());
            //poiGeoObj.setPlaceId(pois.get(0).getPlaceId());

            //Bitmap bitmap=textAsBitmap(pois.get(i).getName(),30.0f,Color.WHITE);

            Bitmap snapshot = null;
            View view= getLayoutInflater().inflate(R.layout.poi_container,null);
            TextView name= (TextView)view.findViewById(R.id.poi_container_name);
            TextView dist= (TextView)view.findViewById(R.id.poi_container_dist);
            ImageView icon=(ImageView)view.findViewById(R.id.poi_container_icon);

            name.setText(pois.get(i).getName());
            String distance=String.valueOf((SphericalUtil.computeDistanceBetween(
                    new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                    new LatLng(pois.get(i).getGeometry().getLocation().getLat(),
                            pois.get(i).getGeometry().getLocation().getLng())))/1000);
            String d=distance+" KM";
            dist.setText(d);

            String type=pois.get(i).getTypes().get(0);
            Log.d(TAG, "Configure_AR: TYPE:"+type + "LODGING:"+R.string.logding);
            if(type.equals(getResources().getString(R.string.restaurant))){
                icon.setImageResource(R.drawable.food_fork_drink);
            }else if(type.equals(getResources().getString(R.string.logding))){
                icon.setImageResource(R.drawable.hotel);
            }else if(type.equals(getResources().getString(R.string.atm))){
                icon.setImageResource(R.drawable.cash_usd);
            }else if(type.equals(getResources().getString(R.string.hosp))){
                icon.setImageResource(R.drawable.hospital);
            }else if(type.equals(getResources().getString(R.string.movie))){
                icon.setImageResource(R.drawable.filmstrip);
            }else if(type.equals(getResources().getString(R.string.cafe))){
                icon.setImageResource(R.drawable.coffee);
            }else if(type.equals(getResources().getString(R.string.bakery))){
                icon.setImageResource(R.drawable.food);
            }else if(type.equals(getResources().getString(R.string.mall))){
                icon.setImageResource(R.drawable.shopping);
            }else if(type.equals(getResources().getString(R.string.pharmacy))){
                icon.setImageResource(R.drawable.pharmacy);
            }else if(type.equals(getResources().getString(R.string.park))){
                icon.setImageResource(R.drawable.pine_tree);
            }else if(type.equals(getResources().getString(R.string.bus))){
                icon.setImageResource(R.drawable.bus);
            }else {
                icon.setImageResource(R.drawable.map_icon);
            }


            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

            try {
              //  Paint paint = new Paint(ANTI_ALIAS_FLAG);
//                paint.setTextSize(textSize);
//                paint.setColor(textColor);
                //paint.setTextAlign(Paint.Align.LEFT);
//                float baseline = -paint.ascent(); // ascent() is negative
//                int width = (int) (paint.measureText(pois.get(i).getName()) + 0.5f); // round
//                int height = (int) (baseline + paint.descent() + 0.5f);

                view.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                snapshot = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight()
                        , Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(snapshot);
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                view.draw(canvas);
                //canvas.drawBitmap(snapshot);
                //snapshot = Bitmap.createBitmap(view.getDrawingCache(),10,10,200,100); // You can tell how to crop the snapshot and whatever in this method
            } finally {
                view.setDrawingCacheEnabled(false);
            }


            String uri=saveToInternalStorage(snapshot,pois.get(i).getId()+".png");

            //icon.setImageURI(Uri.parse(uri));
            poiGeoObj.setImageUri(uri);

            world.addBeyondarObject(poiGeoObj);
        }

        textView.setVisibility(View.INVISIBLE);

        // ... and send it to the fragment
        arFragmentSupport.setWorld(world);

    }

    private String saveToInternalStorage(Bitmap bitmapImage,String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name);

        Log.d(TAG, "saveToInternalStorage: PATH:"+mypath.toString());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.toString();
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path).toString();


//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"profile.jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return directory.getAbsolutePath();
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void Set_googleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        else {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                try {
                    Poi_list_call(900);
                }catch (Exception e){
                    Log.d(TAG, "onCreate: Intent Error");
                }
            }
        }

    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        if (beyondarObjects.size() > 0) {
            Log.e("SOBHAn-------------" , beyondarObjects.get(0).getName());
            Poi_details_call(beyondarObjects.get(0).getName());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onTouchBeyondarView(MotionEvent event, ArBeyondarGLSurfaceView var2) {

        float x = event.getX();
        float y = event.getY();

        ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();

        // This method call is better to don't do it in the UI thread!
        // This method is also available in the BeyondarFragment
        var2.getBeyondarObjectsOnScreenCoordinates(x, y, geoObjects);

        String textEvent = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                textEvent = "Event type ACTION_DOWN: ";
                break;
            case MotionEvent.ACTION_UP:
                textEvent = "Event type ACTION_UP: ";
                break;
            case MotionEvent.ACTION_MOVE:
                textEvent = "Event type ACTION_MOVE: ";
                break;
            default:
                break;
        }

        Iterator<BeyondarObject> iterator = geoObjects.iterator();
        while (iterator.hasNext()) {
            BeyondarObject geoObject = iterator.next();
            textEvent = textEvent + " " + geoObject.getName();
            Log.d(TAG, "onTouchBeyondarView: ATTENTION !!! "+textEvent);

            // ...
            // Do something
            // ...
        }
    }




}
