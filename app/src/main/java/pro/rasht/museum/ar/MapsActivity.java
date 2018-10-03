package pro.rasht.museum.ar;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pro.rasht.museum.ar.Activity.MainActivity;
import pro.rasht.museum.ar.Classes.SavePref;
import pro.rasht.museum.ar.ar.ArBeyondarGLSurfaceView;
import pro.rasht.museum.ar.ar.OnTouchBeyondarViewListenerMod;
import pro.rasht.museum.ar.network.AppController;
import pro.rasht.museum.ar.network.DirectionsJSONParser;
import pro.rasht.museum.ar.network.PlaceResponse;
import pro.rasht.museum.ar.network.PoiResponse;
import pro.rasht.museum.ar.network.RetrofitInterface;
import pro.rasht.museum.ar.network.poi.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener,
        OnClickBeyondarObjectListener,
        OnTouchBeyondarViewListenerMod {

    final Context context = this;

    private static final String TAG = "MapsActivity";

    SavePref save;

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;


    private LocationManager mLocationManager = null;
    private String provider = null;
    private Marker mCurrentPosition = null;


    private static LatLng fromPosition = null;
    private static LatLng toPosition = null;
    private static LatLng clickPosition = null;

    private static final LatLng LOWER_MANHATTAN = new LatLng(37.283788, -49.563508);
    private static final LatLng TIMES_SQUARE = new LatLng(37.286127, 49.576188);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(37.290686, 49.568935);
    static final LatLng abojafar = new LatLng(37.279088, 49.583653);
    static final LatLng pirsara = new LatLng(37.275186, 49.573086);
    static final LatLng darvishmokhles = new LatLng(37.275522, 49.571745);
    static final LatLng chomasara = new LatLng(37.275934, 49.567535);
    static final LatLng karfabad = new LatLng(37.272923, 49.571907);
    static final LatLng bisetoon = new LatLng(37.277112, 49.574916);
    static final LatLng t_chomasara = new LatLng(37.275862, 49.565693);
    static final LatLng t_kiyab = new LatLng(37.275694, 49.580752);
    static final LatLng t_khamirankiyab = new LatLng(37.273426, 49.584530);
    static final LatLng t_mostofi = new LatLng(37.279064, 49.579728);
    static final LatLng mostofi = new LatLng(37.279151, 49.579599);
    static final LatLng molaalimohammad = new LatLng(37.275987, 49.577017);
    static final LatLng lakani = new LatLng(37.275524, 49.580935);


    //private TextView dialog_loading_text;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private LayoutInflater layoutInflater;
    //private ArFragmentSupport arFragmentSupport;
    private World world;
    List<Result> poiResult;


    static final String AUDIO_PATH =
            "http://dl.pop-music.ir/music/1397/Shahrivar/Javad%20Kamalirad%20-%20Salare%20Zeinab%20(128).mp3";
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;


    @BindView(R.id.dialog_place_detail)
    CardView dialog_cardview;
    @BindView(R.id.dialog_place_close_btn)
    ImageButton dialog_cardview_close_btn;
    @BindView(R.id.dialog_place_name)
    TextView dialog_place_name;
    @BindView(R.id.dialog_place_address)
    TextView dialog_place_addr;
    @BindView(R.id.dialog_place_image)
    ImageView dialog_place_image;
    @BindView(R.id.dialog_place_ar_direction)
    Button dialog_place_ar_btn;
    @BindView(R.id.dialog_brwoser_progress)
    ProgressBar dialog_browser_progress;
    @BindView(R.id.dialog_place_maps_direction)
    Button dialog_place_maps_btn;
    @BindView(R.id.btn_history_point_maps)
    Button btnHistoryPointMaps;
    @BindView(R.id.btn_placeId_point_maps)
    Button btnPlaceIdPointMaps;

    //Padkast Dialog
    @BindView(R.id.seekBar_maps)
    SeekBar seekBarMaps;
    @BindView(R.id.media_pause_maps)
    ImageView mediaPauseMaps;
    @BindView(R.id.songDuration_maps)
    TextView songDurationMaps;
    @BindView(R.id.media_play_mpas)
    ImageView mediaPlayMpas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);


        addMap();
        addLines();
        dialog_browser_progress.setVisibility(View.GONE);
        dialog_cardview.setVisibility(View.GONE);
        arNav();


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        final String title = marker.getTitle();

       /* Toast.makeText(this,
                marker.getTitle() + " has been clicked " + " times.",
                Toast.LENGTH_SHORT).show();*/

        clickPosition = marker.getPosition();

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Toast.makeText(context, "" + pointOfInterest.placeId, Toast.LENGTH_SHORT).show();

            }
        });


        Poi_details_call(poiResult.get(0).getPlaceId());

        Log.e("ID----------", poiResult.get(0).getPlaceId());

        return false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // do nothing during drag
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        toPosition = marker.getPosition();
        Toast.makeText(
                getApplicationContext(),
                "Marker " + marker.getTitle() + " dragged from " + fromPosition
                        + " to " + toPosition, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        fromPosition = marker.getPosition();
        Log.d(getClass().getSimpleName(), "Drag start at: " + fromPosition);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //change theme
        changeTheme();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (isProviderAvailable() && (provider != null)) {
            locateCurrentPosition();
        }

        addMarkers();

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {

        updateWithNewLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {

        updateWithNewLocation(null);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.AVAILABLE:
                break;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }


    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    //method


    //Click History point or Place Custom
    public void HistoryOrPlac(View view){
        switch (view.getId()){
            case R.id.btn_history_point_maps:

                mMap.clear();


                Toast.makeText(context, "" + AppController.POINTMODEL.get(0).getIshistory(), Toast.LENGTH_SHORT).show();


                break;
            case R.id.btn_placeId_point_maps:




                break;
        }
    }







    //Player
    public void MediaOnclick(View view) {
        switch (view.getId()) {
            case R.id.media_play_mpas:
                try {
                    //playAudio(AUDIO_PATH);
                    //playLocalAudio();
                    //playLocalAudio_UsingDescriptor();3
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.media_pause_maps:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    playbackPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                }
                break;
        }
    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        //mediaPlayer.start();
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }
    //End Player


    private void addMap() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    private void addMarkers() {


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addMarker(new MarkerOptions().position(abojafar).title("بقعه ابوجعفر")
                .icon(fromResource(R.mipmap.ic_map)));


        mMap.addMarker(new MarkerOptions().position(pirsara).title("مسجد و بقعه پیرسرا")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(darvishmokhles).title("مسجد دوریش مخلص")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(chomasara).title("مسجد چمارسرا")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(karfabad).title("مسجد کرف آباد")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(bisetoon).title("مسجد سمیع بزاز (بیستون)")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(t_chomasara).title("تکیه چمارسرا")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(t_kiyab).title("تکیه کیاب")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(t_khamirankiyab).title("تکیه خمیران کیاب")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(t_mostofi).title("تکیه مستوفی استادسرا")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(mostofi).title("مسجد مستوفی استادسرا")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(molaalimohammad).title("مسجد ملاعلی محمد")
                .icon(fromResource(R.mipmap.ic_map)));

        mMap.addMarker(new MarkerOptions().position(lakani).title("مسجد لاکانی")
                .icon(fromResource(R.mipmap.ic_map)));

        // 3D Map
        CameraPosition Library = CameraPosition.builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(16)
                .tilt(45).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Library));
    }

    private void addLines() {
        if (mMap != null) {
            mMap.addPolyline((new PolylineOptions())
                    .add(TIMES_SQUARE, BROOKLYN_BRIDGE, LOWER_MANHATTAN,
                            TIMES_SQUARE).width(5).color(Color.BLUE)
                    .geodesic(true));
            // move camera to zoom on map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LOWER_MANHATTAN, 13));
        }
    }

    private void changeTheme() {

        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void locateCurrentPosition() {

        int status = getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                getPackageName());

        if (status == PackageManager.PERMISSION_GRANTED) {
            location = mLocationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);
            //  mLocationManager.addGpsStatusListener(this);
            long minTime = 5000;// ms
            float minDist = 5.0f;// meter
            mLocationManager.requestLocationUpdates(provider, minTime, minDist,
                    this);
        }
        Poi_list_call(900);
    }

    private boolean isProviderAvailable() {
        mLocationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        provider = mLocationManager.getBestProvider(criteria, true);
        if (mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;

            return true;
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }

        if (provider != null) {
            return true;
        }
        return false;
    }

    private void updateWithNewLocation(Location location) {

        if (location != null && provider != null) {
            double lng = location.getLongitude();
            double lat = location.getLatitude();

            addBoundaryToCurrentPosition(lat, lng);


            CameraPosition camPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng)).zoom(17f).build();

            if (mMap != null)
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(camPosition));
        } else {
            Log.d("Location error", "Something went wrong");
        }
    }

    private void addBoundaryToCurrentPosition(double lat, double lang) {

        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.position(new LatLng(lat, lang));
        mMarkerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.marker_current));
        mMarkerOptions.anchor(0.5f, 0.5f);
       /* CircleOptions mOptions = new CircleOptions()
                .center(new LatLng(lat, lang)).radius(100)
                .strokeColor(0x110000FF).strokeWidth(1).fillColor(0x110000FF);
        mMap.addCircle(mOptions);*/
        if (mCurrentPosition != null)
            mCurrentPosition.remove();
        mCurrentPosition = mMap.addMarker(mMarkerOptions);
    }


    // Start Ar Navigation After Click
    private void arNav() {

        //dialog_loading_text=(TextView) findViewById(R.id.loading_text);

        /*arFragmentSupport = (ArFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.dialog_cam_fragment);
        arFragmentSupport.setOnClickBeyondarObjectListener(this);
        arFragmentSupport.setOnTouchBeyondarViewListener(this);*/

        //dialog_loading_text=(TextView) findViewById(R.id.loading_text);

        Set_googleApiClient(); //Sets the GoogleApiClient

        dialog_cardview_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //seekbar_cardview.setVisibility(View.VISIBLE);
                dialog_cardview.setVisibility(View.GONE);
                dialog_place_image.setImageResource(android.R.color.transparent);
                dialog_place_name.setText(" ");
                dialog_place_addr.setText(" ");
            }
        });


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

    void Poi_list_call(int radius) {
        dialog_browser_progress.setVisibility(View.VISIBLE);
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

        final Call<PoiResponse> call = apiService.listPOI(String.valueOf(location.getLatitude()) + "," +
                        String.valueOf(location.getLongitude()), radius,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<PoiResponse>() {
            @Override
            public void onResponse(Call<PoiResponse> call, Response<PoiResponse> response) {

                dialog_browser_progress.setVisibility(View.GONE);
                //seekbar_cardview.setVisibility(View.VISIBLE);

                poiResult = response.body().getResults();


                Configure_AR(poiResult);
            }

            @Override
            public void onFailure(Call<PoiResponse> call, Throwable t) {
                dialog_browser_progress.setVisibility(View.GONE);
            }
        });

    }

    void Poi_details_call(String placeid) {

        dialog_browser_progress.setVisibility(View.VISIBLE);

        //buffer music
        try {
            playAudio(AUDIO_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }


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

        final Call<PlaceResponse> call = apiService.getPlaceDetail(placeid,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {

                //seekbar_cardview.setVisibility(View.GONE);
                dialog_cardview.setVisibility(View.VISIBLE);
                dialog_browser_progress.setVisibility(View.GONE);

                final pro.rasht.museum.ar.network.place.Result result = response.body().getResult();

                dialog_place_name.setText(result.getName());
                dialog_place_addr.setText(result.getFormattedAddress());

                try {
                    HttpUrl url = new HttpUrl.Builder()
                            .scheme("https")
                            .host("maps.googleapis.com")
                            .addPathSegments("maps/api/place/photo")
                            .addQueryParameter("maxwidth", "400")
                            .addQueryParameter("photoreference", result.getPhotos().get(0).getPhotoReference())
                            .addQueryParameter("key", getResources().getString(R.string.google_maps_key))
                            .build();

                    new PoiPhotoAsync().execute(url.toString());

                } catch (Exception e) {
                    Log.d(TAG, "onResponse: " + e.getMessage());
                    Toast.makeText(MapsActivity.this, getString(R.string.no_image_poiActivity), Toast.LENGTH_SHORT).show();
                }

                dialog_place_maps_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String url = getDirectionsUrl(
                                new LatLng(location.getLatitude(), location.getLongitude()),
                                clickPosition);
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(url);

                        dialog_cardview_close_btn.performClick();

                        /*Intent intent;
                        try{
                            Uri.Builder builder = new Uri.Builder();
                            builder.scheme("http")
                                    .authority("maps.google.com")
                                    .appendPath("maps")
                                    .appendQueryParameter("saddr", location.getLatitude()+","+location.getLongitude())
                                    .appendQueryParameter("daddr",result.getGeometry().getLocation().getLat()+","+
                                            result.getGeometry().getLocation().getLng());

                            intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse( builder.build().toString()));
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            Log.d(TAG, "onClick: mapNav Exception caught");
                            Toast.makeText(MapsActivity.this, getString(R.string.error_openmap_poiActivity), Toast.LENGTH_SHORT).show();
                        }
                        */
                    }
                });

                dialog_place_ar_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MapsActivity.this, ArCamActivity.class);

                        try {
                            intent.putExtra("SRC", "Current Location");
                            intent.putExtra("DEST", result.getGeometry().getLocation().getLat() + "," +
                                    result.getGeometry().getLocation().getLng());
                            intent.putExtra("SRCLATLNG", location.getLatitude() + "," + location.getLongitude());
                            intent.putExtra("DESTLATLNG", result.getGeometry().getLocation().getLat() + "," +
                                    result.getGeometry().getLocation().getLng());
                            startActivity(intent);
                            finish();
                        } catch (NullPointerException npe) {
                            Log.d(TAG, "onClick: The IntentExtras are Empty");
                        }
                    }
                });


            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                dialog_browser_progress.setVisibility(View.GONE);
            }
        });

    }


    public class PoiPhotoAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            dialog_place_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            dialog_place_image.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];

            Bitmap bitmap = null;
            try {
                InputStream input = new URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private void Configure_AR(List<Result> pois) {

        layoutInflater = getLayoutInflater();

        world = new World(getApplicationContext());
        world.setGeoPosition(location.getLatitude(), location.getLongitude());
        world.setDefaultImage(R.drawable.ar_sphere_default);

//        arFragmentSupport.getGLSurfaceView().setPullCloserDistance(25);

        GeoObject geoObjects[] = new GeoObject[pois.size()];

        for (int i = 0; i < pois.size(); i++) {
            GeoObject poiGeoObj = new GeoObject(1000 * (i + 1));
            //ArObject2 poiGeoObj=new ArObject2(1000*(i+1));

//            poiGeoObj.setImageUri(getImageUri(this,textAsBitmap(pois.get(i).getName(),10.0f, Color.WHITE)));
            poiGeoObj.setGeoPosition(pois.get(i).getGeometry().getLocation().getLat(),
                    pois.get(i).getGeometry().getLocation().getLng());
            poiGeoObj.setName(pois.get(i).getPlaceId());
            //poiGeoObj.setPlaceId(pois.get(0).getPlaceId());

            //Bitmap bitmap=textAsBitmap(pois.get(i).getName(),30.0f,Color.WHITE);

            Bitmap snapshot = null;
            View view = getLayoutInflater().inflate(R.layout.poi_container, null);
            TextView name = (TextView) view.findViewById(R.id.poi_container_name);
            TextView dist = (TextView) view.findViewById(R.id.poi_container_dist);
            ImageView icon = (ImageView) view.findViewById(R.id.poi_container_icon);

            name.setText(pois.get(i).getName());
            String distance = String.valueOf((SphericalUtil.computeDistanceBetween(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    new LatLng(pois.get(i).getGeometry().getLocation().getLat(),
                            pois.get(i).getGeometry().getLocation().getLng()))) / 1000);
            String d = distance + " KM";
            dist.setText(d);

            String type = pois.get(i).getTypes().get(0);
            Log.d(TAG, "Configure_AR: TYPE:" + type + "LODGING:" + R.string.logding);
            if (type.equals(getResources().getString(R.string.restaurant))) {
                icon.setImageResource(R.drawable.food_fork_drink);
            } else if (type.equals(getResources().getString(R.string.logding))) {
                icon.setImageResource(R.drawable.hotel);
            } else if (type.equals(getResources().getString(R.string.atm))) {
                icon.setImageResource(R.drawable.cash_usd);
            } else if (type.equals(getResources().getString(R.string.hosp))) {
                icon.setImageResource(R.drawable.hospital);
            } else if (type.equals(getResources().getString(R.string.movie))) {
                icon.setImageResource(R.drawable.filmstrip);
            } else if (type.equals(getResources().getString(R.string.cafe))) {
                icon.setImageResource(R.drawable.coffee);
            } else if (type.equals(getResources().getString(R.string.bakery))) {
                icon.setImageResource(R.drawable.food);
            } else if (type.equals(getResources().getString(R.string.mall))) {
                icon.setImageResource(R.drawable.shopping);
            } else if (type.equals(getResources().getString(R.string.pharmacy))) {
                icon.setImageResource(R.drawable.pharmacy);
            } else if (type.equals(getResources().getString(R.string.park))) {
                icon.setImageResource(R.drawable.pine_tree);
            } else if (type.equals(getResources().getString(R.string.bus))) {
                icon.setImageResource(R.drawable.bus);
            } else {
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


            String uri = saveToInternalStorage(snapshot, pois.get(i).getId() + ".png");

            //icon.setImageURI(Uri.parse(uri));
            poiGeoObj.setImageUri(uri);

            world.addBeyondarObject(poiGeoObj);
        }

        //dialog_loading_text.setVisibility(View.INVISIBLE);

        // ... and send it to the fragment
        //arFragmentSupport.setWorld(world);

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name);

        Log.d(TAG, "saveToInternalStorage: PATH:" + mypath.toString());

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


    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        if (beyondarObjects.size() > 0) {
            Poi_details_call("ChIJbWShqpzYH0ARA_izn4n4cNA");
        }
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
            Log.d(TAG, "onTouchBeyondarView: ATTENTION !!! " + textEvent);

            // ...
            // Do something
            // ...
        }
    }
    // End Ar Navigation After Click


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            mMap.clear();
            addMarkers();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(String.valueOf(point.get("lat")));
                    double lng = Double.parseDouble(String.valueOf(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.parseColor("#3374ba"));
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}