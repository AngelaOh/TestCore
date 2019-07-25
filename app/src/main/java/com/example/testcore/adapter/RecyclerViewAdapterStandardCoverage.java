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
import com.example.testcore.models.Question;
import com.example.testcore.models.Standard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    private final Context context;
    private final ArrayList<Standard> standardList;
    private final HashMap<String, Integer> questionCount;

    public RecyclerViewAdapterStandardCoverage(Context context, ArrayList<Standard> standardList, HashMap<String, Integer> questionCount) {
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


        //pie chart
        List<SliceValue> questionCountData = new ArrayList<>();
        questionCountData.add(new SliceValue( Math.round(((double)questionCountWithZero/5) * 100), Color.CYAN ));
        questionCountData.add(new SliceValue( (100 - (Math.round(((double)questionCountWithZero/5) * 100))), Color.LTGRAY) );


        PieChartData pieCharData = new PieChartData(questionCountData);
        holder.pieChart.setPieChartData(pieCharData);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            standardLabel = itemView.findViewById(R.id.standard_coverage_label);
            standardDescription = itemView.findViewById(R.id.standard_coverage_description);
            standardQuestionCount = itemView.findViewById(R.id.standard_coverage_question_num);
            standardCard = itemView.findViewById(R.id.one_standard_coverage);
            pieChart = itemView.findViewById(R.id.chart);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
