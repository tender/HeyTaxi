package com.ta.heytaxi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    protected static final String TAG = "MapActivity";
    private static final int REQUEST_LOCATION=2;
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
        //mapFragment.onActivityCreated(savedInstanceState);
        mapFragment.getMapAsync(this);
       //mMap=mapFragment.getMap();

        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
//        orderListView = (ListView) findViewById(R.id.orderlistView);
//        items = createOrderItems();
//
//        adapter = new CustomerOrderAdapter(items, this);
//        orderListView.setAdapter(adapter);
//        orderListView.setOnItemClickListener(this);


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION);
        }else{
            setupMyLocation();
        }
        setMapInfomation();
//        mMap.addMarker(new MarkerOptions().position(defaultTaipei).title("Marker in Taipei"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultTaipei));
        //moveMap(defaultTaipei);

        //putCustomerOrderInfoToMap(items);

    }

    private void setupMyLocation() {
        //noinspection MissingPermission
        mMap.setMyLocationEnabled(true);

/*        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria=new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String provider=locationManager.getBestProvider(criteria,true);
                //noinspection MissingPermission
                Location location=locationManager.getLastKnownLocation(provider);
                if(location !=null){
                    Log.i("LOCATION",location.getLatitude()+"/"+location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),location.getLongitude())
                            ,19));
                }
                return true;
            }
        });*/
    }

    private void loadData(LatLng _location){
        orderListView = (ListView) findViewById(R.id.orderlistView);
        items = createOrderItems(_location);

        adapter = new CustomerOrderAdapter(items, this);
        orderListView.setAdapter(adapter);
        orderListView.setOnItemClickListener(this);

        putCustomerOrderInfoToMap(items);
    }



    private void makeUseOfNewLocation(Location location) {
        Log.i("current location", String.valueOf(location.getLatitude()));
        Log.i("current location", String.valueOf(location.getLongitude()));
    }

    private List<CustomerOrder> createOrderItems(LatLng _location) {
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
            latLng = new LatLng(_location.latitude - (i * x * 0.0003), _location.longitude - (i * y * 0.0005));
            order.setCurrentLocation(Helper.getAddressByLatLng(latLng));
            order.setCurrentLocationByLatLng(latLng);
            result.add(order);
        }
        return result;
    }

    private void putCustomerOrderInfoToMap(List<CustomerOrder> _items) {
        boolean isFirst = true;
        Marker marker = null;
        for (CustomerOrder _item : _items) {
            Log.i("CustomerOrder", _item.toString());

            if (isFirst) {
                drawMarker(_item.getCurrentLocationByLatLng(), _item.getName(),isFirst);
                currentLocation = _item.getCurrentLocationByLatLng();
                isFirst = false;
            } else {
                drawMarker(_item.getCurrentLocationByLatLng(),_item.getName(), isFirst);
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
        drawMarker(value,"", true);
        moveMap(value);
        setCurrentLocation(value);
    }

    private void setMapInfomation() {

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
        drawMarker(latLng,"", false);
    }

    private void drawMarker(LatLng latLng, String title,boolean isCurrentIcon) {

        // 1.建立 Marker
        MarkerOptions options = new MarkerOptions(); // 建立標記選項的實例
        options.position(latLng); // 標記經緯度
        options.title(title); // Info-Window標題
        options.snippet("緯經度:" + latLng); // Info-Window標記摘要
        options.anchor(0.5f, 1.0f); // 錨點
        options.draggable(true); // 是否可以拖曳標記?
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));
        if (isCurrentIcon) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        //options.

        // 2.將Marker加入到地圖中
        mMap.addMarker(options);
        // 3.移動到Marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

    }

    private void replaceMarker(LatLng latLng) {
        if (currentLocation != null) {
            drawMarker(latLng);
        }
    }

    private void drawCircle(LatLng _location){
        CircleOptions options=new CircleOptions();
        options.center(_location);
        options.radius(100);
        options.strokeWidth(5);
        options.strokeColor(Color.TRANSPARENT);
        options.fillColor(Color.argb(150,255,0,0));
        options.zIndex(3);
        mMap.addCircle(options);

    }
    private void removeCircle(LatLng _location){
        mMap.clear();

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
        switch(requestCode){
            case REQUEST_LOCATION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //noinspection MissingPermission
                    setupMyLocation();
                }else{

                }
                break;
        }

    }


//    @Override
//    protected void onResume() throws SecurityException {
//        super.onResume();
//        setupMyLocation();
//
//    }
//
//    @Override
//    protected void onPause() throws SecurityException {
//        super.onPause();
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //noinspection MissingPermission
        Location location=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        setMapInfomation();
        if(location !=null){
            Log.i("LOCATION",location.getLatitude()+"/"+location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(),location.getLongitude())
                    ,19));
            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("My Home"));
            Log.i("LOCATION","222222222222222");
            loadData(latLng);
            moveMap(latLng);
            drawCircle(latLng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
}
