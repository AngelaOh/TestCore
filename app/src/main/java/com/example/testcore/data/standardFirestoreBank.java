package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class standardFirestoreBank {
    ArrayList<Standard> firestoreArrayList = new ArrayList<>();
    private String userContent;
    private String userGrade;
    private String currentUserId;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public standardFirestoreBank( String userContent, String userGrade, String currentUserId) {
        this.userContent = userContent;
        this.userGrade = userGrade;
        this.currentUserId = currentUserId;
    }

    public List<Standard> getFirestoreStandards(final FirestoreAsyncResponse callBack) {
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

                                Standard newStandard = new Standard(key_array[i].toString(), standard.toString());
                                newStandard.setLabel(key_array[i].toString());
                                newStandard.setDescription(standard.toString());
                                Log.d("check object", "onSuccess: " + newStandard.getLabel());

                                firestoreArrayList.add(newStandard);

                            }
                            Log.d("get standards", "onSuccess: standards_array " + firestoreArrayList);

                        } else {
                            Log.d("Standards Retrieval", "onSuccess: Document snapshot is empty!");
                        }
                        if (null != callBack) callBack.processFinished(firestoreArrayList);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Standards Retrieval", "onSuccess: Failed to retrieve standards!");
                    }
                });

        return firestoreArrayList;
    }

}
