package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.EditExistingTestActivity;
import com.example.testcore.R;
import com.example.testcore.models.Question;
import com.example.testcore.models.Test;

import java.util.ArrayList;

public class RecyclerViewAdapterTestQuestions extends RecyclerView.Adapter<RecyclerViewAdapterTestQuestions.ViewHolder> {

    private final Context context;
    private final ArrayList<Question> questionList;
    private final String testId;

    public RecyclerViewAdapterTestQuestions(Context context, ArrayList<Question> questionList, String testId) {
        this.context = context;
        this.questionList = questionList;
        this.testId = testId;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            questionText = itemView.findViewById(R.id.one_test_question_bank);
            questionLabel = itemView.findViewById(R.id.one_test_standard_label_bank);
            editQuestion = itemView.findViewById(R.id.one_test_edit);
        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.one_test_edit) {
                // go to edit question activity
            }


//            int position = getAdapterPosition();
//            Question question = questionList.get(position);
//
//            Intent intent = new Intent(context, EditExistingTestActivity.class);
//            intent.putExtra("title", test.getTitle());
//            intent.putExtra("test_id", test.getTestId());
//
//            context.startActivity(intent);
        }
    }
}
