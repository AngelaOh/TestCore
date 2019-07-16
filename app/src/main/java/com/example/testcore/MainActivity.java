package com.example.testcore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    private Button createAccountnButton;
//    private EditText createAccountName, createAccountEmail, createAccountState, createAccountGrade, createAccountContent;
//    private TextView createAccountHeader;
    private TextView signinHeader;
    private Button createAccountButton;

    private final int REQUEST_CODE = 123;

    // Connection to Firestore
//    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAccountButton = findViewById(R.id.start_create_account);
        createAccountButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.start_create_account) {
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE) {
//            String successMessage = data.getStringExtra("dashboard_callback");
//            Toast.makeText(MainActivity.this, successMessage, Toast.LENGTH_LONG).show();
//        }
//    }

}
