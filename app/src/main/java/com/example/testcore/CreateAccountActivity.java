// TODO: add progress bar

package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.StandardApi;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private Button createAccountButton;
    private EditText createAccountName, createAccountEmail, createAccountPassword, createAccountState, createAccountGrade, createAccountContent;
    private final int REQUEST_CODE = 123;

    // Volley
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    private String jurisdictionID;
    RequestQueue queue;

    // Firebase Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = database.collection("Users");

    // Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountName = findViewById(R.id.create_account_name);
        createAccountEmail = findViewById(R.id.create_account_email);
        createAccountPassword = findViewById(R.id.create_account_password);
        createAccountState = findViewById(R.id.create_account_state);
        createAccountGrade = findViewById(R.id.create_account_grade);
        createAccountContent = findViewById(R.id.create_account_content);

        createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(CreateAccountActivity.this, "Hi, " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    // user is not logged in
                }
            }
        };
    }

    @Override
    public void onClick(View view) {

        String name = createAccountName.getText().toString().trim();
        String email = createAccountEmail.getText().toString().trim();
        String password = createAccountPassword.getText().toString().trim();
        String state = createAccountState.getText().toString().trim();
        String grade = createAccountGrade.getText().toString().trim();
        String content = createAccountContent.getText().toString().trim();

        if (view.getId() == R.id.create_account_button) {

            getJurisdictionId();
//            String sendJurisdictionId;
//            sendJurisdictionId = getJurisdictionId();
            Log.d("BEFORE SEND JID", "onComplete: " + jurisdictionID);

            if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {

                createUserEmailAccount(email, password, name);
//                Log.d("SEND JURISDICTION ID", "onComplete: " + something);


            } else {
                Toast.makeText(CreateAccountActivity.this, "Empty fields not allowed!", Toast.LENGTH_LONG).show();
            }
        }

//        addData(name, email, state, grade, content);

    }

    private void createUserEmailAccount(String email, String password, final String name) {
        if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {

            // progress bar to visible here

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {

                              // get current user info and put in firestore database
                              currentUser = firebaseAuth.getCurrentUser();
                              assert currentUser != null;
                              final String currentUserId = currentUser.getUid();

                              // create user map so we can create a user in the User collection of Firestore
                              Map<String, String> userObj = new HashMap<>();
                              userObj.put("userId", currentUserId);
                              userObj.put("username", name);
                              userObj.put("content", createAccountContent.getText().toString().trim());
                              userObj.put("grade", createAccountGrade.getText().toString().trim());
                              userObj.put("jurisdictionId", jurisdictionID);
//                              Log.d("MOVE TO DASH", "onComplete: " + currentUserId);

                              // add Course data to Firestore
                              addData(name, currentUserId);

                              // api call to get jurisdiction ID, pass to Dashboard Activity through intent
//                              getJurisdictionId();
//                              String something;
//                              something = getJurisdictionId();
//                              Log.d("SEND JURISDICTION ID", "onComplete: " + something);

                              // save to Firestore
                              collectionReference.add(userObj)
                                      .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                          @Override
                                          public void onSuccess(DocumentReference documentReference) {
                                              documentReference.get()
                                                      .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                              Log.d("MOVE TO DASH", "onComplete: " + currentUserId);

                                                              if (Objects.requireNonNull(task.getResult().exists())) {
                                                                  // set progress bar to invisible
                                                                  String name = task.getResult()
                                                                          .getString("name");
                                                                  String userName = createAccountName.getText().toString().trim();
                                                                  String userContent = createAccountContent.getText().toString().trim();
                                                                  String userGrade = createAccountGrade.getText().toString().trim();

                                                                  StandardApi standardApi = StandardApi.getInstance(); //global API
                                                                  standardApi.setUserId(currentUserId);
                                                                  standardApi.setUsername(userName);
                                                                  standardApi.setUserContent(userContent);
                                                                  standardApi.setUserGrade(userGrade);
                                                                  standardApi.setJurisdictionId(jurisdictionID);

                                                                  Log.d("STANDARDAPI JID", "onComplete: " + standardApi.getJurisdictionId());

                                                                  Intent intent = new Intent(CreateAccountActivity.this,
                                                                          DashboardActivity.class);
                                                                  intent.putExtra("username", userName);
                                                                  intent.putExtra("userId", currentUserId);
                                                                  intent.putExtra("jurisdiction_id", jurisdictionID);
                                                                  startActivity(intent);
                                                                  Log.d("MOVE TO DASH", "onComplete: " + currentUserId);
                                                                  Log.d("SENT JURISDICTIONID", "onComplete: " + jurisdictionID);


                                                              } else {
                                                                  // set progress bar to invisible
                                                                  // maybe some logcat error message
                                                              }


                                                          }
                                                      });
                                          }
                                      })
                                      .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Log.d("FAILED TO MAKE ACCOUNT", "onFailure: " + e.getMessage());
                                          }
                                      });
                          }
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                   }
             });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void addData(String name, String userId) {

        String currentUserId = currentUser.getUid();
        String state = createAccountState.getText().toString().trim();
        String grade = createAccountGrade.getText().toString().trim();
        String content = createAccountContent.getText().toString().trim();

        DocumentReference addCourse = database.collection("Standard Sets").document(content + ": " + grade + ": " + currentUserId);
        Map<String, Object> course_data = new HashMap<>();
        course_data.put("username", name);
        course_data.put("userId", userId);
        course_data.put("state", state);
        course_data.put("grade", grade);
        course_data.put("content", content);
        addCourse.set(course_data);

    }

    public String getJurisdictionId() {
        final String userState = createAccountState.getText().toString().trim();
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
                             Log.d("CHECK ARRAY", "onResponse: " + jurisdictionArray);

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

        return jurisdictionID;
    }


}
