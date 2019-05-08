package com.example.smarthome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarthome.R;
import com.example.smarthome.pojo.Room;

public class room_info extends AppCompatActivity {

    public static final String ROOM_ID = "roomId";

    private TextView humText;
    private TextView tempText;
    private TextView nameText;
    private Button addBtn;
    private ImageView RoomImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        RoomImg = findViewById(R.id.RoomImgInfo);
        humText = findViewById(R.id.hum);
        tempText = findViewById(R.id.temp);
        nameText = findViewById(R.id.RoomNameInfo);
        addBtn = toolbar.findViewById(R.id.action_add_info);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.MainBackgroundColor));
        }



        Intent intent = getIntent();
        Room room = (Room)intent.getSerializableExtra(room_info.ROOM_ID);

        humText.setText(room.getHum() + "%");
        tempText.setText(room.getId() + "°C");
        nameText.setText(room.getName());
        RoomImg.setImageResource(room.getImg());

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

}
