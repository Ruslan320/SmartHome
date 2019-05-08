package com.example.smarthome.pojo;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.smarthome.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room implements Serializable {
    private static int count = 0;
    private int id;
    private int temp;
    private int hum;
    private String type_room;
    private String name;
    private int img;
//    private static  List<Integer> TypeList = new ArrayList<>(7);
//    private static List<List<Integer>> RoomImgArr = Arrays.asList(
//            new ArrayList<Integer>(Arrays.asList(R.drawable.bedroom_0, R.drawable.bedroom_0, R.drawable.bedroom_0))
//    )
    private List<String> SensorList = new ArrayList<>();

    public Room(int temp, int hum, String name, int type_room) {
        this.temp = temp;
        this.hum = hum;
        this.name = name;

        if(type_room == 0){
            this.type_room = "Bedroom";
            img = R.drawable.bedroom_6;
//            TypeList.set(0, TypeList.get(0)+1);
        }
        else if(type_room == 1){
            this.type_room = "Living room";
            img = R.drawable.living_room_2;
//            img.setImageResource(R.drawable.living_room_2);
        }
        else if(type_room == 2){
            this.type_room = "Kitchen";
            img = R.drawable.kitchen_0;
//            img.setImageResource(R.drawable.kitchen_0);
        }
        else if(type_room == 3){
            this.type_room = "Dining room";
            img = R.drawable.dining_room_3;
//            img.setImageResource(R.drawable.dining_room_3);
        }
        else if(type_room == 4){
            this.type_room = "Office";
            img = R.drawable.office_3;
//            img.setImageResource(R.drawable.office_3);
        }
        else if(type_room == 5){
            this.type_room = "Bathroom";
            img = R.drawable.bathroom_3;
//            img.setImageResource(R.drawable.bathroom_3);
        }
        else if(type_room == 6){
            this.type_room = "Hall";
            img = R.drawable.hall_0;
//            img.setImageResource(R.drawable.hall_0);
        }
        id = count++;

    }

    public Room(String name, int img) {
        id++;
        this.name = name;
        this.img = img;
    }

    public Room(String name) {
        this.name = name;
        id++;
    }

    public static int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(name, room.name);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public String getType_room() {
        return type_room;
    }
}
