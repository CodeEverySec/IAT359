package com.iat359.jianghuidai_m3.viewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.iat359.jianghuidai_m3.R;
import com.iat359.jianghuidai_m3.stepsdb.StepsDbContract;
import com.iat359.jianghuidai_m3.stepsdb.StepsDbManipulator;

import java.util.List;

public class SearchDialogFragment extends DialogFragment {

    private StepsDbManipulator stepsDb;

    private SearchView searchInput;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_search, null))
                .setTitle("Search for user steps");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog dialogView = (Dialog) dialog;
                searchInput = dialogView.findViewById(R.id.search_input);

                if (!searchInput.getQuery().toString().replaceAll("\\s", "").equals("")) {
                    stepsDb = new StepsDbManipulator(getActivity());
                    // search for all the user who have same steps
                    List<ContentValues> users = stepsDb.selectTypeFromStepsEntry(searchInput.getQuery().toString());
                    stepsDb.close();
                    StringBuilder searchResultString = new StringBuilder();
                    for (ContentValues user : users) {
                        searchResultString.append(String.format("%s : %s\r\n",
                                user.getAsString(StepsDbContract.StepsEntry.COLUMN_NAME),
                                user.getAsString(StepsDbContract.StepsEntry.COLUMN_TYPE)));
                    }
                    Toast.makeText(getActivity(), searchResultString.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SearchDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
