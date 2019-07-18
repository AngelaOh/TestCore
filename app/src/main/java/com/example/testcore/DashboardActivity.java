// TODO: issue with order to volley calls going. need to control order for non-hardcoded url to work
// ---- maybe try doing API calls at different points (on sign in button click, onCreate, on standardsButton click

package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.testcore.controller.MySingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import util.StandardApi;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOutButton, backButton, viewCoursesButton, viewAllButton;
    private TextView welcomeMessage;
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    private String jurisdictionID;
    private String standardSetID;
    private String userGrade;
    private String userContent;
    private String userState;
    private String documentId;


    // Volley
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
        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(DashboardActivity.this, "Hi, " + currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
                    Log.d("Logged in", "onAuthStateChanged: " + currentUser.getUid());

                } else {
                    Log.d("Not Logged in", "onAuthStateChanged: " + currentUser);
                }
            }
        };

        currentUser = firebaseAuth.getCurrentUser();

        // Set jurisdictionId from db
        // get the path to user
        // .get()
        // store info in juridiction ID
        database.collection("Users").whereEqualTo("userId", currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    jurisdictionID = queryDocumentSnapshots.getDocuments().get(0).getString("jurisdictionId");
                    userState = queryDocumentSnapshots.getDocuments().get(0).getString("state");
                    userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");
                    userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");
                    documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    Log.d("DOCUMENT SNAP", "onSuccess: " + queryDocumentSnapshots.getDocuments().get(0).getId());

                }
            }
        }). addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Jurisdiction fail", "onFailure: " + e.getMessage());
            }
        });


        logOutButton = findViewById(R.id.log_out_button);
        backButton = findViewById(R.id.back_button);
        viewCoursesButton = findViewById(R.id.view_courses);
        viewAllButton = findViewById(R.id.view_all_tests);
        welcomeMessage = findViewById(R.id.welcome_message);

        logOutButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        viewCoursesButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);

        welcomeMessage.setText(StandardApi.getInstance().getUsername());

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.log_out_button) {
            Toast.makeText(DashboardActivity.this, "log out button clicked", Toast.LENGTH_LONG).show();
        }

        else if (view.getId() == R.id.back_button) {
            Log.d("checking back button", "onClick: IN BACK BUTTON CLICK LISTENER");
            backButtonMethod();
        } else if (view.getId() == R.id.view_all_tests) {
            Toast.makeText(DashboardActivity.this, "view all button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.view_courses) {
            viewCoursesCall();

        }
    }

    public void backButtonMethod() {
        Intent intent = getIntent();
        intent.putExtra("dashboard_callback", "Successfully went back");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void viewCoursesCall() {

        // TODO: 1. make call to get jurisdiction id
        // TODO: 2. make call to get standard sets id [use: jurisdiction id, content, grade]
        // TODO: 3. make call to get standards [use: standard set id]
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

//        String receivedJurisdictionId = StandardApi.getInstance().getJurisdictionId();
////        Log.d("JURISDICTION ID RETURNER", "viewCoursesCall: " + receivedJurisdictionId);
//
//        final String userGrade = StandardApi.getInstance().getUserGrade();
//        final String userContent = StandardApi.getInstance().getUserContent();
//        final String userName = StandardApi.getInstance().getUsername();

        String standardsSetURL = "https://api.commonstandardsproject.com/api/v1/jurisdictions/" + jurisdictionID + "?api-key=" + standardsApiKey; // get the standards set id

        Log.d("URL CHECK", "viewCoursesCall: " + standardsSetURL);
        JsonObjectRequest standardsIDObject = new JsonObjectRequest(Request.Method.GET, standardsSetURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataWrapper = response.getJSONObject("data");
                            JSONArray standardSets = dataWrapper.getJSONArray("standardSets");
                            Log.d("STANDARD SET", "onResponse: " + standardSets);

                            for (int i = 0; i < standardSets.length(); i ++) {
                                if (standardSets.getJSONObject(i).getString("title").equals("Grade " + userGrade) && standardSets.getJSONObject(i).getString("subject").equals(userContent)) {
                                    standardSetID = standardSets.getJSONObject(i).getString("id");

                                    Map<String, String> standardObj = new HashMap<>();
                                    standardObj.put("standardSetId", standardSetID);

                                    database.collection("Users").document(documentId).set(standardObj, SetOptions.merge());
                                    Log.d("STANDARDSET ID", "onResponse: " + standardSetID);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley Error", "onErrorResponse: " + error.getMessage());
            }
        });

        Log.d("check queue", "viewCoursesCall: " + standardsIDObject);
        queue.add(standardsIDObject);

//        Intent intent = new Intent(getApplicationContext(), ViewStandardsActivity.class);
//        startActivity(intent);
        startActivity(new Intent(DashboardActivity.this, ViewStandardsActivity.class));

    }

        // method to check if course is missing any standards not covered in tests
//    public ArrayList<Standard> checkStandards(Course course) {
//        for (test : course.getTests()) {
//            // check for standards
//        }
//    }
}


