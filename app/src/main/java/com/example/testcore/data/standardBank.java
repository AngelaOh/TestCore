package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.testcore.BuildConfig;
import com.example.testcore.controller.AppController;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.StandardApi;

public class standardBank {
    ArrayList<Standard> standardArrayList = new ArrayList<>();
    private String standardSetID;
    private String standardsApiKey = BuildConfig.StandardsApiKey;

    public standardBank(String standardSetID) {
        this.standardSetID = standardSetID;
    }

    public List<Standard> getStandards(final AnswerListAsyncResponse callBack) {

        Log.d("Check STANDARDS set It", "getStandards: " + standardSetID); // THIS IS NULL RIGHT NOW

        String url = "https://api.commonstandardsproject.com/api/v1/standard_sets/"+ standardSetID + "?api-key=" + standardsApiKey; // get the standards;
        JsonObjectRequest finalStandardsObject = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("STANDARDS BANK", "getStandards: " + "inside of standardsBank" + response);

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
                                    Log.d("New Standard Obj", "onResponse: " + newStandard.getLabel());
                                    standardArrayList.add(newStandard);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (null != callBack) callBack.processFinished(standardArrayList);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(finalStandardsObject);
        Log.d("Before Activity List", "getStandards: " + standardArrayList);
        return standardArrayList;
    }
}

// return list of standards from volley api call
// save in database inside of Activity
// post
