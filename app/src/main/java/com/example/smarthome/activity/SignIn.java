package com.example.smarthome.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.smarthome.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    public static final String SP_NAME = "spName";
    public static final String SP_KEY_FIRST_START = "spKeyFirstStart";


    @Override
    protected void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.my_signin)
                .setEmailButtonId(R.id.mailJoin)
                .setPhoneButtonId(R.id.telJoin)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(customLayout)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);



    }

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build());

    // Create and launch sign-in intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent();

        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean firstStart = sp.getBoolean(SP_KEY_FIRST_START, true);
        if(firstStart) {
            sp.edit().putBoolean(SP_KEY_FIRST_START, false).apply();
            intent.setClass(this, first_start.class);
        } else {
            intent.setClass(this, MainActivity.class);
        }

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                startActivity(intent);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Toast.makeText(getApplicationContext(),user.getEmail().toString(),Toast.LENGTH_LONG).show();

                TextView tEmail,tName;
                tEmail = findViewById(R.id.nav_email);
                tName = findViewById(R.id.nav_name);


            } else {

            }
        }
    }
}
