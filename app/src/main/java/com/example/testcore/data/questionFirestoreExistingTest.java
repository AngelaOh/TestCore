package com.example.testcore.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testcore.models.Question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class questionFirestoreExistingTest {
    ArrayList<Question> firestoreArrayList = new ArrayList<>();
    private String testId;

    public questionFirestoreExistingTest(String testId) {
        this.testId = testId;
    }

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public List<Question> getFirestorequestions(final QuestionFirestoreAsyncResponse callBack) {
        CollectionReference questionPath = database.collection("Questions");
        Log.d("docuemnts?", "onSuccess: " + testId);

        questionPath.whereEqualTo("Test Id", testId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Log.d("docuemnts?", "onSuccess: " + queryDocumentSnapshots.size());

                        for (int i = 0; i < queryDocumentSnapshots.size(); i ++) {
                            String questionText = queryDocumentSnapshots.getDocuments().get(i).getString("Question Text");
                            String standardLabel = queryDocumentSnapshots.getDocuments().get(i).getString("Standard Label");
                            String answerA = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice A");
                            String answerB = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice B");
                            String answerC = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice C");
                            String answerD = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice D");


                            Question question = new Question();
                            question.setStandardLabel(standardLabel);
                            question.setQuestionText(questionText);
                            question.setAnswerChoiceA(answerA);
                            question.setAnswerChoiceB(answerB);
                            question.setAnswerChoiceC(answerC);
                            question.setAnswerChoiceD(answerD);

                            Log.d("get questions text", "onSuccess: " + question.getQuestionText());

                            firestoreArrayList.add(question);

                        }
                        if (null != callBack) callBack.processFinished(firestoreArrayList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        Log.d("Inside Firestore Call", "getFirestorequestions: " + firestoreArrayList);
        return firestoreArrayList;
    }
}
