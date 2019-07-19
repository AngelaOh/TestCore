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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.data.AnswerListAsyncResponse;
import com.example.testcore.data.FirestoreAsyncResponse;
import com.example.testcore.data.standardBank;
import com.example.testcore.data.standardFirestoreBank;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class ViewStandardsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button displayStandardsButton;
    private TextView contentInfo;
    private String userContent;
    private String userGrade;
    private String standardSetID;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_standards);


        // Instantiate Current User
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(ViewStandardsActivity.this, "Hi, " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    // user is not logged in
                }
            }
        };
        currentUser = firebaseAuth.getCurrentUser();
        contentInfo = findViewById(R.id.content_info);


        // Get userGrade and userContent Info for API/Firestore Retrieval
        database.collection("Users").whereEqualTo("userId", currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");
                    Log.d("user grade gonna pass", "onSuccess: " + userGrade);
                    userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");

                    String courseInfoString = "Grade: " + userGrade + "\n" + "Content: " + userContent;
                    contentInfo.setText(courseInfoString);
                }
            }
        }). addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Jurisdiction fail", "onFailure: " + e.getMessage());
            }
        });
        Intent getInfo =  getIntent();
        Bundle bundle = getInfo.getExtras();
        standardSetID = bundle.get("standard_set_id").toString();
        Log.d("FROM INTENT", "onCreate: " + standardSetID);


        // Instantiate view widgets
        displayStandardsButton = findViewById(R.id.display_standards_button);
        displayStandardsButton.setOnClickListener(this);

        recyclerView = findViewById(R.id.standard_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new standardBank(standardSetID).getStandards(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Standard> standardArrayList) {
                Log.d("List in Activity", "StandardArrayList: " + (standardArrayList));

                for (Standard oneStandard : standardArrayList) {
                    String currentUserId = currentUser.getUid();

                    DocumentReference pathIdTwo = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection("Standards").document("All Standards");
                    DocumentReference pathId = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection(oneStandard.getLabel()).document(oneStandard.getLabel() + " Information");

                    Map<String, Object> one_standard = new HashMap<>();
                    one_standard.put(oneStandard.getLabel(), oneStandard.getDescription());

                    pathId.set(one_standard, SetOptions.merge());
                    pathIdTwo.set(one_standard, SetOptions.merge());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.display_standards_button) {
                displayStandards();
        }
    }

    private void displayStandards() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

        new standardFirestoreBank(userContent, userGrade, currentUserId).getFirestoreStandards(new FirestoreAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Standard> firestoreArrayList) {
                Log.d("ASYNC CHECK", "processFinished: " + firestoreArrayList);
                implementRecyclerView(firestoreArrayList);
            }
        });

    }

    public void implementRecyclerView(ArrayList<Standard> standards_array_check) {
        recyclerViewAdapter = new RecyclerViewAdapter(ViewStandardsActivity.this, standards_array_check);
        Log.d("STANDARDS ARRAY", "implementRecyclerView: " + standards_array_check);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
