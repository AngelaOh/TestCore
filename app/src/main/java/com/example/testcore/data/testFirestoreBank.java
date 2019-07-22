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

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public List<Test> getFirestoretests(final TestFirestoreAsyncResponse callBack) {
        CollectionReference pathIdTwo = database.collection("Tests");
        pathIdTwo.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i ++) {
                            Log.d("Tests from Firestore", "onSuccess: " + queryDocumentSnapshots.getDocuments().get(0));
                            String testTitle = queryDocumentSnapshots.getDocuments().get(i).getString("Test Title");
                            Object numOfQuestions = queryDocumentSnapshots.getDocuments().get(i).get("Questions"); // Eventually want to be able to also display number of questions

                            Test firestoreTest = new Test();
                            firestoreTest.setTitle(testTitle);
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
