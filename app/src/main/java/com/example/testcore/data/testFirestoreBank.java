package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testcore.models.Standard;
import com.example.testcore.models.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class testFirestoreBank {

    ArrayList<Test> firestoreArrayList = new ArrayList<>();
    private String userContent;
    private String userGrade;
    private String currentUserId;

    public testFirestoreBank(String userContent, String userGrade, String currentUserId) {
        this.userContent = userContent;
        this.userGrade = userGrade;
        this.currentUserId = currentUserId;
    }

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();


    // userContent, userGrade, userId
    public List<Test> getFirestoretests(final TestFirestoreAsyncResponse callBack) {
        CollectionReference pathIdTwo = database.collection("Tests");
        pathIdTwo.whereEqualTo("Course Id", userContent + ": " + userGrade + ": " + currentUserId).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i ++) {
                            Log.d("Tests from Firestore", "onSuccess: " + queryDocumentSnapshots.getDocuments().get(0));
                            String testTitle = queryDocumentSnapshots.getDocuments().get(i).getString("Test Title");
                            Object numOfQuestions = queryDocumentSnapshots.getDocuments().get(i).get("Questions"); // Eventually want to be able to also display number of questions
                            String testId = queryDocumentSnapshots.getDocuments().get(i).getId();

                            Test firestoreTest = new Test();
                            firestoreTest.setTitle(testTitle);
                            firestoreTest.setTestId(testId);
                            Log.d("View One Test", "onSuccess: " + firestoreTest.getTitle());

                            firestoreArrayList.add(firestoreTest);
                        }
                        Log.d("Array of Tests", "getFirestoretests: " + firestoreArrayList);
                        if (null != callBack) callBack.processFinished(firestoreArrayList);

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });


        return firestoreArrayList;
    }
}
