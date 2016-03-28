package com.ta.heytaxi;

/**
 * Created by IT-0002993 on 16/3/22.
 */
public class FunctionItem {
    private String name;
    private boolean disabled;
    private int imageResource;

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
}
