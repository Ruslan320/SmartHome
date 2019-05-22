package com.example.smarthome.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.smarthome.MQTT.MqttHelper;
import com.example.smarthome.R;
import com.example.smarthome.adapter.RoomAdapter;
import com.example.smarthome.pojo.Room;
import com.example.smarthome.pojo.Sensor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener{

    MqttHelper mqttHelper;
    private RecyclerView RoomsRecycleView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RoomAdapter roomAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseFirestore db;
    public static String Element_home;
    private static final String TAG = "My_TAG";
    private Collection<Room> rooms;
    ProgressBar progressBar;
    private ImageView im;
    private FirebaseUser user;
    private Intent notificationIntent;
    private PendingIntent contentIntent;
    private NotificationCompat.Builder builder;
    private static final short NOTIFY_ID = 101;

    long[] time = new long[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time[0]=System.currentTimeMillis();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);



        time[1]=System.currentTimeMillis();
        //if(user.getDisplayName()!=null) nm.setText(user.getDisplayName());
        //if(user.getEmail()!=null) em.setText(user.getEmail());


        initRecycleView();

        String s = user.getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("smart_home").whereArrayContains("family", s).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(Objects.requireNonNull(task).getResult())) {
                    Element_home = document.getId();
                    loadRooms();
                    Log.d("My_TAG", document.getId());
                }
            } else {
                Log.d("My_TAG", "Error getting documents: ", task.getException());
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        });
        checkingIfusernameExist(s);
        time[2]=System.currentTimeMillis();



        //Запуск MQTT сервера
        startMqtt();
        time[3]=System.currentTimeMillis();

        //Создание объекта базы данных
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        TextView nm = headerLayout.findViewById(R.id.nav_name);
        TextView em = headerLayout.findViewById(R.id.nav_email);
        im = headerLayout.findViewById(R.id.nav_img);
        for (UserInfo profile : user.getProviderData()) {

            // Name, email address
            String name = profile.getDisplayName();
            String email = profile.getEmail();
            Uri uri = profile.getPhotoUrl();

            nm.setText(name);
            em.setText(email);
            im.setImageURI(uri);

        }
        navigationView.setNavigationItemSelectedListener(this);

        time[4]=System.currentTimeMillis();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "testid");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "testName");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        notificationIntent = new Intent(this, MainActivity.class);
        contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        builder = new NotificationCompat.Builder(this);

            //nm.setText(user.getDisplayName());
            //em.setText(user.getEmail());

        //Toast.makeText(getApplicationContext(),user.getDisplayName()+"\n"+user.getEmail(),Toast.LENGTH_LONG).show();
        time[5]=System.currentTimeMillis();
        String a="";
        for (int i =1 ; i<6 ; i++){
            a+=time[i]-time[0]+"\n";
        }
        Toast.makeText(getApplicationContext(),a ,Toast.LENGTH_LONG).show();
    }

    //Проверка на наличие пользователя в базе и добавление
    private void checkingIfusernameExist(final String usernameToCompare) {

        final Query mQuery = db.collection("smart_home").whereArrayContains("family", usernameToCompare);
        mQuery.get().addOnCompleteListener(task -> {
            Log.d("TAG", "checkingIfusernameExist: checking if username exists");
            if (task.isSuccessful()) {
                for (DocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                    List<String> userNames;
                    userNames = (List<String>)ds.get("family");
                    for(int i = 0; i < userNames.size(); i++){
                        Log.d("TAG", userNames.get(i));
                        if (userNames.get(i).equals(usernameToCompare)) {
                            Log.d("TAG", "checkingIfusernameExist: FOUND A MATCH -username already exists");
                            Toast.makeText(MainActivity.this, "username already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            //checking if task contains any payload. if no, then update
            if (Objects.requireNonNull(task.getResult()).size() == 0) {
                try {

                    Log.d("TAG", "onComplete: MATCH NOT FOUND - username is available");
                    Toast.makeText(MainActivity.this, "username changed", Toast.LENGTH_SHORT).show();
                    //Updating new username............
//                    Map<String, Object> user_home = new HashMap<>();
//                    user_home.put("family", Collections.singletonList(usernameToCompare));
//                    db.collection("smart_home").add(user_home);


                } catch (NullPointerException e) {
                    Log.e("TAG", "NullPointerException: " + e.getMessage());
                }
            }
        });
    }

    //MQTT Сервер для связи с датчиками
    private synchronized void startMqtt(){
        new Thread(() -> {

    mqttHelper = new MqttHelper(getApplicationContext());
    mqttHelper.setCallback(new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean b, String s) {
            Toast.makeText(MainActivity.this, "Подключение к MQTT серверу произошло успешно", LENGTH_LONG).show();
        }

        @Override
        public void connectionLost(Throwable throwable) {
            Toast.makeText(MainActivity.this, "Ошибка подключения к MQTT серверу", LENGTH_LONG).show();
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            //mqttMessage.toString() = содержимое сообщения, топик = топик
            //Toast.makeText(MainActivity.this, topic +"\n"+ mqttMessage.toString(), LENGTH_LONG).show();   //Для отладки
            //mqttHelper.publish("hi","mayBe345iuljkl6");  //Для отправки данных

            //Проверка полученных данных, где i это номер комнаты, s это тип датчика, mqttMessage.toString() само значение с датчика
            Short i; String s="";
            if(topic.contains("sensors/") && topic.indexOf("sensors/")==0)try {
                topic = topic.replace("sensors/","");
                i = Short.valueOf(topic.substring(0,topic.indexOf("/")));
                s = topic.replace(i.toString()+"/","");
                //Toast.makeText(MainActivity.this, s +"\n"+ mqttMessage.toString(), LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                Log.e("Error", "Получена не венрая информация с датчика");
            }

            if(s.equals("secure") && mqttMessage.toString().equals("break-in")){
                Toast.makeText(MainActivity.this, s +"\n"+ mqttMessage.toString(), LENGTH_LONG).show();
                builder.setContentIntent(contentIntent)
                        // обязательные настройки
                        .setSmallIcon(R.drawable.logo)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                        .setContentTitle("Сигнализация")
                        //.setContentText(res.getString(R.string.notifytext))
                        .setContentText("Сработка Сигнализации") // Текст уведомления
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                        .setTicker("Опасно!")
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFY_ID, builder.build());
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    });
        }).run();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        synchronized (this){
        // Handle item selection
            if (item.getItemId() == R.id.action_add_main) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View myview = inflater.inflate(R.layout.dialog_add_room, null);
                a_builder.setView(myview);
                AlertDialog alertDialog = a_builder.create();
                EditText editText = (EditText)myview.findViewById(R.id.name_add_room);
                editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
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
                            room_el_map.put("type", menu.getText().toString());
                            Room item_room = new Room(45, 345, editText.getText().toString(), menu.getText().toString());
                            collection.add(room_el_map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    item_room.setId(documentReference.getId());
                                }
                            });

                            roomAdapter.setItems(Arrays.asList(item_room));
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
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public synchronized void GetRoomsFromFireStore(){
        new Thread(() -> {

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
                                    Room room = new Room(45, 45, map.get("name").toString(), map.get("type").toString(), document.getId());
                                    db.collection("smart_home")
                                            .document(Element_home)
                                            .collection("rooms")
                                            .document(document.getId()).collection("sensors")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSen : task.getResult()) {
                                                            Map<String, Object> mapSen = documentSen.getData();
                                                            room.addSensor(new Sensor(mapSen.get("name").toString(), mapSen.get("type").toString(), documentSen.getId(), (Boolean)mapSen.get("on")));
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
//                                Room r = new Room("nfmdk");
//                                rooms.add(r);
                                    roomAdapter.setItems(Arrays.asList(room));
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }).start();
        }



    private void initRecycleView(){
        RoomsRecycleView = findViewById(R.id.rooms_recycler_view);
        RoomsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        RoomsRecycleView.setItemViewCacheSize(20);
//        RoomsRecycleView.setDrawingCacheEnabled(true);
//        RoomsRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        RoomsRecycleView.setHasFixedSize(true);

        RoomAdapter.OnRoomClickListener onRoomClickListener = room -> {
            Intent intent = new Intent(MainActivity.this, room_info.class);
            intent.putExtra(room_info.ROOM_ID, room);
            intent.putExtra("id_home", Element_home);

            startActivity(intent);
        };
        RoomAdapter.OnRoomLongClickListener onRoomLongClickListener = room -> {

        };

        roomAdapter = new RoomAdapter(onRoomClickListener, onRoomLongClickListener);
        new Thread(() -> RoomsRecycleView.setAdapter(roomAdapter)).start();
    }

    private Collection<Room> getRooms(){
        return Collections.singletonList(new Room(45, 435, "fsa", "bedroom"));
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
                .setOnMenuItemClickListener(item -> {
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
                });

        popupMenu.setOnDismissListener(menu -> {

        });
        popupMenu.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        im = headerLayout.findViewById(R.id.nav_img);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageReturnedIntent.getData())
                        .build();

                user.updateProfile(profileUpdates);
                im.setImageURI(imageReturnedIntent.getData());
            }
        }
    }

    @Override
    public void onRefresh() {
        roomAdapter.clearItems();
        loadRooms();
        mSwipeRefreshLayout.setRefreshing(false);
    }


}

