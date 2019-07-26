package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorStateListDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.EditExistingTestActivity;
import com.example.testcore.R;
import com.example.testcore.data.QuestionforStandardAsyncResponse;
import com.example.testcore.data.eachTestCoveredBank;
import com.example.testcore.data.questionsforStandardSetBank;
import com.example.testcore.models.Question;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static android.graphics.BlendMode.COLOR;

public class RecyclerViewAdapterStandardCoverage extends RecyclerView.Adapter<RecyclerViewAdapterStandardCoverage.ViewHolder> {
    // Connection to Firestore
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private final Context context;
    private final ArrayList<Standard> standardList;
    private final HashMap<String, Integer> questionCount;
    private final ArrayList<String> testList;
    private final String userContent;
    private final String userGrade;
    private String testText;

    public RecyclerViewAdapterStandardCoverage(Context context, ArrayList<Standard> standardList, HashMap<String, Integer> questionCount, ArrayList<String> testList, String userContent, String userGrade) {
        this.context = context;
        this.standardList = standardList;
        this.questionCount = questionCount;
        this.testList = testList;
        this.userContent = userContent;
        this.userGrade = userGrade;

    }

    @NonNull
    @Override
    public RecyclerViewAdapterStandardCoverage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.standard_coverage_row, parent, false);

        return new RecyclerViewAdapterStandardCoverage.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterStandardCoverage.ViewHolder holder, int position) {

        final Standard standard = standardList.get(position);

        Integer questionCountWithZero;
        if (questionCount.get(standard.getLabel()) == null) {
            questionCountWithZero = 0;
        } else {
            questionCountWithZero = questionCount.get(standard.getLabel());
        }
        String questionCountString = "Count: " + (questionCountWithZero);

        holder.standardLabel.setText(standard.getLabel());
        holder.standardDescription.setText(standard.getDescription());
        holder.standardQuestionCount.setText(questionCountString); // put in a question count


        ///////////pie chart
        List<SliceValue> questionCountData = new ArrayList<>();
        questionCountData.add(new SliceValue( Math.round(((double)questionCountWithZero/5) * 100), Color.CYAN ));
        questionCountData.add(new SliceValue( (100 - (Math.round(((double)questionCountWithZero/5) * 100))), Color.LTGRAY) );


        PieChartData pieCharData = new PieChartData(questionCountData);
        holder.pieChart.setPieChartData(pieCharData);

        /////// find tests associated with user --> test id
        Log.d("TEST LIST", "onBindViewHolder: " + testList);

        new questionsforStandardSetBank(standard.getLabel(), userContent, userGrade).getQuestionText(new QuestionforStandardAsyncResponse() {
            @Override
            public void processFinished(String questionText) {
                holder.questionsSpecific.setText(questionText);
            }
        });

    }
    @Override
    public int getItemCount() {
        return standardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView standardLabel;
        public TextView standardDescription;
        public TextView standardQuestionCount;
        public CardView standardCard;
        public PieChartView pieChart;
        public TextView questionsSpecific;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            standardLabel = itemView.findViewById(R.id.standard_coverage_label);
            standardDescription = itemView.findViewById(R.id.standard_coverage_description);
            standardQuestionCount = itemView.findViewById(R.id.standard_coverage_question_num);
            standardCard = itemView.findViewById(R.id.one_standard_coverage);
            pieChart = itemView.findViewById(R.id.chart);
            questionsSpecific = itemView.findViewById(R.id.standard_coverage_questions_text);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
