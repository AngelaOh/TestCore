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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOutButton, backButton, viewAllButton, createTestButton, createQuestionButton;
    private TextView welcomeMessage, apiText;
    private String standardsApiKey = BuildConfig.StandardsApiKey;

    // Volley
    private String waURL = "https://commonstandardsproject.com/api/v1/standard_sets/B1339AB05F0347E79200FCA63240F3B2_D2513639_grade-06?api-key=" + standardsApiKey;
    RequestQueue queue;
    String practiceAPIStandard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logOutButton = findViewById(R.id.log_out_button);
        backButton = findViewById(R.id.back_button);
        viewAllButton = findViewById(R.id.view_all_tests);
        createTestButton = findViewById(R.id.create_new_test);
        createQuestionButton = findViewById(R.id.create_new_question);
        welcomeMessage = findViewById(R.id.welcome_message);
        apiText = findViewById(R.id.test_api_text);

        logOutButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);
        createTestButton.setOnClickListener(this);
        createQuestionButton.setOnClickListener(this);

        String userName = getIntent().getStringExtra("login_name");
        String userEmail = getIntent().getStringExtra("login_email");
        String userState = getIntent().getStringExtra("login_state");
        String userGrade = getIntent().getStringExtra("login_grade");
        String userContent = getIntent().getStringExtra("login_content");

//        Log.d("INFO FROM MAIN", "onCreate: " + userName + userEmail + userState + userGrade + userContent);
        // Log.d("checking back button", "onClick: IN BACK BUTTON CLICK LISTENER");

        welcomeMessage.setText("Welcome, " + userName);

        // Volley API Call - GET Request
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        JsonObjectRequest testStandardsObject = new JsonObjectRequest(Request.Method.GET,
                waURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //                            practiceAPIStandard = response.getJSONObject("data")
//                                    .getJSONObject("standards")
//                                    .getJSONObject("2A816130DFE80131C06868A86D17958E")
//                                    .getString("description");
//
//                            apiText.setText(practiceAPIStandard);
//                            Log.d("JSON STANDARDS: ", "onResponse: " + practiceAPIStandard);

                        Log.d("JSON STANDARDS", "onResponse: " + response);

//                            String testObjectString = "";
//                            testObjectString = response.getJSONObject("title").toString();
//                            apiText.setText(testObjectString);
//
//                            Log.d("API CALL TEST", "onResponse: " + testObjectString);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("API Error", "onErrorResponse: HERE IS API ERROR" + error.getMessage());
            }
        });
        queue.add(testStandardsObject);
    }

    @Override
    public void onClick(View view) {
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
        }
    }

    public void backButtonMethod() {
        Intent intent = getIntent();
        intent.putExtra("dashboard_callback", "Successfully went back");
        setResult(RESULT_OK, intent);
        finish();
    }
}
