package com.ta.heytaxi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyLocationDemoActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        com.google.android.gms.location.LocationListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    // Location請求物件
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTest);
        mapFragment.getMapAsync(this);

        configGoogleApiClient();


        configLocationRequest();

        // 如果記事已經儲存座標
        if (false) {
            //if (lat != 0.0 && lng != 0.0) {
/*            // 建立座標物件
            LatLng itemPlace = new LatLng(lat, lng);
            // 加入地圖標記
            addMarker(itemPlace, intent.getStringExtra("title"),
                    intent.getStringExtra("datetime"));
            // 移動地圖
            moveMap(itemPlace);*/
        } else {
            // 連線到Google API用戶端
            if (!client.isConnected()) {
                client.connect();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();


    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            mMap.setMyLocationEnabled(true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
        Log.i("Current Location", "Location Enabled:" + mMap.isMyLocationEnabled());
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new OrderLocationListener();

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 9000, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        makeUseOfNewLocation(location);

        location = LocationServices.FusedLocationApi.getLastLocation(client);

        makeUseOfNewLocation(location);

    }

    private void makeUseOfNewLocation(Location location) {
        if (location == null) {
            Log.e("current location", "Location is null");
            return;
        }
        Log.i("current location", String.valueOf(location.getLatitude()));
        Log.i("current location", String.valueOf(location.getLongitude()));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        enableMyLocation();
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, MyLocationDemoActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, "google_play_service_missing",
                    Toast.LENGTH_LONG).show();
        }
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // 建立Google API用戶端物件
    private synchronized void configGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();

        // 連線到Google API用戶端
        if (!client.isConnected() ) {
            client.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 移除位置請求服務
        if (client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    client, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 移除Google API用戶端連線
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
