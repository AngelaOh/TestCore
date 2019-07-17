package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import util.StandardApi;

public class ViewStandardsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getStandardsButton;
    private TextView standardsView;

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

        standardsView = findViewById(R.id.standards_view);
        getStandardsButton = findViewById(R.id.get_standards_button);
        getStandardsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.get_standards_button) {
            getStandards();
            displayStandards();
        }

    }

    private void getStandards() {
        queue = MySingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
        String standardSetID = StandardApi.getInstance().getStandardSetId();
        final String userContent = StandardApi.getInstance().getUserContent();
        final String userGrade = StandardApi.getInstance().getUserGrade();

        Log.d("ON CLICK STANDARDS", "getStandards: " + standardSetID);

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

//                                    Standard newStandard = new Standard(label,description);

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
        // get standards from firestore
        final String userContent = StandardApi.getInstance().getUserContent();
        final String userGrade = StandardApi.getInstance().getUserGrade();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();

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
                            }
                             standardsView.setText(data.toString());
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
