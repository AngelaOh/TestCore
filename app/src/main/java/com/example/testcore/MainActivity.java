// TODO: MAP OUT WHAT DATA WILL LOOK LIKE
// TODO: Connect Volley for API Calls

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signinButton;
    private EditText signinName, signinEmail;
    private TextView signinHeader, signinOAuth, showMessages;

    private final int REQUEST_CODE = 123;

    // HASHMAP KEYS
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference firstDocRef = database.collection("first_storage").document("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signinButton = findViewById(R.id.signin_button);
        signinName = findViewById(R.id.signin_name);
        signinEmail = findViewById(R.id.signin_email);
        signinHeader = findViewById(R.id.signin_header);
        signinOAuth = findViewById(R.id.OAuth_header);
        showMessages = findViewById(R.id.show_messages);

        signinButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String name = signinName.getText().toString().trim();
        String email = signinEmail.getText().toString().trim();

        addData(name, email);

        if (view.getId() == R.id.signin_button) {
            showMessages.setText("Sign In Name: " + name + "\n Sign In Email: " + email);

            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("login_name", name);
            startActivityForResult(intent, REQUEST_CODE);
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

    private void addData(String name, String email) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY_NAME, name);
        data.put(KEY_EMAIL, email);

        firstDocRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // doesn't acctually show on screen; what else should I add to see it worked?
                        Toast.makeText(MainActivity.this, "Successfully added to Firestore", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR in Main", "onFailure: " + e.toString());
                    }
                });
    }
}
