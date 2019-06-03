package com.example.smarthome.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.smarthome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class first_start extends AppCompatActivity {
    private FirebaseUser user;
    private Button Skip;
    private Button btn;
    private EditText HomeId;
    public FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Skip= findViewById(R.id.skip_btn_homeid);
        btn = findViewById(R.id.input_btn_homeid);
        HomeId = findViewById(R.id.editTextHomeId);

        Skip.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(first_start.this, MainActivity.class);
            intent.putExtra("bool", false);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HomeId.getText().toString().equals("")){
                    Snackbar.make(v, "Введите идентификатор", Snackbar.LENGTH_LONG).show();
                }
                else{
                    db.collection("smart_home").document(HomeId.getText().toString()).update("family", FieldValue.arrayUnion(user.getUid())).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(v, "Ошибка! Проверьте правильность введенной информации", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(first_start.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(v.getContext(), "Перезагрузите приложение", Toast.LENGTH_LONG).show();
                }

            }
        });




    }
}
