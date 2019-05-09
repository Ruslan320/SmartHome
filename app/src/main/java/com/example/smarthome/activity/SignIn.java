package com.example.smarthome.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.smarthome.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class SignIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


    }

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build());

    // Create and launch sign-in intent
    public void onMyButtonClick(View view)  {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);

        Toast.makeText(getApplicationContext(), String.valueOf(resultCode) , Toast.LENGTH_SHORT).show();

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == 0) {
                startActivityForResult(intent, 1);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Вы Успешно Залогинились", Toast.LENGTH_SHORT);
                toast.show();
            } else {

            }
        }
    }
}
