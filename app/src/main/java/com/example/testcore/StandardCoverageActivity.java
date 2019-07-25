package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.testcore.adapter.RecyclerViewAdapterQuestion;
import com.example.testcore.data.EachStandardAsyncResponse;
import com.example.testcore.data.EachTestAsyncResponse;
import com.example.testcore.data.StandardCoverageAsyncResponse;
import com.example.testcore.data.eachStandardCoveredBank;
import com.example.testcore.data.eachTestCoveredBank;
import com.example.testcore.data.standardCoverageBank;
import com.example.testcore.data.standardFirestoreBank;
import com.example.testcore.models.Standard;
import com.example.testcore.models.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private HashMap<String, Object> standardsCoverageHash = new HashMap<>();
    private String standardSetId;
    private String userGrade;
    private String userContent;
    private int standardsTotalNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_coverage);

        Intent incomingInfo = getIntent();
        Bundle bundle = incomingInfo.getExtras();
        standardSetId = bundle.getString("user_standard_set_id");
        userGrade = bundle.getString("user_grade");
        userContent = bundle.getString("user_content");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        totalStandardPercent = findViewById(R.id.standard_coverage_percent);
        totalStandardProgressBar = findViewById(R.id.standard_coverage_progressbar);
        recyclerView = findViewById(R.id.standard_coverage_recyclerView);

        // total number of standards for this teacher
        new standardCoverageBank(userContent, userGrade, currentUser.getUid()).getAllStandards(new StandardCoverageAsyncResponse() {
            @Override
            public void processFinished(HashMap<String, String> allStandardsHash) {
                Log.d("All STANDARDS", "processFinished: " + allStandardsHash.size());
                standardsTotalNum = allStandardsHash.size();
            }
        });

        // number of standards covered (needed to loop through tests and questions per test)
        new eachTestCoveredBank(userContent, userGrade, currentUser.getUid()).getIndividualTests(new EachTestAsyncResponse() {
            @Override
            public void processFinished(ArrayList<String> eachTestList) {
                Log.d("each test list", "processFinished: " + eachTestList);

                new eachStandardCoveredBank(userContent, userGrade, currentUser.getUid(), eachTestList).getIndividualStandards(new EachStandardAsyncResponse() {
                    @Override
                    public void processFinished(ArrayList<String> eachStandardsList) {

                        double percent = eachStandardsList.size()/((double)standardsTotalNum);
                        Log.d("standards used size", "processFinished: " + eachStandardsList.size());
                        Log.d("standards total size", "processFinished: " + standardsTotalNum);
                        Log.d("coverage percentage", "processFinished: " + percent);

                        String percentDisplay = Math.round(percent*100) + "%";
                        totalStandardPercent.setText(percentDisplay);
                        //(int)percent*100
                        Log.d("lalalala", "processFinished: " + Math.round(percent*100));
                        totalStandardProgressBar.setProgress((int)Math.round(percent*100));
                        totalStandardProgressBar.setBackgroundColor();
                        Log.d("string percentage", "processFinished: " + percentDisplay);
                    }
                });

            }
        });

        // number of questions per standard in recycler view

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
