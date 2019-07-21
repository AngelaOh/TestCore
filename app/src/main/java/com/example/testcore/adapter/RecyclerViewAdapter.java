package com.example.testcore.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testcore.CreateTestActivity;
import com.example.testcore.R;
import com.example.testcore.ViewEditTestsActivity;
import com.example.testcore.models.Standard;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Standard> standardList;

    public RecyclerViewAdapter(Context context, ArrayList<Standard> standardList) {
        this.context = context;
        this.standardList = standardList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.standards_row, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Standard standard = standardList.get(position);

        holder.label.setText(standard.getLabel());
        holder.description.setText(standard.getDescription());
    }

    @Override
    public int getItemCount() {
        return standardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView label;
        public TextView description;
        public ImageView iconButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            label = itemView.findViewById(R.id.label);
            description = itemView.findViewById(R.id.description);
            iconButton = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Standard standard = standardList.get(position);

            Intent intent = new Intent(context, CreateTestActivity.class);
            intent.putExtra("label", standard.getLabel());
            intent.putExtra("description", standard.getDescription());

            context.startActivity(intent);
        }
    }
}
