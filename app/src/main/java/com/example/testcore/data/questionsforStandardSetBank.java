package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testcore.models.Question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class questionsforStandardSetBank {
    String outgoingQuestionText = new String();
    private String standardLabel;
    private String userContent;
    private String userGrade;

    public questionsforStandardSetBank(String standardLabel, String userContent, String userGrade) {
        this.standardLabel = standardLabel;
        this.userContent = userContent;
        this.userGrade = userGrade;
    }

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    public String getQuestionText(final QuestionforStandardAsyncResponse callBack) {

        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + currentUserId)
                .collection(standardLabel).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 1) {
                            outgoingQuestionText = queryDocumentSnapshots.getDocuments().get(1).getId();
                        }
                        if (null != callBack) callBack.processFinished(outgoingQuestionText);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return outgoingQuestionText;
    }
}
