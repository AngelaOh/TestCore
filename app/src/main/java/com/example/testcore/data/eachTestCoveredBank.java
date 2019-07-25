package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class eachTestCoveredBank {
    ArrayList<String> eachTestList = new ArrayList<>();
    private String userContent;
    private String userGrade;
    private String currentUserId;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    public eachTestCoveredBank(String userContent, String userGrade, String currentUserId) {
        this.userContent = userContent;
        this.userGrade = userGrade;
        this.currentUserId = currentUserId;
    }

    public List<String> getIndividualTests(final EachTestAsyncResponse callBack) {

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        /////// get all test id's associated with user
        database.collection("Tests").whereEqualTo("Course Id",userContent + ": " + userGrade + ": " + currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i ++) {
                            String test_id = queryDocumentSnapshots.getDocuments().get(i).getId();
                            eachTestList.add(test_id);
                        }
                        if (null != callBack) callBack.processFinished(eachTestList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        Log.d("Inside Data Call", "getIndividualTests: " + eachTestList);
        return eachTestList;
    }
}
