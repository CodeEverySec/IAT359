package com.iat359.jianghuidai_m3.viewer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iat359.jianghuidai_m3.R;

public class StepsRowHolder extends RecyclerView.ViewHolder {
    private TextView rankNameTextView;
    private TextView rankStepsTextView;


    public StepsRowHolder(@NonNull View itemView) {
        super(itemView);
        rankNameTextView = itemView.findViewById(R.id.rankName_textView);
        rankStepsTextView = itemView.findViewById(R.id.rankSteps_textView);
    }

    public void update(String name, String type) {
        rankNameTextView.setText(name);
        rankStepsTextView.setText("  " + type);
    }
}
