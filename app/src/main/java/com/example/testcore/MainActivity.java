package com.example.testcore;

import androidx.annotation.NonNull;
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

import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signinButton;
    private EditText signinName, signinEmail, signinState, signinGrade, signinContent;
    private TextView signinHeader, signinOAuth, showMessages;

    private final int REQUEST_CODE = 123;

    // HASHMAP KEYS
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signinName = findViewById(R.id.signin_name);
        signinEmail = findViewById(R.id.signin_email);
        signinState = findViewById(R.id.signin_state);
        signinGrade = findViewById(R.id.signin_grade);
        signinContent = findViewById(R.id.signin_content);

        signinHeader = findViewById(R.id.signin_header);
        signinOAuth = findViewById(R.id.OAuth_header);
        showMessages = findViewById(R.id.show_messages);

        signinButton = findViewById(R.id.signin_button);

        signinButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String name = signinName.getText().toString().trim();
        String email = signinEmail.getText().toString().trim();
        String state = signinState.getText().toString().trim();
        String grade = signinGrade.getText().toString().trim();
        String content = signinContent.getText().toString().trim();

        Log.d("SEE CONTENT", "onClick: " + (name instanceof String));

        addData(name, email, state, grade, content);

        if (view.getId() == R.id.signin_button) {
            showMessages.setText("Sign In Name: " + name + "\n Sign In Email: " + email + "\n Other Info: " + state + grade + content);

            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("login_name", name);
            intent.putExtra("login_email", email);
            intent.putExtra("login_state", state);
            intent.putExtra("login_grade", grade);
            intent.putExtra("login_content", content);

            startActivity(intent);
//            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            String successMessage = data.getStringExtra("dashboard_callback");
            Toast.makeText(MainActivity.this, successMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void addData(String name, String email, String state, String grade, String content) {

        DocumentReference addUser = database.collection(name).document(name + "Info");
        Map<String, Object> general_data = new HashMap<>();
        general_data.put("NAME", name);
        general_data.put("EMAIL", email);
        general_data.put("STATE", state);
        addUser.set(general_data);

        DocumentReference addContent = database.collection(name).document(name + " Preps");
        Map<String, Object> prep_data = new HashMap<>();
        prep_data.put("CONTENT", content);
        prep_data.put("GRADE", grade);
        addContent.set(prep_data);

//        DocumentReference addStandards = database.collection(name).document("Angela Preps");


//        Map<String, Object> data = new HashMap<>();
//        data.put(KEY_NAME, name);
//        data.put(KEY_EMAIL, email);

//        firstDocRef.set(data)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // doesn't acctually show on screen; what else should I add to see it worked?
//                        Toast.makeText(MainActivity.this, "Successfully added to Firestore", Toast.LENGTH_LONG).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("ERROR in Main", "onFailure: " + e.toString());
//                    }
//                });

    }
}
