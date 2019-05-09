package com.example.smarthome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.R;
import com.example.smarthome.adapter.RoomAdapter;
import com.example.smarthome.pojo.Room;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.Collection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView RoomsRecycleView;
    private RoomAdapter roomAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        initRecycleView();
        loadRooms();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView nm; //Name at the opening menu
        TextView em; //Email at the opening menu
        nm = findViewById(R.id.nav_name);
        em = findViewById(R.id.nav_email);

            //nm.setText(user.getDisplayName());
            //em.setText(user.getEmail());

        //Toast.makeText(getApplicationContext(),user.getDisplayName()+"\n"+user.getEmail(),Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_main:
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();

                a_builder.setView(inflater.inflate(R.layout.dialog_add_room, null));
                a_builder.setMessage("Вы хотите закрыть приложение?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = a_builder.create();
                alertDialog.show();
                Toast.makeText(this, "dfsdf", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void initRecycleView(){
        RoomsRecycleView = findViewById(R.id.rooms_recycler_view);
        RoomsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        RoomsRecycleView.setItemViewCacheSize(6);
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
        return Arrays.asList(
                new Room(23,45,"Кухня", 2),
                new Room(24,65,"Гостинная", 1),
                new Room(21,45,"Спальня", 0),
                new Room(11,77,"Ванная комната", 5),
                new Room(22,66,"Офис", 4),
                new Room(45,77,"Столовая", 3)
        );
    }

    private void loadRooms(){
        Collection<Room> rooms = getRooms();
        roomAdapter.setItems(rooms);
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

