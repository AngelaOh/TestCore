package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.CreateQuestionActivity;
import com.example.testcore.R;
import com.example.testcore.models.Standard;
import com.example.testcore.models.Test;

import java.util.ArrayList;

public class RecyclerViewAdapterTest extends RecyclerView.Adapter<RecyclerViewAdapterTest.ViewHolder>{

    private final Context context;
    private final ArrayList<Test> testList;

    public RecyclerViewAdapterTest(Context context, ArrayList<Test> testList) {
        this.context = context;
        this.testList = testList;

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
//        holder.description.setText(test.getDescription());
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
//        public TextView description;
//        public ImageView iconButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.test_title);
//            description = itemView.findViewById(R.id.description);
//            iconButton = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Test test = testList.get(position);

            Intent intent = new Intent(context, CreateQuestionActivity.class);
            intent.putExtra("label", test.getTitle());
//            intent.putExtra("description", standard.getDescription());

            context.startActivity(intent);
        }
    }
}
