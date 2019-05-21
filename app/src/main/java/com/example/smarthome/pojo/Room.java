package com.example.smarthome.pojo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

import com.example.smarthome.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room  implements Serializable{
    private static int count = 0;
    private String id;
    private int temp;
    private int hum;
    private String type_room;
    private String name;
    private int img;
//    private static  List<Integer> TypeList = new ArrayList<>(7);
//    private static List<List<Integer>> RoomImgArr = Arrays.asList(
//            new ArrayList<Integer>(Arrays.asList(R.drawable.bedroom_0, R.drawable.bedroom_0, R.drawable.bedroom_0))
//    )
    private List<Sensor> SensorList = new ArrayList<>();

    public Room(int temp, int hum, String name, String type_room, String id) {
        this.temp = temp;
        this.hum = hum;
        this.name = name;

        if(type_room.equals("Спальня")){
            this.type_room = "bedroom";
            img = R.drawable.bedroom;
//            TypeList.set(0, TypeList.get(0)+1);
        }
        else if(type_room.equals("Гостинная")){
            this.type_room = "living_room";
            img = R.drawable.living_room;
//            img.setImageResource(R.drawable.living_room_2);
        }
        else if(type_room.equals("Кухня")){
            this.type_room = "kitchen";
            img = R.drawable.kitchen;
//            img.setImageResource(R.drawable.kitchen_0);
        }
        else if(type_room.equals("Столовая")){
            this.type_room = "dining_room_3";
            img = R.drawable.dining_room;
//            img.setImageResource(R.drawable.dining_room_3);
        }
        else if(type_room.equals("Офис")){
            this.type_room = "office";
            img = R.drawable.office;
//            img.setImageResource(R.drawable.office_3);
        }
        else if(type_room.equals("Ванная комната")){
            this.type_room = "bathroom";
            img = R.drawable.bathroom;
//            img.setImageResource(R.drawable.bathroom_3);
        }
        else if(type_room.equals("Коридор")){
            this.type_room = "hall";
            img = R.drawable.hall_2;
//            img.setImageResource(R.drawable.hall_0);
        }
        this.id = id;

    }

    public Room(int temp, int hum, String name, String type_room) {
        this.temp = temp;
        this.hum = hum;
        this.name = name;

        if(type_room.equals("Спальня")){
            this.type_room = "bedroom";
            img = R.drawable.bedroom;
//            TypeList.set(0, TypeList.get(0)+1);
        }
        else if(type_room.equals("Гостинная")){
            this.type_room = "living_room";
            img = R.drawable.living_room;
//            img.setImageResource(R.drawable.living_room_2);
        }
        else if(type_room.equals("Кухня")){
            this.type_room = "kitchen";
            img = R.drawable.kitchen;
//            img.setImageResource(R.drawable.kitchen_0);
        }
        else if(type_room.equals("Столовая")){
            this.type_room = "dining_room_3";
            img = R.drawable.dining_room;
//            img.setImageResource(R.drawable.dining_room_3);
        }
        else if(type_room.equals("Офис")){
            this.type_room = "office";
            img = R.drawable.office;
//            img.setImageResource(R.drawable.office_3);
        }
        else if(type_room.equals("Ванная комната")){
            this.type_room = "bathroom";
            img = R.drawable.bathroom;
//            img.setImageResource(R.drawable.bathroom_3);
        }
        else if(type_room.equals("Коридор")){
            this.type_room = "hall";
            img = R.drawable.hall_2;
//            img.setImageResource(R.drawable.hall_0);
        }

    }

    public Room(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public Room(String name) {
        this.name = name;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void addSensor(Sensor sensor){
        SensorList.add(sensor);
    }

    public void removeSensor(Sensor sensor){
        SensorList.remove(sensor);
    }

    public List<Sensor> GetSensorList(){
        return SensorList;
    }

    public int GetSensorListSize(){
        return SensorList.size();
    }
}
