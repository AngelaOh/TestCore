//package com.example.testcore.data;
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.example.testcore.models.Question;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class questionsByStandardFirestore {
//    Map<String, String> standardQuestionHash = new HashMap<>();
//    private ArrayList<String> testIdArray;
//
//    // Connection to Firestore
//    private FirebaseFirestore database = FirebaseFirestore.getInstance();
//
//    // Firebase Auth
//    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener authStateListener;
//    private FirebaseUser currentUser;
//
//    public questionsByStandardFirestore(ArrayList<String> testIdArray) {
//        this.testIdArray = testIdArray;
//    }
//
//    public HashMap<String,String> getQuestionsByStandard(final QuestionByStandardAsyncResponse callBack) {
//
//        for (int i = 0; i < testIdArray.size(); i ++) {
//            database.collection("Questions").whereEqualTo("Test Id", testIdArray.get(i)).get()
//                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                            for (int j = 0; j < queryDocumentSnapshots.getDocuments().size(); j++) {
//                                standardQuestionHash.put(queryDocumentSnapshots.getDocuments().get(j).getString("Standard Label"), queryDocumentSnapshots.getDocuments().get(j).getString("Standard Label"));
////                                Log.d("check standard label", "onSuccess: " + eachStandardList);
//                            }
////                            if (null != callBack) callBack.processFinished(standardQuestionHash);
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//        }
//
//        return standardQuestionHash;
//    }
//}
