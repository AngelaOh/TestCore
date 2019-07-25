package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button  backButton, viewCoursesButton, viewStandardCoverageButton;
    private TextView welcomeMessage;
    private TextView stateCard;
    private TextView gradeCard;
    private TextView contentCard;
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    private String jurisdictionID;
    private String standardSetID;
    private String userName;
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
        Log.d("check email", "onCreate: " + currentUser.getEmail());
        Log.d("check uid", "onCreate: " + currentUser.getUid());

        // Set jurisdictionId from db
        // get the path to user
        // .get()
        // store info in juridiction ID
//        Log.d("User??", "onCreate: " + currentUser.getUid());
        database.collection("Users").whereEqualTo("userId", currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    userName = queryDocumentSnapshots.getDocuments().get(0).getString("username");
                    String welcomeText = "Welcome, " + userName;
                    welcomeMessage.setText(welcomeText);

                    userState = queryDocumentSnapshots.getDocuments().get(0).getString("state");
                    String userText = "State: " + userState;
                    stateCard.setText(userText);

                    userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");
                    String gradeText = "Grade: " + userGrade;
                    gradeCard.setText(gradeText);

                    userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");
                    String contentText = "Content: " + userContent;
                    contentCard.setText(contentText);

                    jurisdictionID = queryDocumentSnapshots.getDocuments().get(0).getString("jurisdictionId");

//                    Log.d("jurisdicition id", "onCreate: " + jurisdictionID);

                    documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    // Log.d("DOCUMENT SNAP", "onSuccess: " + queryDocumentSnapshots.getDocuments().get(0).getId());
                }
            }
        }). addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Jurisdiction fail", "onFailure: " + e.getMessage());
            }
        });

//        Log.d("jurisdicition id", "onCreate: " + jurisdictionID);

        backButton = findViewById(R.id.back_button);
        viewCoursesButton = findViewById(R.id.view_create_tests);
        viewStandardCoverageButton = findViewById(R.id.view_standards_coverage_button);
        welcomeMessage = findViewById(R.id.welcome_message);
        stateCard = findViewById(R.id.show_state_text);
        gradeCard = findViewById(R.id.show_grade_text);
        contentCard = findViewById(R.id.show_content_text);

        backButton.setOnClickListener(this);
        viewCoursesButton.setOnClickListener(this);
        viewStandardCoverageButton.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.new_test:
                // go to new test
                CollectionReference testCollection = database.collection("Tests");
                Map<String, Object> testObj = new HashMap<>();
                testObj.put("Questions", new ArrayList<>());
                testObj.put("Course Id", userContent + ": " + userGrade + ": " + firebaseAuth.getCurrentUser().getUid());
//                testObj.put("Test Title", "Some Test Title");
                testCollection.add(testObj)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // go to Create Test Activity
                                Intent intent = new Intent(DashboardActivity.this, CreateTestActivity.class);
                                intent.putExtra("user_content", userContent);
                                intent.putExtra("user_grade", userGrade);
                                intent.putExtra("test_id", documentReference.getId());

                                startActivity(intent);
                            }
                        });
                break;
            case R.id.sign_out:
                logOutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_button) {
            backButtonMethod();
        } else if (view.getId() == R.id.view_standards_coverage_button) {

            database.collection("Users").whereEqualTo("userId", currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String standardSetId = queryDocumentSnapshots.getDocuments().get(0).getString("standardSetId");

                            Intent intent = new Intent(DashboardActivity.this, StandardCoverageActivity.class);
                            intent.putExtra("user_content", userContent);
                            intent.putExtra("user_grade", userGrade);
                            intent.putExtra("user_standard_set_id", standardSetId);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else if (view.getId() == R.id.view_create_tests) {
            viewCoursesCall();
        }
    }

    private void logOutUser() {
        if (currentUser != null && firebaseAuth != null) {
            firebaseAuth.signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        }
    }

    public void backButtonMethod() {
        Intent intent = getIntent();
        intent.putExtra("dashboard_callback", "Successfully went back");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void viewCoursesCall() {
          // Make API Call to get Standard Set ID
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        String standardsSetURL = "https://api.commonstandardsproject.com/api/v1/jurisdictions/" + jurisdictionID + "?api-key=" + standardsApiKey; // get the standards set id
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

                                    // Send to View Standards Activity
                                    Intent intent = new Intent(getApplicationContext(), ViewEditTestsActivity.class);
                                    intent.putExtra("standard_set_id", standardSetID);
                                    intent.putExtra("user_grade", userGrade);
                                    intent.putExtra("user_content", userContent);
                                    startActivity(intent);
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

        Log.d("check queue", "viewCoursesCall: " + queue);
        queue.add(standardsIDObject);
    }

}


