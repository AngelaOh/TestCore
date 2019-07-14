// TODO: issue with order to volley calls going. need to control order for non-hardcoded url to work

package com.example.testcore;

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
import com.example.testcore.models.Course;
import com.example.testcore.models.Standard;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOutButton, backButton, getStandardsButton, viewAllButton, createTestButton, createQuestionButton;
    private TextView welcomeMessage;
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    private String jurisdictionID;
    private String standardSetID;


    // Volley
    RequestQueue queue;
    String practiceAPIStandard;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logOutButton = findViewById(R.id.log_out_button);
        backButton = findViewById(R.id.back_button);
        getStandardsButton = findViewById(R.id.call_standards);
        viewAllButton = findViewById(R.id.view_all_tests);
        createTestButton = findViewById(R.id.create_new_test);
        createQuestionButton = findViewById(R.id.create_new_question);
        welcomeMessage = findViewById(R.id.welcome_message);

        logOutButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        getStandardsButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);
        createTestButton.setOnClickListener(this);
        createQuestionButton.setOnClickListener(this);

        String userName = getIntent().getStringExtra("login_name");
        String userEmail = getIntent().getStringExtra("login_email");
        String userState = getIntent().getStringExtra("login_state");
        String userGrade = getIntent().getStringExtra("login_grade");
        String userContent = getIntent().getStringExtra("login_content");

        welcomeMessage.setText("Welcome, " + userName);
    }

    @Override
    public void onClick(View view) {
        String userName = getIntent().getStringExtra("login_name");
        String userState = getIntent().getStringExtra("login_state");
        String userGrade = getIntent().getStringExtra("login_grade");
        String userContent = getIntent().getStringExtra("login_content");

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
        } else if (view.getId() == R.id.call_standards) {
            makeStandardsCall(userState, userGrade, userContent, userName);
        }
    }

    public void backButtonMethod() {
        Intent intent = getIntent();
        intent.putExtra("dashboard_callback", "Successfully went back");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void makeStandardsCall(final String userState, final String userGrade, final String userContent, final String userName) {

        // TODO: 1. make call to get jurisdiction id
        // TODO: 2. make call to get standard sets id [use: jurisdiction id, content, grade]
        // TODO: 3. make call to get standards [use: standard set id]
        // Volley API Call - GET Request
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        final String jurisdictionURL = "https://commonstandardsproject.com/api/v1/jurisdictions/?api-key=" + standardsApiKey; // get the jurisdiction id
        JsonObjectRequest jurisdictionObject = new JsonObjectRequest(Request.Method.GET,
                jurisdictionURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray jurisdictionArray = response.getJSONArray("data");
                            // Log.d("CHECK ARRAY", "onResponse: " + jurisdictionArray);

                            for (int i = 0; i < jurisdictionArray.length(); i ++) {
                                if ( jurisdictionArray.getJSONObject(i).getString("title").equals(userState.trim()) ) {
                                    jurisdictionID = jurisdictionArray.getJSONObject(i).getString("id");
                                     Log.d("CHECK JURISDICTION ID", "onResponse: " + jurisdictionID);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("API Error", "onErrorResponse: HERE IS API ERROR" + error.getMessage());
            }
        });
        queue.add(jurisdictionObject);


        String standardsSetURL = "https://api.commonstandardsproject.com/api/v1/jurisdictions/7432D25024594EA9A2092DF45BBA7F6C?api-key=" + standardsApiKey; // get the standards set id
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


        String waStandardsURL = "https://api.commonstandardsproject.com/api/v1/standard_sets/7432D25024594EA9A2092DF45BBA7F6C_D1000385_grade-06?api-key=" + standardsApiKey; // get the standards
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

                                    Standard newStandard = new Standard(label,description);
                                    DocumentReference addStandards = database.collection(userName).document(userName + " Preps").collection("Standard Sets").document("Standard for " + userContent);
                                    Map<String, Object> one_standard = new HashMap<>();
                                    one_standard.put(label, newStandard);
                                    addStandards.set(one_standard, SetOptions.merge());
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

        // method to check if course is missing any standards not covered in tests
//    public ArrayList<Standard> checkStandards(Course course) {
//        for (test : course.getTests()) {
//            // check for standards
//        }
//    }
}


