package com.example.smarthome.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.adapter.SensorAdapter;
import com.example.smarthome.pojo.Room;
import com.example.smarthome.pojo.Sensor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class room_info extends AppCompatActivity {

    public static final String ROOM_ID = "roomId";

    private TextView humText;
    private TextView tempText;
    private TextView nameText;
    public FirebaseFirestore db;
    private ImageView RoomImg;
    private RecyclerView sensorRecyclerView;
    private SensorAdapter sensorAdapter;
    private Room room;
    private String Element_home;
    ProgressBar progressBar;
    private final static String TAG = "My_TAG";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        RoomImg = findViewById(R.id.RoomImgInfo);
        humText = findViewById(R.id.hum);
        tempText = findViewById(R.id.temp);
        nameText = toolbar.findViewById(R.id.RoomNameInfo);
        Button addBtn = toolbar.findViewById(R.id.action_add_info);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.MainBackgroundColor));
        }



        Intent intent = getIntent();
        room = (Room)intent.getSerializableExtra(room_info.ROOM_ID);
        Element_home = intent.getStringExtra("id_home");

        humText.setText(room.getHum() + "%");
        tempText.setText("4" + "°C");
        nameText.setText(room.getName());

        RoomImg.setImageResource(room.getImg());

        db = FirebaseFirestore.getInstance();



        initRecyclerView();
        loadSensor();

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        toolbar.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId() == R.id.action_add_info){
                AlertDialog.Builder a_builder = new AlertDialog.Builder(room_info.this);
                LayoutInflater inflater = getLayoutInflater();
                View myview = inflater.inflate(R.layout.dialog_add_sensor, null);
                a_builder.setView(myview);
                AlertDialog alertDialog = a_builder.create();
                EditText editText = (EditText)myview.findViewById(R.id.name_add_sensor);
                Button menu = (Button)myview.findViewById(R.id.choose_type_sensor);
                Button btn_yes = (Button)myview.findViewById(R.id.btn_yes_add_sensor);
                Button btn_no = (Button)myview.findViewById(R.id.btn_no_add_sensor);
                menu.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(v, myview.getContext(), menu);
                    }
                });
                alertDialog.show();
                CollectionReference collection = db.collection("smart_home").document(Element_home)
                        .collection("rooms").document(room.getId())
                        .collection("sensors");

                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().equals("")){
                            Snackbar.make(myview, "Введите название", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (menu.getText().toString().equals(getResources().getString(R.string.click_me_sensor))){
                            Snackbar.make(myview, "Выберите тип", Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Map<String, Object> sensor_el_map = new HashMap<>();
                            sensor_el_map.put("name", editText.getText().toString());
                            sensor_el_map.put("type", menu.getText().toString());
                            Sensor item_sensor = new Sensor(editText.getText().toString(),  menu.getText().toString());
                            int imageView;
                            switch (menu.getText().toString()){
                                case "Умная розетка":
                                    imageView = R.drawable.ic_socket_on;
                                    break;
                                case "Умная лампа":
                                    imageView = R.drawable.ic_lamp_on;
                                    break;
                                case "Умный чайник":
                                    imageView = R.drawable.ic_kettler_on;
                                    break;
                                default:
                                    imageView = R.drawable.ic_kettler_on;
                                    break;
                            }

                            Log.d(TAG, item_sensor.getName() + " " + item_sensor.getType());
                            collection.add(sensor_el_map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    item_sensor.setId(documentReference.getId());
                                    room.addSensor(item_sensor);
                                }
                            });

                            sensorAdapter.setItems(Arrays.asList(item_sensor));



                            Snackbar.make(v, "Комната успешно добавлена", Snackbar.LENGTH_SHORT).show();
                            alertDialog.cancel();

                        }

                    }
                });
                btn_no.setOnClickListener(v -> alertDialog.cancel());

            }
            return false;
        });

    }

    private Collection<Sensor> getSensors(){
        return Collections.emptyList();
    }

    private void loadSensor(){
        sensorAdapter.setItems(room.GetSensorList());
    }

    private void initRecyclerView(){
        sensorRecyclerView = findViewById(R.id.sensor_recycler_view);
        sensorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorAdapter = new SensorAdapter();
        sensorAdapter.clearItems();
        sensorRecyclerView.setAdapter(sensorAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_toolbar, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPopupMenu(View v, Context cont, Button btn) {
        PopupMenu popupMenu = new PopupMenu(cont, v, Gravity.BOTTOM);
        popupMenu.inflate(R.menu.popupmenu_add_sensor);

        popupMenu
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_socket:
                            btn.setText(R.string.socket);
                            return true;
                        case R.id.menu_lamp:
                            btn.setText(R.string.lamp);
                            return true;
                        case R.id.menu_kettle:
                            btn.setText(R.string.kettle);
                            return true;
                        default:
                            return false;
                    }
                });

        popupMenu.setOnDismissListener(menu -> {

        });
        popupMenu.show();
    }

//    private void GetSensorsFromFireStore(){
////        rooms = new CopyOnWriteArrayList<>();
//        db.collection("smart_home")
//                .document(Element_home)
//                .collection("rooms")
//                .document(((Integer)room.getId()).toString())
//                .collection("sensors")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> map = document.getData();
////                                Room r = new Room("nfmdk");
////                                rooms.add(r);
//                                sensorAdapter.setItems(Arrays.asList(new Sensor(map.get("name").toString(), map.get("type").toString())));
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
////        Log.d(TAG, ((Integer)rooms.size()).toString());
////        for(Room element: rooms){
////            Log.d(TAG, element.getName() + " " + element.getType_room());
////        }
////        return rooms;
//    }

    public Room getRoom() {
        return room;
    }
}
