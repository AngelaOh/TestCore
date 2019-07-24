package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.testcore.adapter.RecyclerViewAdapterQuestion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StandardCoverageActivity extends AppCompatActivity implements View.OnClickListener {

    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private TextView totalStandardPercent;
    private ProgressBar totalStandardProgressBar;
    private RecyclerView recyclerView;
//    private RecyclerViewAdapterStandardCoverage recyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_coverage);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        totalStandardPercent = findViewById(R.id.standard_coverage_percent);
        totalStandardProgressBar = findViewById(R.id.standard_coverage_progressbar);
        recyclerView = findViewById(R.id.standard_coverage_recyclerView);

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
                startActivity(new Intent(StandardCoverageActivity.this, DashboardActivity.class));
                break;

            case R.id.sign_out:
                logOutUser();
                break;

            case R.id.view_all_tests:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutUser() {
        if (currentUser != null && firebaseAuth != null) {
            firebaseAuth.signOut();
            startActivity(new Intent(StandardCoverageActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {

    }
}
