package com.example.smarthome.pojo;

import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.Serializable;
import java.util.Objects;

public class Sensor implements Serializable {
    private String id;
    private boolean on = false;
    private String name;
    private int type;
    private int bg;
    private int style;



    public Sensor(String name, String type, String id, boolean on) {
        this.name = name;
        switch (type) {
            case "Умная розетка":
                this.type = 0;
                break;
            case "Умная лампа":
                this.type = 1;
                break;
            case "Умный чайник":
                this.type = 2;
                break;
        }
        this.on = on;
        this.id = id;
    }

    public Sensor(String name, String type, boolean on) {
        this.name = name;
        switch (type) {
            case "Умная розетка":
                this.type = 0;
                break;
            case "Умная лампа":
                this.type = 1;
                break;
            case "Умный чайник":
                this.type = 2;
                break;
        }
        this.on = on;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return Objects.equals(id, sensor.id) &&
                on == sensor.on &&
                Objects.equals(name, sensor.name) &&
                Objects.equals(type, sensor.type);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, on, name, type);
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
}

