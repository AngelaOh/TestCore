package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testcore.data.FirestoreAsyncResponse;
import com.example.testcore.data.standardFirestoreBank;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EditQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView showStandard;
    private EditText editQuestionText, editAnswerA, editAnswerB, editAnswerC, editAnswerD;
    private Button submitEdits;
    private String standardLabel;
    private String questionText;
    private String userContent;
    private String userGrade;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        showStandard = findViewById(R.id.edit_standard_info_text);
        editQuestionText = findViewById(R.id.edit_question_text);
        editAnswerA = findViewById(R.id.edit_answer_choice_a);
        editAnswerB = findViewById(R.id.edit_answer_choice_b);
        editAnswerC = findViewById(R.id.edit_answer_choice_c);
        editAnswerD = findViewById(R.id.edit_answer_choice_d);
        submitEdits = findViewById(R.id.submit_edit_button);
        submitEdits.setOnClickListener(this);


        Intent fromRecyclerTestQuestions = getIntent();
        Bundle bundle = fromRecyclerTestQuestions.getExtras();
        standardLabel = bundle.getString("standard_label");
        questionText = bundle.getString("question_text");


        // getting standard to show at top
        database.collection("Users").whereEqualTo("userId", currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");
                        userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");

                        new standardFirestoreBank(userContent, userGrade, currentUser.getUid()).getFirestoreStandards(new FirestoreAsyncResponse() {
                            @Override
                            public void processFinished(ArrayList<Standard> firestoreArrayList) {
                                String standardDescription;
                                for (Standard standard : firestoreArrayList) {
                                    if (standard.getLabel().matches(standardLabel)) {
                                        standardDescription = standard.getLabel() + ": " + standard.getDescription();
                                        showStandard.setText(standardDescription);
                                    }
                                }

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        // getting edit question hints to show original question text
        database.collection("Questions").whereEqualTo("Question Text", questionText).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String questionA = queryDocumentSnapshots.getDocuments().get(0).getString("Answer Choice A");
                        String questionB = queryDocumentSnapshots.getDocuments().get(0).getString("Answer Choice B");
                        String questionC = queryDocumentSnapshots.getDocuments().get(0).getString("Answer Choice C");
                        String questionD = queryDocumentSnapshots.getDocuments().get(0).getString("Answer Choice D");
                        String questionText = queryDocumentSnapshots.getDocuments().get(0).getString("Question Text");

                        editQuestionText.setHint(questionText);
                        editAnswerA.setHint(questionA);
                        editAnswerB.setHint(questionB);
                        editAnswerC.setHint(questionC);
                        editAnswerD.setHint(questionD);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_edit_button) {
            updateQuestion();
        }
    }

    private void updateQuestion() {
        database.collection("Questions").whereEqualTo("Question Text", questionText).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String questionId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        final String testId = queryDocumentSnapshots.getDocuments().get(0).getString("Test Id");

                        // update question in database
                        DocumentReference questionUpdateRef = database.collection("Questions").document(questionId);
                        if (!editQuestionText.getText().toString().trim().isEmpty()) {
                            Log.d("Edit QuestionText", "onSuccess: " + (editQuestionText.getText().toString().trim().isEmpty()));
                            questionUpdateRef.update("Question Text", editQuestionText.getText().toString().trim());
                        }
                        if (!editAnswerA.getText().toString().trim().isEmpty()) {
                            questionUpdateRef.update("Answer Choice A", editAnswerA.getText().toString().trim());
                        }
                        if (!editAnswerB.getText().toString().trim().isEmpty()) {
                            questionUpdateRef.update("Answer Choice B", editAnswerB.getText().toString().trim());
                        }
                        if (!editAnswerC.getText().toString().trim().isEmpty()) {
                            questionUpdateRef.update("Answer Choice C", editAnswerC.getText().toString().trim());
                        }
                        if (!editAnswerD.getText().toString().trim().isEmpty()) {
                            questionUpdateRef.update("Answer Choice D", editAnswerD.getText().toString().trim());
                        }

                        database.collection("Tests").document(testId).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String testTitle = documentSnapshot.getString("Test Title");

                                        Intent intent = new Intent(EditQuestionActivity.this, EditExistingTestActivity.class);
                                        intent.putExtra("title", testTitle);
                                        intent.putExtra("test_id", testId);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
