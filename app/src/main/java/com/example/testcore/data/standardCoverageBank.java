package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class standardCoverageBank {
    HashMap<String, String> allStandardsHash = new HashMap<>();
    private String userContent;
    private String userGrade;
    private String currentUserId;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    public standardCoverageBank(String userContent, String userGrade, String currentUserId) {
        this.userContent = userContent;
        this.userGrade = userGrade;
        this.currentUserId = currentUserId;
    }

    public HashMap<String, String> getAllStandards(final StandardCoverageAsyncResponse callBack) {

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUser.getUid()).collection("Standards").document("All Standards").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        Set<String> standard_label_keys = data.keySet();
                        Object[] standard_labels_array = standard_label_keys.toArray();

                        for (int i = 0; i < standard_labels_array.length; i += 1) {
                            Object standard_description = data.get(standard_labels_array[i]);
//                            Log.d("check keys", "onSuccess: " + standard_labels_array[i].getClass());

//                            Log.d("check description", "onSuccess: " + standard_description);
                            allStandardsHash.put(standard_labels_array[i].toString(), standard_description.toString());
                        }

//                        Log.d("all standards!", "onCreate: " + allStandardsHash);
                        if (null != callBack) callBack.processFinished(allStandardsHash);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return allStandardsHash;
    }
}
