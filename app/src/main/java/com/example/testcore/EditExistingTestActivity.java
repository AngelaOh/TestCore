package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.adapter.RecyclerViewAdapterQuestion;
import com.example.testcore.adapter.RecyclerViewAdapterTest;
//import com.example.testcore.adapter.RecyclerViewAdapterTestQuestions;
import com.example.testcore.adapter.RecyclerViewAdapterTestQuestions;
import com.example.testcore.data.QuestionFirestoreAsyncResponse;
import com.example.testcore.data.questionFirestoreExistingTest;
import com.example.testcore.models.Question;
import com.example.testcore.models.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditExistingTestActivity extends AppCompatActivity implements View.OnClickListener {
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private TextView questionCount;
    private TextView testTitle;
    private Button addQuestion;

    private RecyclerView recyclerView;
    private RecyclerViewAdapterTestQuestions recyclerViewAdapter;

    private String incomingTestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_existing_test);

        Bundle bundle = getIntent().getExtras();
        String incomingTitle = bundle.getString("title");
        incomingTestId = bundle.getString("test_id");

        questionCount = findViewById(R.id.question_count_text);
        testTitle = findViewById(R.id.existing_test_title);
        testTitle.setText(incomingTitle);
        addQuestion = findViewById(R.id.one_test_add_question_button);
        addQuestion.setOnClickListener(this);

        recyclerView = findViewById(R.id.existing_test_questions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new questionFirestoreExistingTest(incomingTestId).getFirestorequestions(new QuestionFirestoreAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> firestoreArrayList) {
                Log.d("Existing test q's", "processFinished: " + firestoreArrayList);
                implementRecyclerView(firestoreArrayList, incomingTestId);
            }
        });

    }

    public void implementRecyclerView(ArrayList<Question> questions_array, String testId) {
        recyclerViewAdapter = new RecyclerViewAdapterTestQuestions(EditExistingTestActivity.this, questions_array, testId);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.one_test_add_question_button) {

            // user grade, user content, test id
            database.collection("Tests").document(incomingTestId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String courseId = documentSnapshot.getString("Course Id");
                            Log.d("check course id", "onSuccess: " + courseId);

                            database.collection("Standard Sets").document(courseId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String userContent = documentSnapshot.getString("content");
                                            String userGrade = documentSnapshot.getString("grade");

                                            Intent intent = new Intent(EditExistingTestActivity.this, CreateTestActivity.class);
                                            intent.putExtra("user_grade", userGrade);
                                            intent.putExtra("user_content", userContent);
                                            intent.putExtra("test_id", incomingTestId);
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
}
