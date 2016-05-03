package com.ta.heytaxi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener

{
    protected static final String TAG = "MainActivity";
    private Context context;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private Double longitude = 0.0;
    private Double latitude = 0.0;

    private LatLng defaultTaipei = new LatLng(25.047924, 121.517081);

    private ListView orderListView;
    private CustomerOrderAdapter adapter;
    private List<CustomerOrder> items;
    private LatLng currentLocation;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        orderListView = (ListView) findViewById(R.id.orderlistView);
        items = createOrderItems();

        adapter = new CustomerOrderAdapter(items, this);
        orderListView.setAdapter(adapter);
        orderListView.setOnItemClickListener(this);

        updateLocation();
        Log.i(TAG,"001");
        //updateLocation();

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @SuppressWarnings("MissingPermission")
    protected void updateLocation() {

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updatesF
        LocationListener locationListener = new OrderLocationListener();

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 9000, 0, locationListener);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.i("current location", String.valueOf(location));
        Log.i(TAG,"002");
    }

    private void makeUseOfNewLocation(Location location) {
        Log.i("current location", String.valueOf(location.getLatitude()));
        Log.i("current location", String.valueOf(location.getLongitude()));
    }

    private List<CustomerOrder> createOrderItems() {
        Random rand = new Random();
        List<CustomerOrder> result = new ArrayList<CustomerOrder>();
        CustomerOrder order;
        LatLng latLng;
        int imageResourceId = getResources().getIdentifier("service_01", "drawable", this.getPackageName());
        ;
        for (int i = 0; i < 20; i++) {
            order = new CustomerOrder();
            order.setImageResource(imageResourceId);
            order.setOrderNO("00000" + i);
            int x = ((rand.nextInt(2) + 1) % 2 == 0) ? -1 : 1;
            int y = ((rand.nextInt(2) + 1) % 2 == 0) ? -1 : 1;
            latLng = new LatLng(defaultTaipei.latitude - (i * x * 0.0003), defaultTaipei.longitude - (i * y * 0.0005));
            //order.setCurrentLocation(Helper.getAddressByLatLng(latLng));
            order.setCurrentLocationByLatLng(latLng);
            result.add(order);
        }
        return result;
    }

    private void putCustomerOrderInfoToMap(List<CustomerOrder> _items) {
        boolean isFirst = true;
        MarkerOptions marker = null;
        for (CustomerOrder _item : _items) {
            Log.i("CustomerOrder", _item.toString());

            if (isFirst) {
                drawMarker(_item.getCurrentLocationByLatLng(), isFirst);
                currentLocation = _item.getCurrentLocationByLatLng();
                isFirst = false;
            } else {
                drawMarker(_item.getCurrentLocationByLatLng(), isFirst);
            }
        }
        if (_items.size() > 0) {
            moveMap(_items.get(0).getCurrentLocationByLatLng());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        replaceMarker(currentLocation);
        LatLng value = ((CustomerOrder) parent.getAdapter().getItem(position)).getCurrentLocationByLatLng();

        playAnimateCamera(value, 3000);
        drawMarker(value, true);
        moveMap(value);
        setCurrentLocation(value);

    }

    //@SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        setMapInfomation();
        // Add a marker in Sydney, Australia, and move the camera.

        mMap.addMarker(new MarkerOptions().position(defaultTaipei).title("Marker in Taipei"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultTaipei));
        //moveMap(defaultTaipei);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        updateLocation();
        putCustomerOrderInfoToMap(items);
        Log.i(TAG,"003");

    }


    //@SuppressWarnings("MissingPermission")
    private void setMapInfomation() {

        // 設定地圖類型
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // 地圖上顯示建築物
        // 注意：zoom的設定要 >=17才會顯示建築物
        mMap.setBuildingsEnabled(true);

        // 顯示目前所在位置
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } else {

        }

        // Google地圖使用者操作界面功能設定
        UiSettings ui = mMap.getUiSettings();
        // 開啟/關閉縮放鈕
        ui.setZoomControlsEnabled(true);
        // 開啟/關閉地圖捲動手勢
        ui.setScrollGesturesEnabled(true);
        // 開啟/關閉地圖縮放手勢
        ui.setZoomGesturesEnabled(true);
        // 開啟/關閉地圖傾斜手勢
        ui.setTiltGesturesEnabled(true);
        // 開啟/關閉地圖旋轉手勢
        ui.setRotateGesturesEnabled(true);

        //noinspection ResourceType
        //mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String address = Helper.getAddressByLatLng(latLng);
                if (address == null) {
                    Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                    playAnimateCamera(latLng, 3000);
                    drawMarker(latLng);
                    moveMap(latLng);
                }
            }
        });
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .bearing(300)
                        .tilt(0)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void playAnimateCamera(LatLng latlng, int durationMs) {
        // 設置相關地圖相機位置參數，其中zoom的設定要 >=17才會顯示建築物
        CameraPosition cameraPos = new CameraPosition.Builder().target(latlng)
                .zoom(17.0f).bearing(300).tilt(45).build();
        // 定義地圖相機移動
        CameraUpdate cameraUpt = CameraUpdateFactory
                .newCameraPosition(cameraPos);
        // 地圖相機動畫行程設定
        mMap.animateCamera(cameraUpt, durationMs, null);
    }

    private void drawMarker(LatLng latLng) {
        drawMarker(latLng, false);
    }

    private void drawMarker(LatLng latLng, boolean isCurrentIcon) {

        // 1.建立 Marker
        MarkerOptions options = new MarkerOptions(); // 建立標記選項的實例
        options.position(latLng); // 標記經緯度
        options.title(""); // Info-Window標題
        options.snippet("緯經度:" + latLng); // Info-Window標記摘要
        options.anchor(0.5f, 1.0f); // 錨點
        options.draggable(true); // 是否可以拖曳標記?

        if (isCurrentIcon) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        //options.

        // 2.將Marker加入到地圖中
        mMap.addMarker(options);
        // 3.移動到Marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


    }

    private void replaceMarker(LatLng latLng) {
        if (currentLocation != null) {
            drawMarker(latLng);
        }
    }


    public List<CustomerOrder> getItems() {
        return items;
    }

    public void setItems(List<CustomerOrder> items) {
        this.items = items;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getmLastLocation() {
        return mLastLocation;
    }

    public void setmLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
    }

    public ListView getOrderListView() {
        return orderListView;
    }

    public void setOrderListView(ListView orderListView) {
        this.orderListView = orderListView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int length=permissions.length;
        String[] _permissions=new String[length+1];
        _permissions[length+1]= Manifest.permission.MAPS_RECEIVE;
        Log.i(TAG,_permissions[length+1]);
        int start=0;
        for(String _permission:permissions){
            _permissions[start]=_permission;
            Log.i(TAG,_permissions[start]);
            start++;

        }
        Log.i(TAG,"004");
        super.onRequestPermissionsResult(requestCode, _permissions, grantResults);
    }

    @Override
    protected void onResume() throws SecurityException {
        super.onResume();
        updateLocation();
        Log.i(TAG,"005");
    }

    @Override
    protected void onPause() throws SecurityException {
        super.onPause();
        Log.i(TAG,"006");
//        locationManager.removeUpdates(locationListener);
//        // 移除位置請求服務
//        if (googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        updateLocation();
        Log.i(TAG,"007");
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        Log.i(TAG,"008");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        Log.i(TAG,"009");
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Log.i(TAG,"010");

        updateLocation();
        Log.i(TAG,String.valueOf(mLastLocation));
        Log.i(TAG,"011");
    }
    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
        Log.i(TAG,"012");
    }

//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
}
