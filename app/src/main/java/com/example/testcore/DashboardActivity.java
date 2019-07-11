package com.example.testcore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOutButton, backButton, viewAllButton, createTestButton, createQuestionButton;
    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logOutButton = findViewById(R.id.log_out_button);
        backButton = findViewById(R.id.back_button);
        viewAllButton = findViewById(R.id.view_all_tests);
        createTestButton = findViewById(R.id.create_new_test);
        createQuestionButton = findViewById(R.id.create_new_question);
        welcomeMessage = findViewById(R.id.welcome_message);

        logOutButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);
        createTestButton.setOnClickListener(this);
        createQuestionButton.setOnClickListener(this);

        String userName = getIntent().getStringExtra("login_name");
        Log.d("USERNAME FROM MAIN", "onCreate: " + userName);
//        Log.d("checking back button", "onClick: IN BACK BUTTON CLICK LISTENER");

        welcomeMessage.setText("Welcome, " + userName);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.log_out_button) {
            Toast.makeText(DashboardActivity.this, "log out button clicked", Toast.LENGTH_LONG).show();
        }

        else if (view.getId() == R.id.back_button) {
//             Toast.makeText(DashboardActivity.this, "back button clicked", Toast.LENGTH_LONG).show();
            Log.d("checking back button", "onClick: IN BACK BUTTON CLICK LISTENER");
            backButtonMethod();
        } else if (view.getId() == R.id.view_all_tests) {
            Toast.makeText(DashboardActivity.this, "view all button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.create_new_test) {
            Toast.makeText(DashboardActivity.this, "create new test button clicked", Toast.LENGTH_LONG).show();
        } else if (view.getId() == R.id.create_new_question) {
            Toast.makeText(DashboardActivity.this, "create new question button clicked", Toast.LENGTH_LONG).show();
        }
    }

    public void backButtonMethod() {
        Intent intent = getIntent();
        intent.putExtra("dashboard_callback", "Successfully went back");
        setResult(RESULT_OK, intent);
        finish();
    }
}
