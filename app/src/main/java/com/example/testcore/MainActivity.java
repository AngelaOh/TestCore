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
//        createAccountButton.setOnClickListener(this);

//        createAccountName = findViewById(R.id.create_account_name);
//        createAccountEmail = findViewById(R.id.create_account_email);
//        createAccountState = findViewById(R.id.create_account_state);
//        createAccountGrade = findViewById(R.id.create_account_grade);
//        createAccountContent = findViewById(R.id.create_account_content);
//
//        createAccountHeader = findViewById(R.id.create_account_header);
//        signinHeader = findViewById(R.id.signin_header);
//
//        createAccountnButton = findViewById(R.id.create_account_button);
//
//        createAccountnButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.start_create_account) {
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        }

//        String name = createAccountName.getText().toString().trim();
//        String email = createAccountEmail.getText().toString().trim();
//        String state = createAccountState.getText().toString().trim();
//        String grade = createAccountGrade.getText().toString().trim();
//        String content = createAccountContent.getText().toString().trim();
//
//        Log.d("SEE CONTENT", "onClick: " + (name instanceof String));
//
//        addData(name, email, state, grade, content);
//
//        if (view.getId() == R.id.create_account_button) {
//
//            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//            intent.putExtra("login_name", name);
//            intent.putExtra("login_email", email);
//            intent.putExtra("login_state", state);
//            intent.putExtra("login_grade", grade);
//            intent.putExtra("login_content", content);
//
//            startActivity(intent);
////            startActivityForResult(intent, REQUEST_CODE);
//        }

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

//    private void addData(String name, String email, String state, String grade, String content) {
//
//        DocumentReference addUser = database.collection(name).document(name + "Info");
//        Map<String, Object> general_data = new HashMap<>();
//        general_data.put("NAME", name);
//        general_data.put("EMAIL", email);
//        general_data.put("STATE", state);
//        addUser.set(general_data);
//
//        DocumentReference addContent = database.collection(name).document(name + " Preps");
//        Map<String, Object> prep_data = new HashMap<>();
//        prep_data.put("CONTENT", content);
//        prep_data.put("GRADE", grade);
//        addContent.set(prep_data);
//
//    }
}
