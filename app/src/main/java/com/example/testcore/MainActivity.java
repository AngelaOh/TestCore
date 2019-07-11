// TODO: Connect to Github
// TODO: Ability to Change Views
// TODO: Connect Firebase and Firestore
// TODO: Connect Volley for API Calls

package com.example.testcore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signinButton;
    private EditText signinName, signinEmail;
    private TextView signinHeader, signinOAuth, showMessages;

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
        }

    }
}