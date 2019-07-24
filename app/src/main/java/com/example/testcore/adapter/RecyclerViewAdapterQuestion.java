package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.CreateQuestionActivity;
import com.example.testcore.EditExistingTestActivity;
import com.example.testcore.R;
import com.example.testcore.models.Question;
import com.example.testcore.models.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapterQuestion extends RecyclerView.Adapter<RecyclerViewAdapterQuestion.ViewHolder>{
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

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
        public ImageButton addQuestionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            questionText = itemView.findViewById(R.id.question_text_bank);
            questionLabel = itemView.findViewById(R.id.standard_label_bank);
            addQuestionButton = itemView.findViewById(R.id.add_question_bank);
            addQuestionButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Question question = questionList.get(position);
//            Log.d("INSIDE ADD QUESTION", "onClick: ");

            if (view.getId() == view.findViewById(R.id.add_question_bank).getId()) {
                // pull apart question into hash map
                // put question obj into firestore
                // redirect to EditExistingTestActivity

                final Map<String, String> questionforFirestore = new HashMap<>();
                questionforFirestore.put("Question Text", question.getQuestionText());
                questionforFirestore.put("Answer Choice A", question.getAnswerChoiceA());
                questionforFirestore.put("Answer Choice B", question.getAnswerChoiceB());
                questionforFirestore.put("Answer Choice C", question.getAnswerChoiceC());
                questionforFirestore.put("Answer Choice D", question.getAnswerChoiceD());
                questionforFirestore.put("Test Id", testId);
                questionforFirestore.put("Standard Label", question.getStandardLabel());

                database.collection("Tests").document(testId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                final String testTitle = documentSnapshot.getString("Test Title");

                                database.collection("Questions").add(questionforFirestore)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(context, "Question added successfully", Toast.LENGTH_SHORT).show();

                                                // test_id, title
                                                Intent intent = new Intent(context, EditExistingTestActivity.class);
                                                intent.putExtra("test_id", testId);
                                                intent.putExtra("title", testTitle);
                                                context.startActivity(intent);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "There was an issue adding this question", Toast.LENGTH_SHORT).show();
                                                Log.d("Issue Adding Question", "onFailure: " + e.getMessage());
                                            }
                                        });



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


            } else {

            }
        }
    }
}
