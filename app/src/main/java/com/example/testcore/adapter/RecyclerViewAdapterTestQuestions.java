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
import com.example.testcore.models.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecyclerViewAdapterTestQuestions extends RecyclerView.Adapter<RecyclerViewAdapterTestQuestions.ViewHolder> {
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private final Context context;
    private final ArrayList<Question> questionList;
    private final String testId;
    private final String testTitle;

    public RecyclerViewAdapterTestQuestions(Context context, ArrayList<Question> questionList, String testId, String testTitle) {
        this.context = context;
        this.questionList = questionList;
        this.testId = testId;
        this.testTitle = testTitle;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterTestQuestions.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_test_existing_questions, parent, false);

        return new RecyclerViewAdapterTestQuestions.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterTestQuestions.ViewHolder holder, int position) {

        Question question = questionList.get(position);

        holder.questionText.setText(question.getQuestionText());
        holder.questionLabel.setText(question.getStandardLabel());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView questionText;
        public TextView questionLabel;
        public Button editQuestion;
        public ImageButton deleteQuestion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            deleteQuestion = itemView.findViewById(R.id.delete_question_bank);
            deleteQuestion.setOnClickListener(this);
            questionText = itemView.findViewById(R.id.one_test_question_bank);
            questionLabel = itemView.findViewById(R.id.one_test_standard_label_bank);
            editQuestion = itemView.findViewById(R.id.one_test_edit);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Question question = questionList.get(position);

            int deleteItemPosition = RecyclerViewAdapterTestQuestions.this.getItemViewType(-1);

            Log.d("view??", "onClick: " + view.getId());

            if (view.getId() == R.id.one_test_edit) {
                // go to edit question activity
            } else if (view.getId() == view.findViewById(R.id.delete_question_bank).getId()) {
                Log.d("REGISTER BUTTON", "onClick: ");

                database.collection("Questions").whereEqualTo("Question Text", question.getQuestionText()).get() //TODO: find more sound way of querying documentID
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                database.collection("Questions").document(documentId).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("DELETED QUESTION", "onSuccess: ");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Issue with deleting q", "onFailure: " + e.getMessage());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                notifyDataSetChanged();

                Intent refresh = new Intent(context, EditExistingTestActivity.class);
                refresh.putExtra("title", testTitle);
                refresh.putExtra("test_id", testId);
                context.startActivity(refresh);
            }


//
//
//            Intent intent = new Intent(context, EditExistingTestActivity.class);
//
//
//            context.startActivity(intent);
        }
    }
}
