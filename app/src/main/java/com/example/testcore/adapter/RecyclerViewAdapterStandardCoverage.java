package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.EditExistingTestActivity;
import com.example.testcore.R;
import com.example.testcore.models.Question;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecyclerViewAdapterStandardCoverage extends RecyclerView.Adapter<RecyclerViewAdapterStandardCoverage.ViewHolder> {
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final Context context;
    private final ArrayList<Standard> standardList;
    private final String questionCount;

    public RecyclerViewAdapterStandardCoverage(Context context, ArrayList<Standard> standardList, String questionCount) {
        this.context = context;
        this.standardList = standardList;
        this.questionCount = questionCount;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterStandardCoverage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.standard_coverage_row, parent, false);

        return new RecyclerViewAdapterStandardCoverage.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterStandardCoverage.ViewHolder holder, int position) {

        Standard standard = standardList.get(position);
        String questionCountString = "Count: " + questionCount;

        holder.standardLabel.setText(standard.getLabel());
        holder.standardDescription.setText(standard.getDescription());
        holder.standardQuestionCount.setText(questionCountString); // put in a question count
    }

    @Override
    public int getItemCount() {
        return standardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView standardLabel;
        public TextView standardDescription;
        public TextView standardQuestionCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            standardLabel = itemView.findViewById(R.id.standard_coverage_label);
            standardDescription = itemView.findViewById(R.id.standard_coverage_description);
            standardQuestionCount = itemView.findViewById(R.id.standard_coverage_question_num);
        }

        @Override
        public void onClick(View view) {

//            int position = getAdapterPosition();
//            Question question = questionList.get(position);
//
//                notifyDataSetChanged();
//
//                Intent refresh = new Intent(context, EditExistingTestActivity.class);
//                refresh.putExtra("title", testTitle);
//                refresh.putExtra("test_id", testId);
//                context.startActivity(refresh);
//            }
        }
    }
}
