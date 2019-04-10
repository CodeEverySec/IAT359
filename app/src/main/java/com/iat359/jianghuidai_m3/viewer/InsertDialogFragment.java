package com.iat359.jianghuidai_m3.viewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.iat359.jianghuidai_m3.R;
import com.iat359.jianghuidai_m3.stepsdb.StepsDbManipulator;

// I learned how to implement this class from https://developer.android.com/guide/topics/ui/dialogs
public class InsertDialogFragment extends DialogFragment {
    private StepsDbManipulator stepsDb;

    private EditText insertUserName;
    private EditText insertSteps;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_insert, null))
                .setTitle("Insert user data");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // learned how to get these two editText from
                // https://stackoverflow.com/questions/12799751/android-how-do-i-retrieve-edittext-gettext-in-custom-alertdialog
                Dialog dialogView = (Dialog) dialog;
                insertUserName = dialogView.findViewById(R.id.insert_username);
                insertSteps = dialogView.findViewById(R.id.insert_steps);

                if (!insertUserName.getText().toString().replaceAll("\\s", "").equals("") && !insertSteps.getText().toString().replaceAll("\\s", "").equals("")) {
                    // insert the input to the database
                    stepsDb = new StepsDbManipulator(getActivity());
                    long rowid = stepsDb.insertToStepsEntry(
                            insertUserName.getText().toString(),
                            insertSteps.getText().toString());
                    stepsDb.close();
                    Toast.makeText(getActivity(), String.format("The user %s data is added", insertUserName.getText().toString()), Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InsertDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
