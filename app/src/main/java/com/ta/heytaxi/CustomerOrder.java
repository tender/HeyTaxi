package com.ta.heytaxi;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by IT-0002993 on 16/3/22.
 */
public class CustomerOrder {
    private String name;
    private boolean disabled;
    private int imageResource;


    private String orderNO;//單號
    private String currentLocation;//目前位置
    private LatLng currentLocationByLatLng;//目前位置
    private String destination;//目的地


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(String orderNO) {
        this.orderNO = orderNO;
    }

    public LatLng getCurrentLocationByLatLng() {
        return currentLocationByLatLng;
    }

    public void setCurrentLocationByLatLng(LatLng currentLocationByLatLng) {
        this.currentLocationByLatLng = currentLocationByLatLng;
    }
}
