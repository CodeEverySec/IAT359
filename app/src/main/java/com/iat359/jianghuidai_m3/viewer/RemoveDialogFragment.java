package com.iat359.jianghuidai_m3.viewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.iat359.jianghuidai_m3.stepsdb.StepsDbManipulator;

public class RemoveDialogFragment extends DialogFragment {
    private StepsDbManipulator stepsDb;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure you want to delete all the data?")
                .setTitle("Remove all data")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stepsDb = new StepsDbManipulator(getActivity());
                        stepsDb.clearStepsEntry();
                        stepsDb.close();
                        Toast.makeText(getActivity(), "All data has been deleted", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }
}
