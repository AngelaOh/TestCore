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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.data.FirestoreAsyncResponse;
import com.example.testcore.data.standardFirestoreBank;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        titleSubmitButton.setOnClickListener(this);
    }

    public void implementRecyclerView(ArrayList<Standard> standards_array_check) {
        recyclerViewAdapter = new RecyclerViewAdapter(CreateTestActivity.this, standards_array_check, testId);
        Log.d("STANDARDS ARRAY", "implementRecyclerView: " + standards_array_check);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_submit_button) {
            String newTitle;
            final String titleForFirebase = testTitle.getText().toString().trim();
            String currentUserId = firebaseAuth.getCurrentUser().getUid();

            database.collection("Tests").whereEqualTo("Course Id", userContent + ": " + userGrade + ": " + currentUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            titleChangeId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Log.d("Check Course Id", "onSuccess: " + titleChangeId);


                            database.collection("Tests").document(titleChangeId).update("Test Title", titleForFirebase)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Title Updated", "onSuccess: " + aVoid);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Title Did Not Update", "onFailure: " + e.getMessage());
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }
}
