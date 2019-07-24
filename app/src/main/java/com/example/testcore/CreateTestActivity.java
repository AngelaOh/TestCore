// TODO: Add View to See Current Test; Bottom of Screen Recycler View
// TODO: figure out way to replace test title input once submitted
package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.data.FirestoreAsyncResponse;
import com.example.testcore.data.standardFirestoreBank;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateTestActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private String userGrade;
    private String userContent;
    private Button titleSubmitButton;
    private EditText testTitle;
    private TextView titleAfterSubmit;
    private String titleChangeId;
    private String testId;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);

        recyclerView = findViewById(R.id.standard_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();


        Intent getInfo =  getIntent();
            Bundle bundle = getInfo.getExtras();
            userGrade = bundle.get("user_grade").toString();
            userContent = bundle.get("user_content").toString();
            testId = bundle.get("test_id").toString();


        new standardFirestoreBank(userContent, userGrade, currentUserId).getFirestoreStandards(new FirestoreAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Standard> firestoreArrayList) {
                implementRecyclerView(firestoreArrayList);
            }
        });

        titleSubmitButton = findViewById(R.id.title_submit_button);
        testTitle = findViewById(R.id.test_title);
        titleAfterSubmit = findViewById(R.id.test_title_after_submit);
        titleSubmitButton.setOnClickListener(this);

        database.collection("Tests").document(testId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String firebaseTitle = documentSnapshot.getString("Test Title");
                        if (firebaseTitle == null) {
                            testTitle.setVisibility(View.VISIBLE);
                            titleSubmitButton.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        } else {

                            testTitle.setVisibility(View.INVISIBLE);
                            titleSubmitButton.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void implementRecyclerView(ArrayList<Standard> standards_array_check) {
        recyclerViewAdapter = new RecyclerViewAdapter(CreateTestActivity.this, standards_array_check, testId);
        Log.d("STANDARDS ARRAY", "implementRecyclerView: " + standards_array_check);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_test_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.return_to_dashboard:
                startActivity(new Intent(CreateTestActivity.this, DashboardActivity.class));
                break;

            case R.id.sign_out:
                logOutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null && firebaseAuth != null) {
            Log.d("HIIIIII", "logOutUser: ");

            firebaseAuth.signOut();
            startActivity(new Intent(CreateTestActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_submit_button) {
            final String titleForFirebase = testTitle.getText().toString().trim();
//            final String titleFROMTFirebae;
            String currentUserId = firebaseAuth.getCurrentUser().getUid();

            database.collection("Tests").document(testId).update("Test Title", titleForFirebase)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(CreateTestActivity.this, "Title Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Edit Test Title", "onFailure: " + e.getMessage());
                        }
                    });

            // get rid of title input and replace with actual title

                testTitle.setVisibility(View.INVISIBLE);
                titleSubmitButton.setVisibility(View.INVISIBLE);
                titleAfterSubmit.setText(titleForFirebase);
                recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
