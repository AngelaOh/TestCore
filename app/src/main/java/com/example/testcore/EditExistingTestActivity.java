package com.example.testcore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.testcore.adapter.RecyclerViewAdapter;
import com.example.testcore.adapter.RecyclerViewAdapterTestQuestions;

public class EditExistingTestActivity extends AppCompatActivity {

    private TextView questionCount;
    private TextView testTitle;

    private RecyclerView recyclerView;
    private RecyclerViewAdapterTestQuestions recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_existing_test);

        Bundle bundle = getIntent().getExtras();
        String incomingTitle = bundle.getString("title");
        String incomingTestId = bundle.getString("test_id");

        questionCount = findViewById(R.id.question_count_text);
        testTitle = findViewById(R.id.existing_test_title);
        testTitle.setText(incomingTitle);

//        recyclerView = findViewById(R.id.standard_recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
