package com.example.testcore.data;

import androidx.annotation.NonNull;

import com.example.testcore.models.Question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class questionsByStandardFirestore {
    ArrayList<Question> firestoreArrayList = new ArrayList<>();
    private String standardLabel;

    public questionsByStandardFirestore(String standardLabel) {
        this.standardLabel = standardLabel;
    }

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public List<Question> getFirestorequestions(final QuestionFirestoreAsyncResponse callBack) {
        CollectionReference questionPath = database.collection("Questions");

        questionPath.whereEqualTo("Standard Label", standardLabel).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                      for (int i = 0; i < queryDocumentSnapshots.size(); i ++) {
                          String questionText = queryDocumentSnapshots.getDocuments().get(i).getString("Question Text");
                          String choiceA = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice A");
                          String choiceB = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice B");
                          String choiceC = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice C");
                          String choiceD = queryDocumentSnapshots.getDocuments().get(i).getString("Answer Choice D");


                          Question question = new Question();
                          question.setStandardLabel(standardLabel);
                          question.setQuestionText(questionText);
                          question.setAnswerChoiceA(choiceA);
                          question.setAnswerChoiceB(choiceB);
                          question.setAnswerChoiceC(choiceB);
                          question.setAnswerChoiceD(choiceB);

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

        return firestoreArrayList;
    }
}
