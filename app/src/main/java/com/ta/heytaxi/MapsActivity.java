package com.ta.heytaxi;

import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , AdapterView.OnItemClickListener{

    private Context context;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private Double longitude = 0.0;
    private Double latitude = 0.0;

    private LatLng defaultTaipei=new LatLng(25.047924, 121.517081);

    ListView orderListView;
    private CustomerOrderAdapter adapter;
    private List<CustomerOrder> items;
    private List<CustomerOrder> itemsForMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context=this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        orderListView=(ListView) findViewById(R.id.orderlistView);
        items=createOrderItems();
        itemsForMap = createOrderItems();
        //Collections.copy(itemsForMap, items);

        adapter=new CustomerOrderAdapter(items,this);
        orderListView.setAdapter(adapter);
        orderListView.setOnItemClickListener(this);



    }

    private List<CustomerOrder> createOrderItems(){
        Random rand = new Random();
        List<CustomerOrder> result=new ArrayList<CustomerOrder>();
        CustomerOrder order;
        LatLng latLng;
        int imageResourceId=getResources().getIdentifier("service_01", "drawable", this.getPackageName());;
        for(int i=0;i<20;i++){
            order=new CustomerOrder();
            order.setImageResource(imageResourceId);
            order.setOrderNO("00000"+i);
            int  x = ((rand.nextInt(2) + 1) % 2==0)?-1:1;
            int  y = ((rand.nextInt(2) + 1) % 2==0)?-1:1;
            latLng=new LatLng(defaultTaipei.latitude-(i*x*0.0003),defaultTaipei.longitude-(i*y*0.0005));
            //order.setCurrentLocation(Helper.getAddressByLatLng(latLng));
            order.setCurrentLocationByLatLng(latLng);
            result.add(order);
        }
        return result;
    }

    private void putCustomerOrderInfoToMap(List<CustomerOrder> _items){
        for(CustomerOrder _item:_items){
            Log.i("CustomerOrder",_item.toString());
            drawMarker(_item.getCurrentLocationByLatLng());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LatLng value=((CustomerOrder)parent.getAdapter().getItem(position)).getCurrentLocationByLatLng();
        playAnimateCamera(value,3000);
        //drawMarker(value);
        moveMap(value);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap=map;
        setMapInfomation();
        // Add a marker in Sydney, Australia, and move the camera.

        mMap.addMarker(new MarkerOptions().position(defaultTaipei).title("Marker in Taipei"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultTaipei));
        //moveMap(defaultTaipei);
        putCustomerOrderInfoToMap(itemsForMap);
    }

    @SuppressWarnings("MissingPermission")
    private void setMapInfomation(){

        // 設定地圖類型
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // 地圖上顯示建築物
        // 注意：zoom的設定要 >=17才會顯示建築物
        mMap.setBuildingsEnabled(true);

        // 顯示目前所在位置
        mMap.setMyLocationEnabled(true);

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
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String address = Helper.getAddressByLatLng(latLng);
                if(address==null){
                    Toast.makeText(context,"Not Found",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,address,Toast.LENGTH_SHORT).show();
                    playAnimateCamera(latLng,3000);
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

        // 1.建立 Marker
        MarkerOptions options = new MarkerOptions(); // 建立標記選項的實例
        options.position(latLng); // 標記經緯度
        options.title(""); // Info-Window標題
        options.snippet("緯經度:" + latLng); // Info-Window標記摘要
        options.anchor(0.5f, 1.0f); // 錨點
        options.draggable(true); // 是否可以拖曳標記?
        // 2.將Marker加入到地圖中
        mMap.addMarker(options);
        // 3.移動到Marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    @Override
    protected void onResume() throws SecurityException {
        super.onResume();

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, locationListener);
//        setUpMapIfNeeded();
//
//        // 連線到Google API用戶端
//        if (!googleApiClient.isConnected()) {
//            googleApiClient.connect();
//        }

    }

    @Override
    protected void onPause() throws SecurityException{
        super.onPause();
//        locationManager.removeUpdates(locationListener);
//        // 移除位置請求服務
//        if (googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//        }
    }


    @Override
    protected void onStart() {

        super.onStart();
//        googleApiClient.connect();
    }



    @Override
    protected void onStop() {
        super.onStop();
//
//        // 移除Google API用戶端連線
//        if (googleApiClient.isConnected()) {
//            googleApiClient.disconnect();
//        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

/*
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        context = this;

        GoogleMap m_map = ((SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);



        // 設定地圖類型
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // 地圖上顯示建築物
        // 注意：zoom的設定要 >=17才會顯示建築物
        mMap.setBuildingsEnabled(true);

        // 顯示目前所在位置
        mMap.setMyLocationEnabled(true);

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

        // 按一下地圖即可取得該點經緯度
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // 根據point取得該經緯度所對應的地址/地標
                String address = Helper.getAddressByLatLng(point);
                if (address == null) {
                    Toast.makeText(context, "Not found !", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                    // 演示地圖相機動畫效果
                    playAnimateCamera(point, 3000);
                }
            }
        });
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
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

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }*/

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        configMapReady();

        setUpMapIfNeeded();

        // 建立Google API用戶端物件
        configGoogleApiClient();

        // 建立Location請求物件
        configLocationRequest();


        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.i("Test",String.valueOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
        updateLocation();
    }


    private void setMapInfomation() throws SecurityException{
        // 設定地圖類型
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // 地圖上顯示建築物
        // 注意：zoom的設定要 >=17才會顯示建築物
        mMap.setBuildingsEnabled(true);

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

*/
/*        // 按一下地圖即可取得該點經緯度
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // 根據point取得該經緯度所對應的地址/地標
                String address = Helper.getAddressByLatLng(point);
                if (address == null) {
                    Toast.makeText(context, "Not found !", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(context, address, Toast.LENGTH_SHORT).show();
                    // 演示地圖相機動畫效果
                    playAnimateCamera(point, 3000);
                }
            }
        });*//*

    }

    */
/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *//*

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException{
        mMap = googleMap;
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        updateLocation();
        setMapInfomation();
        LatLng currentPlace = new LatLng(getLongitude(), getLatitude());
        mMap.addMarker(new MarkerOptions().position(currentPlace).title("Marker in Taipei"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace));
        moveMap(currentPlace);

    }

    public void configMapReady() {
        configGoogleApiClient();
    }

    // 建立Google API用戶端物件
    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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


        //locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onResume() throws SecurityException {
        super.onResume();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, locationListener);
        setUpMapIfNeeded();

        // 連線到Google API用戶端
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

    }

    @Override
    protected void onPause() throws SecurityException{
        super.onPause();
        locationManager.removeUpdates(locationListener);
        // 移除位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }


    @Override
    protected void onStart() {

        super.onStart();
        googleApiClient.connect();
    }



    @Override
    protected void onStop() {
        super.onStop();

        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map)).getMap();

            if (mMap != null) {
                // 移除地圖設定
                setUpMap();
                //processController();
            }
        }
    }

    private void setUpMap(){
        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .title("Marker")
        );
    }


    public void updateLocation() throws SecurityException{
        //locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location newLocation) {
                setLongitude(newLocation.getLongitude() * 1000000);
                setLatitude(newLocation.getLatitude() * 1000000);
                Log.i("Location=", "X=" + getLongitude().intValue() + ", Y=" + getLatitude().intValue());
            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }
        };


        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location==null){
            setLongitude(25.047924);
            setLatitude(121.517081);
        }else{
            setLongitude(location.getLongitude());
            setLatitude(location.getLatitude());
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, getLongitude().longValue(), getLatitude().floatValue(), locationListener);

    }

    @Override
    public void onConnected(Bundle bundle) throws SecurityException{
        // 已經連線到Google Services
        // 啟動位置更新服務
        // 位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MapsActivity.this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        // 位置改變
        // Location參數是目前的位置
//        currentLocation = location;
        LatLng latLng = new LatLng(
                location.getLatitude(), location.getLongitude());
//
//        // 設定目前位置的標記
//        if (currentMarker == null) {
//            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
//        }
//        else {
//            currentMarker.setPosition(latLng);
//        }

        // 移動地圖到目前的位置
        moveMap(latLng);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, "google_play_service_missing",
                    Toast.LENGTH_LONG).show();
        }
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
*/
}
