package com.ta.heytaxi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.ColorUtils;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private List<MarkerOptions> mapMarkers;
    LatLng currentLocation;

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
        //itemsForMap = createOrderItems();
        //Collections.copy(itemsForMap, items);

        adapter=new CustomerOrderAdapter(items,this);
        orderListView.setAdapter(adapter);
        orderListView.setOnItemClickListener(this);


        updateLocation();

    }

    @SuppressWarnings("MissingPermission")
    protected void updateLocation(){

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updatesF
        LocationListener locationListener = new OrderLocationListener();

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

//        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        Log.i("current location",location.toString());

    }

    private void makeUseOfNewLocation(Location location){
        Log.i("current location",String.valueOf(location.getLatitude()));
        Log.i("current location",String.valueOf(location.getLongitude()));
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
        boolean isFirst=true;
        MarkerOptions marker=null;
        for(CustomerOrder _item:_items){
            Log.i("CustomerOrder",_item.toString());

            if(isFirst){
                drawMarker(_item.getCurrentLocationByLatLng(),isFirst);
                currentLocation=_item.getCurrentLocationByLatLng();
                isFirst=false;
            }else{
                drawMarker(_item.getCurrentLocationByLatLng(),isFirst);
            }

            marker=new MarkerOptions();
            marker.position(_item.getCurrentLocationByLatLng());
            getMapMarkers().add(marker);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        reloadMarker();
        LatLng value=((CustomerOrder)parent.getAdapter().getItem(position)).getCurrentLocationByLatLng();
        playAnimateCamera(value,3000);
        drawMarker(value,true);
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

        setMapMarkers(new ArrayList<MarkerOptions>());
        putCustomerOrderInfoToMap(items);


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
        drawMarker(latLng,false);
    }
    private void drawMarker(LatLng latLng,boolean isCurrentIcon) {

        // 1.建立 Marker
        MarkerOptions options = new MarkerOptions(); // 建立標記選項的實例
        options.position(latLng); // 標記經緯度
        options.title(""); // Info-Window標題
        options.snippet("緯經度:" + latLng); // Info-Window標記摘要
        options.anchor(0.5f, 1.0f); // 錨點
        options.draggable(true); // 是否可以拖曳標記?

        if(isCurrentIcon) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        //options.

        // 2.將Marker加入到地圖中
        mMap.addMarker(options);
        // 3.移動到Marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


    }

    private void reloadMarker(){
        for(MarkerOptions item:getMapMarkers()){
            drawMarker(item.getPosition());
        }

    }


    public List<CustomerOrder> getItems() {
        return items;
    }

    public void setItems(List<CustomerOrder> items) {
        this.items = items;
    }

    public List<MarkerOptions> getMapMarkers() {
        return mapMarkers;
    }

    public void setMapMarkers(List<MarkerOptions> mapMarkers) {
        this.mapMarkers = mapMarkers;
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


}
