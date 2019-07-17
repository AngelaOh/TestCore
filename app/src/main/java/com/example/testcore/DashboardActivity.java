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
import com.example.testcore.models.Standard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import util.StandardApi;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOutButton, backButton, viewCoursesButton, viewAllButton, createTestButton, createQuestionButton;
    private TextView welcomeMessage;
    private String standardsApiKey = BuildConfig.StandardsApiKey;
//    private String jurisdictionID;
    private String standardSetID;


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
                } else {
                    // user is not logged in
                }
            }
        };

        logOutButton = findViewById(R.id.log_out_button);
        backButton = findViewById(R.id.back_button);
        viewCoursesButton = findViewById(R.id.view_courses);
        viewAllButton = findViewById(R.id.view_all_tests);
        createTestButton = findViewById(R.id.create_new_test);
        createQuestionButton = findViewById(R.id.create_new_question);
        welcomeMessage = findViewById(R.id.welcome_message);

        logOutButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        viewCoursesButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);
        createTestButton.setOnClickListener(this);
        createQuestionButton.setOnClickListener(this);

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
        } else if (view.getId() == R.id.create_new_test) {
            Toast.makeText(DashboardActivity.this, "create new test button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.create_new_question) {
            Toast.makeText(DashboardActivity.this, "create new question button clicked", Toast.LENGTH_LONG).show();
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

        String receivedJurisdictionId = StandardApi.getInstance().getJurisdictionId();
        final String userGrade = StandardApi.getInstance().getUserGrade();
        final String userContent = StandardApi.getInstance().getUserContent();
        final String userName = StandardApi.getInstance().getUsername();

        String standardsSetURL = "https://api.commonstandardsproject.com/api/v1/jurisdictions/" + receivedJurisdictionId + "?api-key=" + standardsApiKey; // get the standards set id
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
                                    StandardApi.getInstance().setStandardSetId(standardSetID);
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

            }
        });
        queue.add(standardsIDObject);

        Intent intent = new Intent(DashboardActivity.this, ViewStandardsActivity.class);
        startActivity(intent);

    }

        // method to check if course is missing any standards not covered in tests
//    public ArrayList<Standard> checkStandards(Course course) {
//        for (test : course.getTests()) {
//            // check for standards
//        }
//    }
}


