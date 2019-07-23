package com.example.testcore;

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

import java.util.ArrayList;

public class EditExistingTestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionCount;
    private TextView testTitle;
    private Button addQuestion;

    private RecyclerView recyclerView;
    private RecyclerViewAdapterTestQuestions recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_existing_test);

        Bundle bundle = getIntent().getExtras();
        String incomingTitle = bundle.getString("title");
        final String incomingTestId = bundle.getString("test_id");

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
            Intent intent = new Intent(EditExistingTestActivity.this, CreateQuestionActivity.class);
//            intent.putExtra("label", )
            startActivity(intent);
        }

    }
}
