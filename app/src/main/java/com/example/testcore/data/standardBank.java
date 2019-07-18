package com.example.testcore.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.testcore.BuildConfig;
import com.example.testcore.ViewStandardsActivity;
import com.example.testcore.controller.MySingleton;
import com.example.testcore.models.Standard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.StandardApi;

public class standardBank {
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    RequestQueue queue;

    ArrayList<Standard> standardArrayList = new ArrayList<>();
    private String standardSetId = StandardApi.getInstance().getStandardSetId();
    private String standardsApiKey = BuildConfig.StandardsApiKey;
    private String url = "https://api.commonstandardsproject.com/api/v1/standard_sets/"+ standardSetId + "?api-key=" + standardsApiKey; // get the standards;

    public List<Standard> getStandards() {
//        queue = MySingleton.getInstance(ViewStandardsActivity.getApplicationContext()).getRequestQueue();

        JsonObjectRequest finalStandardsObject = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataWrapper = response.getJSONObject("data");
                            JSONObject standardsWrapper = dataWrapper.getJSONObject("standards");
                            JSONArray standardsKeys = standardsWrapper.names();

                            Log.d("STANDARDS WRAPPER", "onResponse: " + standardsWrapper);
                            Log.d("STANDARDS WRAPPER KEYS", "onResponse: " + standardsKeys);

//                            for (int i = 0; i < standardsWrapper.length(); i ++) {
//                                String key = standardsKeys.getString(i);
//                                if (standardsWrapper.getJSONObject(key).getInt("depth") == 3) {
//                                    Log.d("ALL DESCRIPTIONS", "onResponse: " + standardsWrapper.getJSONObject(key).getString("description"));
//
//                                    String description = standardsWrapper.getJSONObject(key).getString("description");
//                                    String label = standardsWrapper.getJSONObject(key).getString("listId");
//
//                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                                    assert currentUser != null;
//                                    String currentUserId = currentUser.getUid();
//
////                                    Standard newStandard = new Standard(label,description);
//
//                                    DocumentReference pathIdTwo = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection("Standards").document("All Standards");
//                                    DocumentReference pathId = database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId).collection(label).document(label + " Information");
//
//                                    Map<String, Object> one_standard = new HashMap<>();
//                                    one_standard.put(label, description);
//
//                                    pathId.set(one_standard, SetOptions.merge());
//                                    pathIdTwo.set(one_standard, SetOptions.merge());
//                                };
//                            }
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
//        MySingleton.getInstance(this);
//        MySingleton.addToRequestQueue(finalStandardsObject);

        return null;
    }
}
