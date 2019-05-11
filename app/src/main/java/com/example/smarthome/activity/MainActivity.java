package com.example.smarthome.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.MQTT.MqttHelper;
import com.example.smarthome.R;
import com.example.smarthome.adapter.RoomAdapter;
import com.example.smarthome.pojo.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    MqttHelper mqttHelper;
    private RecyclerView RoomsRecycleView;
    private RoomAdapter roomAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseFirestore db;
    private String Element_home;
    private static final String TAG = "My_TAG";
    private Collection<Room> rooms;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView nm; //Name at the opening menu
        TextView em; //Email at the opening menu
        nm = findViewById(R.id.nav_name);
        em = findViewById(R.id.nav_email);

        initRecycleView();
        String s = user.getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("smart_home").whereArrayContains("family", s).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Element_home = document.getId();
                        loadRooms();

                    }
                } else {
                    Log.d("My_TAG", "Error getting documents: ", task.getException());
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });

        checkingIfusernameExist(s);

        //Запуск MQTT сервера
        startMqtt();
        //Создание объекта базы данных
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "testid");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "testName");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);










            //nm.setText(user.getDisplayName());
            //em.setText(user.getEmail());

        //Toast.makeText(getApplicationContext(),user.getDisplayName()+"\n"+user.getEmail(),Toast.LENGTH_LONG).show();
    }

    //Проверка на наличие пользователя в базе и добавление
    private void checkingIfusernameExist(final String usernameToCompare) {

        final Query mQuery = db.collection("smart_home").whereArrayContains("family", usernameToCompare);
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("TAG", "checkingIfusernameExist: checking if username exists");
                if (task.isSuccessful()) {
                    for (DocumentSnapshot ds : task.getResult()) {
                        List<String> group = (List<String>)ds.get("family");
                        String userNames = group.get(0);
                        Log.d("TAG", userNames);
                        if (userNames.equals(usernameToCompare)) {
                            Log.d("TAG", "checkingIfusernameExist: FOUND A MATCH -username already exists");
                            Toast.makeText(MainActivity.this, "username already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                //checking if task contains any payload. if no, then update
                if (task.getResult().size() == 0) {
                    try {

                        Log.d("TAG", "onComplete: MATCH NOT FOUND - username is available");
                        Toast.makeText(MainActivity.this, "username changed", Toast.LENGTH_SHORT).show();
                        //Updating new username............
                        Map<String, Object> user_home = new HashMap<>();
                        user_home.put("family", Arrays.asList(usernameToCompare));
                        db.collection("smart_home").add(user_home);


                    } catch (NullPointerException e) {
                        Log.e("TAG", "NullPointerException: " + e.getMessage());
                    }
                }
            }
        });
    }

    //MQTT Сервер для связи с датчиками
    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                //dataReceived.setText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_main:
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View myview = inflater.inflate(R.layout.dialog_add_room, null);
                a_builder.setView(myview);
                AlertDialog alertDialog = a_builder.create();
                EditText editText = (EditText)myview.findViewById(R.id.name_add_room);
                Button menu = (Button)myview.findViewById(R.id.choose_type_room);
                Button btn_yes = (Button)myview.findViewById(R.id.btn_yes_add_room);
                Button btn_no = (Button)myview.findViewById(R.id.btn_no_add_room);
                menu.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(v, myview.getContext(), menu);
                    }
                });
                alertDialog.show();
                CollectionReference collection = db.collection("smart_home").document(Element_home).collection("rooms");
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().equals("")){
                            Snackbar.make(myview, "Введите название", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (menu.getText().toString().equals(getResources().getString(R.string.click_me))){
                            Snackbar.make(myview, "Выберите тип", Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Map<String, Object> room_el_map = new HashMap<>();
                            room_el_map.put("name", editText.getText().toString());
                            room_el_map.put("type", menu.getText());
                            collection.add(room_el_map);
                            roomAdapter.setItems(Arrays.asList(new Room(45, 345, editText.getText().toString(), menu.getText().toString())));

                            alertDialog.cancel();
                            Snackbar.make(v, "Комната успешно добавлена", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null) {return;}
//        String smart_home_id = data.getStringExtra("smart_home_id");
//        //Toast.makeText(this, smart_home_id, Toast.LENGTH_LONG).show();
//    }


    private void GetRoomsFromFireStore(){
//        rooms = new CopyOnWriteArrayList<>();
        db.collection("smart_home")
                .document(Element_home)
                .collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
//                                Room r = new Room("nfmdk");
//                                rooms.add(r);
                                roomAdapter.setItems(Arrays.asList(new Room(45, 45, map.get("name").toString(), map.get("type").toString())));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

//        Log.d(TAG, ((Integer)rooms.size()).toString());
//        for(Room element: rooms){
//            Log.d(TAG, element.getName() + " " + element.getType_room());
//        }
//        return rooms;
    }

    private void initRecycleView(){
        RoomsRecycleView = findViewById(R.id.rooms_recycler_view);
        RoomsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        RoomsRecycleView.setItemViewCacheSize(20);
//        RoomsRecycleView.setDrawingCacheEnabled(true);
//        RoomsRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        RoomsRecycleView.setHasFixedSize(true);

        RoomAdapter.OnRoomClickListener onRoomClickListener = new RoomAdapter.OnRoomClickListener() {
            @Override
            public void onRoomClick(Room room) {
                Intent intent = new Intent(MainActivity.this, room_info.class);
                intent.putExtra(room_info.ROOM_ID, room);
                startActivity(intent);
            }
        };

        roomAdapter = new RoomAdapter(onRoomClickListener);
        RoomsRecycleView.setAdapter(roomAdapter);
    }

    private Collection<Room> getRooms(){
        return Arrays.asList(new Room(45, 435, "fsa", "bedroom"));
    }

    private void loadRooms(){
        GetRoomsFromFireStore();
//        rooms = getRooms();
//        roomAdapter.setItems(rooms);
    }

    //Показ выпадающего меню
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPopupMenu(View v, Context cont, Button btn) {
        PopupMenu popupMenu = new PopupMenu(cont, v, Gravity.BOTTOM);
        popupMenu.inflate(R.menu.popupmenu_add_room);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_bathroom:
                                btn.setText(R.string.bathroom);
                                return true;
                            case R.id.menu_bedroom:
                                btn.setText(R.string.bedroom);
                                return true;
                            case R.id.menu_dining_room:
                                btn.setText(R.string.dining_room);
                                return true;
                            case R.id.menu_hall:
                                btn.setText(R.string.hall);

                                return true;
                            case R.id.menu_kitchen:
                                btn.setText(R.string.kitchen);

                                return true;
                            case R.id.menu_living_room:
                                btn.setText(R.string.living_room);

                                return true;
                            case R.id.menu_office:
                                btn.setText(R.string.office);

                                return true;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

