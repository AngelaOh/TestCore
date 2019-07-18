package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.controller.MySingleton;
import com.example.testcore.data.standardBank;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.StandardApi;

public class ViewStandardsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getStandardsButton;
    private Button displayStandardsButton;
    public ArrayList<Standard> standards_array_check = new ArrayList<>();
    private String userContent;
    private String userGrade;
    private String standardSetID;
    private String documentId;

    // private TextView standardsView; // don't need this anymore

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    // Volley
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    RequestQueue queue;
    String practiceAPIStandard;

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

        database.collection("Users").whereEqualTo("userId", currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");
                    userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");
                    documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    standardSetID = queryDocumentSnapshots.getDocuments().get(0).getString("standardSetId");
                    Log.d("DOCUMENT SNAP", "onSuccess: " + queryDocumentSnapshots.getDocuments().get(0).getId());

                }
            }
        }). addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Jurisdiction fail", "onFailure: " + e.getMessage());
            }
        });

        //standardsView = findViewById(R.id.standards_view);
        getStandardsButton = findViewById(R.id.get_standards_button);
        getStandardsButton.setOnClickListener(this);
        displayStandardsButton = findViewById(R.id.display_standards_button);
        displayStandardsButton.setOnClickListener(this);


        recyclerView = findViewById(R.id.standard_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new standardBank().getStandards();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.get_standards_button) {

            getStandards();
            displayStandards();

        } else if (view.getId() == R.id.display_standards_button) {

            implementRecyclerView();
        }

    }

    private void getStandards() {
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        String waStandardsURL = "https://api.commonstandardsproject.com/api/v1/standard_sets/"+ standardSetID + "?api-key=" + standardsApiKey; // get the standards
        JsonObjectRequest finalStandardsObject = new JsonObjectRequest(Request.Method.GET, waStandardsURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataWrapper = response.getJSONObject("data");
                            JSONObject standardsWrapper = dataWrapper.getJSONObject("standards");
                            JSONArray standardsKeys = standardsWrapper.names();

                            Log.d("STANDARDS WRAPPER", "onResponse: " + standardsWrapper);
                            Log.d("STANDARDS WRAPPER KEYS", "onResponse: " + standardsKeys);

                            for (int i = 0; i < standardsWrapper.length(); i ++) {
                                String key = standardsKeys.getString(i);
                                if (standardsWrapper.getJSONObject(key).getInt("depth") == 3) {
                                    Log.d("ALL DESCRIPTIONS", "onResponse: " + standardsWrapper.getJSONObject(key).getString("description"));

                                    String description = standardsWrapper.getJSONObject(key).getString("description");
                                    String label = standardsWrapper.getJSONObject(key).getString("listId");

                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    assert currentUser != null;
                                    String currentUserId = currentUser.getUid();

                                    DocumentReference pathIdTwo = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection("Standards").document("All Standards");
                                    DocumentReference pathId = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection(label).document(label + " Information");

                                    Map<String, Object> one_standard = new HashMap<>();
                                    one_standard.put(label, description);

                                    pathId.set(one_standard, SetOptions.merge());
                                    pathIdTwo.set(one_standard, SetOptions.merge());
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(finalStandardsObject);
    }

    private void displayStandards() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();
        Log.d("CURRENT USER CHECK", "displayStandards: " + currentUserId);

        DocumentReference pathIdTwo = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection("Standards").document("All Standards");
        pathIdTwo.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            Map<String, Object> data = documentSnapshot.getData();
                            Set<String> keys = data.keySet();
                            Object[] key_array = keys.toArray();

                            for (int i = 0; i < key_array.length; i += 1) {
                                Object standard = data.get(key_array[i]);
                                Log.d("KEY'S VALUE ", "onSuccess: " + standard);

                                Standard newStandard = new Standard(key_array[i].toString(), standard.toString());
                                newStandard.setLabel(key_array[i].toString());
                                newStandard.setDescription(standard.toString());
                                Log.d("check object", "onSuccess: " + newStandard.getLabel());

                                standards_array_check.add(newStandard);

                            }
//                             standardsView.setText(data.toString());
                            Log.d("get standards", "onSuccess: standards_array " + standards_array_check);
                            Toast.makeText(ViewStandardsActivity.this, "successfully retrieved standards", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Standards Retrieval", "onSuccess: Document snapshot is empty!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Standards Retrieval", "onSuccess: Failed to retrieve standards!");
                    }
                });


    }

    public void implementRecyclerView() {
        // set up adapter
        recyclerViewAdapter = new RecyclerViewAdapter(ViewStandardsActivity.this, standards_array_check);
        Log.d("STANDARDS ARRAY", "implementRecyclerView: " + standards_array_check);

        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
