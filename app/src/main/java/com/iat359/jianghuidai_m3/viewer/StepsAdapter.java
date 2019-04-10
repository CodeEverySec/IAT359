package com.iat359.jianghuidai_m3.viewer;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iat359.jianghuidai_m3.R;
import com.iat359.jianghuidai_m3.stepsdb.StepsDbContract;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsRowHolder> {
    private Context context;
    private List<ContentValues> steps;
    private LayoutInflater inflater;

    public StepsAdapter(Context context, List<ContentValues> steps) {
        this.context = context;
        this.steps = steps;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public StepsRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = this.inflater.inflate(R.layout.steps_row, viewGroup, false);
        return new StepsRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsRowHolder stepsRowHolder, int i) {
        stepsRowHolder.update(steps.get(i).getAsString(StepsDbContract.StepsEntry.COLUMN_NAME),
                steps.get(i).getAsString(StepsDbContract.StepsEntry.COLUMN_TYPE));
    }

    @Override
    public int getItemCount() {
        return this.steps.size();
    }
}
