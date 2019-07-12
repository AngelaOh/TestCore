// TODO: Parse through user login info and common core data to save the right info into firestore
// TODO: OAuth using Firebase


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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOutButton, backButton, getStandardsButton, viewAllButton, createTestButton, createQuestionButton;
    private TextView welcomeMessage;
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    private String jurisdictionID;


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
        String userState = getIntent().getStringExtra("login_state");

        if (view.getId() == R.id.log_out_button) {
            Toast.makeText(DashboardActivity.this, "log out button clicked", Toast.LENGTH_LONG).show();
        }

        else if (view.getId() == R.id.back_button) {
//             Toast.makeText(DashboardActivity.this, "back button clicked", Toast.LENGTH_LONG).show();
            Log.d("checking back button", "onClick: IN BACK BUTTON CLICK LISTENER");
            backButtonMethod();
        } else if (view.getId() == R.id.view_all_tests) {
            Toast.makeText(DashboardActivity.this, "view all button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.create_new_test) {
            Toast.makeText(DashboardActivity.this, "create new test button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.create_new_question) {
            Toast.makeText(DashboardActivity.this, "create new question button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.call_standards) {
            makeStandardsCall(userState);
        }
    }

    public void backButtonMethod() {
        Intent intent = getIntent();
        intent.putExtra("dashboard_callback", "Successfully went back");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void makeStandardsCall(final String userState) {
//        String userName = getIntent().getStringExtra("login_name");
//        String userEmail = getIntent().getStringExtra("login_email");
//        String userState = getIntent().getStringExtra("login_state");
//        String userGrade = getIntent().getStringExtra("login_grade");
//        String userContent = getIntent().getStringExtra("login_content");

        // TODO: 1. make call to get jurisdiction id
        // TODO: 2. make call to get standard sets id [use: jurisdiction id, content, grade]
        // TODO: 3. make call to get standards [use: standard set id]
        // Volley API Call - GET Request
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        final String jurisdictionURL = "https://commonstandardsproject.com/api/v1/jurisdictions/?api-key=" + standardsApiKey; // get the jurisdiction id

        String standardsSetURL = "https://api.commonstandardsproject.com/api/v1/jurisdictions/7432D25024594EA9A2092DF45BBA7F6C?api-key=" + standardsApiKey; // get the standards set id
        String waStandardsURL = "https://api.commonstandardsproject.com/api/v1/standard_sets/7432D25024594EA9A2092DF45BBA7F6C_D1000385_grade-06?api-key=" + standardsApiKey; // get the standards

        // get jurisdiction id
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
                                    // Log.d("CHECK ANOTHER ARRAY", "onResponse: " + jurisdictionArray.getJSONObject(i).getString("title"));
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


    }
}

//                            practiceAPIStandard = response.getJSONObject("data")
//                                    .getJSONObject("standards")
//                                    .getJSONObject("2A816130DFE80131C06868A86D17958E")
//                                    .getString("description");
//
//                            apiText.setText(practiceAPIStandard);
//                            Log.d("JSON STANDARDS: ", "onResponse: " + practiceAPIStandard);
