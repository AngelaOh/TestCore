package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.adapter.RecyclerViewAdapterQuestion;
import com.example.testcore.data.QuestionFirestoreAsyncResponse;
import com.example.testcore.data.questionFirestoreBank;
import com.example.testcore.models.Question;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapterQuestion recyclerViewAdapter;

    private TextView showStandard;
    private ImageButton addQuestion;
    private EditText questionText, answerChoiceA, answerChoiceB, answerChoiceC, answerChoiceD;
    private String standardLabel;
    private String testId;

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);


        showStandard = findViewById(R.id.standard_info_text);
        Intent getStandardInfo = getIntent();
        Bundle bundle = getStandardInfo.getExtras();
        standardLabel = bundle.getString("label");
        Log.d("check standard label", "onCreate: " + standardLabel);

        String description = bundle.getString("description");
        String standardText = standardLabel + ": " + description;
        showStandard.setText(standardText);
        testId = bundle.getString("test_id");

        addQuestion = findViewById(R.id.add_question_button);
        addQuestion.setOnClickListener(this);

        questionText = findViewById(R.id.question_text);
        answerChoiceA = findViewById(R.id.answer_choice_a);
        answerChoiceB = findViewById(R.id.answer_choice_b);
        answerChoiceC = findViewById(R.id.answer_choice_c);
        answerChoiceD = findViewById(R.id.answer_choice_d);

        recyclerView = findViewById(R.id.question_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new questionFirestoreBank(standardLabel).getFirestorequestions(new QuestionFirestoreAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> firestoreArrayList) {
                implementRecyclerView(firestoreArrayList);
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_question_button) {
            addQuestionDatabase();
        }
    }

    public void implementRecyclerView(ArrayList<Question> questions_array) {
        recyclerViewAdapter = new RecyclerViewAdapterQuestion(CreateQuestionActivity.this, questions_array, testId);
        Log.d("QUESTIONS ARRAY", "implementRecyclerView: " + questions_array);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void addQuestionDatabase() {
        CollectionReference questionPath = database.collection("Questions");

        Map<String, String> questionforFirestore = new HashMap<>();
        questionforFirestore.put("Question Text",questionText.getText().toString().trim());
        questionforFirestore.put("Answer Choice A", answerChoiceA.getText().toString().trim());
        questionforFirestore.put("Answer Choice B", answerChoiceB.getText().toString().trim());
        questionforFirestore.put("Answer Choice C", answerChoiceC.getText().toString().trim());
        questionforFirestore.put("Answer Choice D", answerChoiceD.getText().toString().trim());
        questionforFirestore.put("Test Id", testId);
        questionforFirestore.put("Standard Label", standardLabel);

        questionPath.add(questionforFirestore)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateQuestionActivity.this, "Question has been added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateQuestionActivity.this, "There was an issue adding your question", Toast.LENGTH_SHORT).show();
                        Log.d("Issue with Question Add", "onFailure: " + e.getMessage());
                    }
                });


        // get content, grade, and testId from firebase and send to next activity

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        String currentUserId = currentUser.getUid();

        database.collection("Users").whereEqualTo("userId", currentUserId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");
                        String userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");

                        Intent intent = new Intent(CreateQuestionActivity.this, CreateTestActivity.class);
                        intent.putExtra("test_id", testId);
                        intent.putExtra("user_grade", userGrade);
                        intent.putExtra("user_content", userContent);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
