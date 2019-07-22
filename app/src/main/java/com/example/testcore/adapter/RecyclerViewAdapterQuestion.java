package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.CreateQuestionActivity;
import com.example.testcore.R;
import com.example.testcore.models.Question;
import com.example.testcore.models.Test;

import java.util.ArrayList;

public class RecyclerViewAdapterQuestion extends RecyclerView.Adapter<RecyclerViewAdapterQuestion.ViewHolder>{

    private final Context context;
    private final ArrayList<Question> questionList;
    private final String testId;

    public RecyclerViewAdapterQuestion(Context context, ArrayList<Question> questionList, String testId) {
        this.context = context;
        this.questionList = questionList;
        this.testId = testId;
    }


    @NonNull
    @Override
    public RecyclerViewAdapterQuestion.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_row, parent, false);

        return new RecyclerViewAdapterQuestion.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterQuestion.ViewHolder holder, int position) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            questionText = itemView.findViewById(R.id.question_text_bank);
            questionLabel = itemView.findViewById(R.id.standard_label_bank);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Question question = questionList.get(position);

            Intent intent = new Intent(context, CreateQuestionActivity.class);
            intent.putExtra("title", question.getQuestionText());

            context.startActivity(intent);
        }
    }
}
