// TODO: add progress bar

package com.example.testcore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private Button createAccountButton;
    private EditText createAccountName, createAccountEmail, createAccountPassword, createAccountState, createAccountGrade, createAccountContent;
    private final int REQUEST_CODE = 123;

    // Firebase Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = database.collection("Users");

    // Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccountName = findViewById(R.id.create_account_name);
        createAccountEmail = findViewById(R.id.create_account_email);
        createAccountPassword = findViewById(R.id.create_account_password);
        createAccountState = findViewById(R.id.create_account_state);
        createAccountGrade = findViewById(R.id.create_account_grade);
        createAccountContent = findViewById(R.id.create_account_content);

        createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(this);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Toast.makeText(CreateAccountActivity.this, "Hi, " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    // user is not logged in
                }
            }
        };
    }

    @Override
    public void onClick(View view) {

        String name = createAccountName.getText().toString().trim();
        String email = createAccountEmail.getText().toString().trim();
        String password = createAccountPassword.getText().toString().trim();
        String state = createAccountState.getText().toString().trim();
        String grade = createAccountGrade.getText().toString().trim();
        String content = createAccountContent.getText().toString().trim();

        Log.d("SEE CONTENT", "onClick: " + (name instanceof String));

        if (view.getId() == R.id.create_account_button) {
            if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {

                createUserEmailAccount(email, password, name);

            } else {
                Toast.makeText(CreateAccountActivity.this, "Empty fields not allowed!", Toast.LENGTH_LONG).show();
            }
        }

//        addData(name, email, state, grade, content);

    }

    private void createUserEmailAccount(String email, String password, final String name) {
        if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {

            // progress bar to visible here

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {

                              // get current user info and put in firestore database
                              currentUser = firebaseAuth.getCurrentUser();
                              final String currentUserId = currentUser.getUid();

                              // create user map so we can create a user in the User collection of Firestore
                              Map<String, String> userObj = new HashMap<>();
                              userObj.put("userId", currentUserId);
                              userObj.put("username", name);
//                              Log.d("MOVE TO DASH", "onComplete: " + currentUserId);


                              // save to Firestore
                              collectionReference.add(userObj)
                                      .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                          @Override
                                          public void onSuccess(DocumentReference documentReference) {
                                              documentReference.get()
                                                      .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                              Log.d("MOVE TO DASH", "onComplete: " + currentUserId);

                                                              if (Objects.requireNonNull(task.getResult().exists())) {
                                                                  // set progress bar to invisible
                                                                  String name = task.getResult()
                                                                          .getString("name");

                                                                  Intent intent = new Intent(CreateAccountActivity.this,
                                                                          DashboardActivity.class);
                                                                  intent.putExtra("username", name);
                                                                  intent.putExtra("userId", currentUserId);
                                                                  startActivity(intent);
                                                                  Log.d("MOVE TO DASH", "onComplete: " + currentUserId);


                                                              } else {
                                                                  // set progress bar to invisible
                                                                  // maybe some logcat error message
                                                              }


                                                          }
                                                      });
                                          }
                                      })
                                      .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Log.d("FAILED TO MAKE ACCOUNT", "onFailure: " + e.getMessage());
                                          }
                                      });
                          }
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                   }
             });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void addData(String name, String email, String state, String grade, String content) {

        DocumentReference addUser = database.collection(name).document(name + "Info");
        Map<String, Object> general_data = new HashMap<>();
        general_data.put("NAME", name);
        general_data.put("EMAIL", email);
        general_data.put("STATE", state);
        addUser.set(general_data);

        DocumentReference addContent = database.collection(name).document(name + " Preps");
        Map<String, Object> prep_data = new HashMap<>();
        prep_data.put("CONTENT", content);
        prep_data.put("GRADE", grade);
        addContent.set(prep_data);

    }
}
