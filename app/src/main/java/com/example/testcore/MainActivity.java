package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import util.StandardApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView signinHeader;
    private Button createAccountButton, signinButton;
    private EditText emailAddress, password;

    private final int REQUEST_CODE = 123;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailAddress = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        createAccountButton = findViewById(R.id.start_create_account);
        signinButton = findViewById(R.id.signin_button);

        createAccountButton.setOnClickListener(this);
        signinButton.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.start_create_account) {
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        } else {
            loginEmailPasswordUser(emailAddress.getText().toString().trim(),
                    password.getText().toString().trim());
        }

    }

    private void loginEmailPasswordUser(String email, String pwd) {

        // progress bar visible

        // Sign In
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            final String currentUserId = user.getUid();

                            database.collection("Users").whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                // progressbar set invisible

                                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                    StandardApi standardApi = StandardApi.getInstance();
                                                    standardApi.setUsername(snapshot.getString("username"));
                                                    standardApi.setUserId(currentUserId);
                                                    standardApi.setUserGrade(snapshot.getString("grade"));
                                                    standardApi.setUserContent(snapshot.getString("content"));
                                                    standardApi.setJurisdictionId(snapshot.getString("jurisdictionId"));

                                                    // Go to Dashboard Activity
                                                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                                }
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // progress bar set invisible
                        }
                    });
        } else {
            // progress bar set invisible

            Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_LONG).show();
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
