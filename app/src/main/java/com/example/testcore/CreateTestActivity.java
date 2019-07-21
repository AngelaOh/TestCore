package com.example.testcore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class CreateTestActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView showStandard;
    private ImageButton addQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);

        showStandard = findViewById(R.id.standard_info_text);
        Intent getStandardInfo = getIntent();
        Bundle bundle = getStandardInfo.getExtras();
        String label = bundle.getString("label");
        String description = bundle.getString("description");
        String standardText = label + ": " + description;
        showStandard.setText(standardText);

        addQuestion = findViewById(R.id.plus_icon_button);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.plus_icon_button) {
            addQuestionDatabase();
        }
    }

    private void addQuestionDatabase() {



    }
}
