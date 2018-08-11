package pro.rasht.museum.ar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.PolylineOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener,
        View.OnClickListener {

    final Context context = this;

    private static final String TAG = "MapsActivity";



    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;


    private LocationManager mLocationManager = null;
    private String provider = null;
    private Marker mCurrentPosition = null;


    private static LatLng fromPosition = null;
    private static LatLng toPosition = null;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        addMap();
        addLines();


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        final String title = marker.getTitle();

       /* Toast.makeText(this,
                marker.getTitle() + " has been clicked " + " times.",
                Toast.LENGTH_SHORT).show();*/


        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_item_marker_map);

        ImageView imgMarkerDialogmap = (ImageView) dialog.findViewById(R.id.img_marker_dialogmap);
        Button btnArDialogmap = (Button) dialog.findViewById(R.id.btn_ar_dialogmap);
        Button btnVrDialogmap = (Button) dialog.findViewById(R.id.btn_vr_dialogmap);
        TextView tvTitleDialogmap = (TextView) dialog.findViewById(R.id.tv_title_dialogmap);
        // if button is clicked, close the custom dialog


        tvTitleDialogmap.setText(marker.getTitle());


        btnArDialogmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MapsActivity.this,ArCamActivity.class);

                /*try {
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
                }*/
            }
        });


        btnVrDialogmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Vr..!!",Toast.LENGTH_SHORT).show();
            }
        });





        dialog.show();


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


    @Override
    public void onClick(View v) {

    }


    //method

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
        CameraPosition Library = CameraPosition.builder().target(new LatLng(37.279151, 49.579599))
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
            Location location = mLocationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);
            //  mLocationManager.addGpsStatusListener(this);
            long minTime = 5000;// ms
            float minDist = 5.0f;// meter
            mLocationManager.requestLocationUpdates(provider, minTime, minDist,
                    this);
        }
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


}