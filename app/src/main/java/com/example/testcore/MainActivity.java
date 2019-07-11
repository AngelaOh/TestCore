// TODO: Connect Firebase and Firestore
// TODO: Connect Volley for API Calls

package com.example.testcore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signinButton;
    private EditText signinName, signinEmail;
    private TextView signinHeader, signinOAuth, showMessages;

    private final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signinButton = findViewById(R.id.signin_button);
        signinName = findViewById(R.id.signin_name);
        signinEmail = findViewById(R.id.signin_email);
        signinHeader = findViewById(R.id.signin_header);
        signinOAuth = findViewById(R.id.OAuth_header);
        showMessages = findViewById(R.id.show_messages);

        signinButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.signin_button) {

            String name = signinName.getText().toString().trim();
            String email = signinEmail.getText().toString().trim();

            showMessages.setText("Sign In Name: " + name + "\n Sign In Email: " + email);

            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("login_name", name);
            startActivityForResult(intent, REQUEST_CODE);
             //startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            String successMessage = data.getStringExtra("dashboard_callback");
            Toast.makeText(MainActivity.this, successMessage, Toast.LENGTH_LONG).show();
        }
    }
}
