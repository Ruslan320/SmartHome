package com.example.smarthome.pojo;

import com.example.smarthome.R;

public class Sensor {
    private static int count = 0;
    private int id;
    private boolean on = false;
    private String name;
    private String type;
    private int bg;
    private int style;

    public Sensor(String name, String type) {
        this.name = name;
        if(type.equals("Умная розетка")){
            bg = R.drawable.ic_toggle_socket_bg;
            style = R.style.toggleButtonSocket;
        }
        else if(type.equals("Умная лампа")){
            bg = R.drawable.ic_toggle_lamp_bg;
            style = R.style.toggleButtonLamp;
        }
        else if(type.equals("Умный чайник")){
            bg = R.drawable.ic_toggle_ketller_bg;
            style = R.style.toggleButtonKattler;
        }
        id = count++;
    }

    public boolean isOn() {
        return on;
    }

    public void Switch() {
        on = !on;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}

