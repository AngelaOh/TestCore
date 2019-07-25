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

public class eachStandardCoveredBank {
    ArrayList<String> eachStandardList = new ArrayList<>();
    private String userContent;
    private String userGrade;
    private String currentUserId;
    private ArrayList<String> testIdArray;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    public eachStandardCoveredBank(String userContent, String userGrade, String currentUserId, ArrayList<String> testIdArray) {
        this.userContent = userContent;
        this.userGrade = userGrade;
        this.currentUserId = currentUserId;
        this.testIdArray = testIdArray;
    }

    public List<String> getIndividualStandards(final EachStandardAsyncResponse callBack) {

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        /////// get all test id's associated with user
        for (int i = 0; i < testIdArray.size(); i ++) {
            database.collection("Questions").whereEqualTo("Test Id", testIdArray.get(i)).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for (int j = 0; j < queryDocumentSnapshots.getDocuments().size(); j++) {
                                eachStandardList.add(queryDocumentSnapshots.getDocuments().get(j).getString("Standard Label"));
                                Log.d("check standard label", "onSuccess: " + eachStandardList);
                            }
                            if (null != callBack) callBack.processFinished(eachStandardList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

        Log.d("Inside Data Call", "getIndividualTests: " + eachStandardList);
        return eachStandardList;
    }
}