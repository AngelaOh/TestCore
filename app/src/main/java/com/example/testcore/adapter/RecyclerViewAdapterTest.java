package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.CreateQuestionActivity;
import com.example.testcore.EditExistingTestActivity;
import com.example.testcore.R;
import com.example.testcore.ViewEditTestsActivity;
import com.example.testcore.models.Standard;
import com.example.testcore.models.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerViewAdapterTest extends RecyclerView.Adapter<RecyclerViewAdapterTest.ViewHolder>{
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final Context context;
    private final ArrayList<Test> testList;
    private final String userGrade;
    private final String userContent;
    private final String standardSetId;

//    userGrade, userContent, standardSetId
    public RecyclerViewAdapterTest(Context context, ArrayList<Test> testList, String userGrade, String userContent, String standardSetId) {
        this.context = context;
        this.testList = testList;
        this.userGrade = userGrade;
        this.userContent = userContent;
        this.standardSetId = standardSetId;
    }


    @NonNull
    @Override
    public RecyclerViewAdapterTest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tests_row, parent, false);

        return new RecyclerViewAdapterTest.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterTest.ViewHolder holder, int position) {
        Test test = testList.get(position);

        holder.title.setText(test.getTitle());
        holder.dateCreated.setText("Created On: " + test.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public ImageButton deleteTest;
        public TextView dateCreated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.test_title);
            deleteTest = itemView.findViewById(R.id.delete_test_button);
            dateCreated = itemView.findViewById(R.id.test_creation_date);
            deleteTest.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Test test = testList.get(position);

            if (view.getId() == view.findViewById(R.id.delete_test_button).getId()) {

                Log.d("DELETE", "onClick: ACCESSING DELETE TEST BUTTON");
                database.collection("Tests").document(test.getTestId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent refresh = new Intent(context, ViewEditTestsActivity.class);
                                refresh.putExtra("user_grade", userGrade);
                                refresh.putExtra("user_content", userContent);
                                refresh.putExtra("standard_set_id", standardSetId);
                                context.startActivity(refresh);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            } else {

                Intent intent = new Intent(context, EditExistingTestActivity.class);
                intent.putExtra("title", test.getTitle());
                intent.putExtra("test_id", test.getTestId());

                context.startActivity(intent);
            }
        }
    }
}
