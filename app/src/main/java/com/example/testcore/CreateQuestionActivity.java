package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

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
    private String userContent;
    private String userGrade;
    private String testTitle;
    private ArrayList<String> questionTextFirestore = new ArrayList<>();

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
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        showStandard = findViewById(R.id.standard_info_text);
        Intent getStandardInfo = getIntent();
        Bundle bundle = getStandardInfo.getExtras();
        standardLabel = bundle.getString("label");
        Log.d("check standard label", "onCreate: " + standardLabel);

        String description = bundle.getString("description");
        String standardText = standardLabel + ": " + description;
        showStandard.setText(standardText);
        testId = bundle.getString("test_id");

        database.collection("Tests").document(testId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        testTitle = documentSnapshot.getString("Test Title");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

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

        database.collection("Users").whereEqualTo("userId", currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        userContent = queryDocumentSnapshots.getDocuments().get(0).getString("content");
                        userGrade = queryDocumentSnapshots.getDocuments().get(0).getString("grade");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        new questionFirestoreBank(standardLabel).getFirestorequestions(new QuestionFirestoreAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> firestoreArrayList) {
                implementRecyclerView(firestoreArrayList);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_question_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.return_to_dashboard:
                startActivity(new Intent(CreateQuestionActivity.this, DashboardActivity.class));
                break;

            case R.id.sign_out:
                logOutUser();
                break;

            case R.id.view_test:

                database.collection("Users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userContent = documentSnapshot.getString("content");
                                userGrade = documentSnapshot.getString("grade");


                                database.collection("Tests").document(userContent + ": " + userGrade + ": " + firebaseAuth.getCurrentUser().getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                testTitle = documentSnapshot.getString("Test Title");

                                                Intent intent = new Intent(CreateQuestionActivity.this, EditExistingTestActivity.class);
                                                intent.putExtra("test_id", testId);
                                                intent.putExtra("title", testTitle);
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutUser() {
        if (currentUser != null && firebaseAuth != null) {
            firebaseAuth.signOut();
            startActivity(new Intent(CreateQuestionActivity.this, MainActivity.class));
        }
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
                        firebaseAuth = FirebaseAuth.getInstance();
                        currentUser = firebaseAuth.getCurrentUser();
                        String currentUserId = currentUser.getUid();

                        Intent intent = new Intent(CreateQuestionActivity.this, EditExistingTestActivity.class);
                        intent.putExtra("test_id", testId);
                        intent.putExtra("title", testTitle);
                        Log.d("test title!!!!!", "onSuccess: " + testTitle);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateQuestionActivity.this, "There was an issue adding your question", Toast.LENGTH_SHORT).show();
                        Log.d("Issue with Question Add", "onFailure: " + e.getMessage());
                    }
                });

        // add question to tests question section
        database.collection("Tests").document(testId).update("Questions", FieldValue.arrayUnion(questionforFirestore));

        Map<String, String> storeQuestionInfo = new HashMap<>();
        storeQuestionInfo.put(questionforFirestore.get("Question Text"), questionforFirestore.get("Question Text"));
        database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + firebaseAuth.getCurrentUser().getUid())
                .collection(questionforFirestore.get("Standard Label")).document(questionforFirestore.get("Question Text")).set(storeQuestionInfo);
//        database.collection("Standard Sets").document(userContent + ": " + userGrade + ": " + firebaseAuth.getCurrentUser().getUid())
//                .collection(questionforFirestore.get("Standard Label")).document("Questions").set(storeQuestionInfo, SetOptions.merge());

    }
}
