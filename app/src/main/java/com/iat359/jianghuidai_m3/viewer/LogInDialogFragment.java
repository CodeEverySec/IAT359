package com.iat359.jianghuidai_m3.viewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.iat359.jianghuidai_m3.RegisterActivity;

import static android.content.Context.MODE_PRIVATE;

public class LogInDialogFragment extends DialogFragment {
    // create a dialog whenever the user insert wrong username or password
    // if user press 'Yes', he will be navigated to register page, old user information is deleted
    // if user press 'No', he will stay in the log in page
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Register Entry")
                .setMessage("Your information is not registered, go to register page?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefs = getContext().getSharedPreferences("USER INFO", MODE_PRIVATE);
                        prefs.edit().clear().commit();
                        SharedPreferences prefsForActivity = getContext().getSharedPreferences("USER ACTIVITY", MODE_PRIVATE);
                        prefsForActivity.edit().clear().commit();
                        Toast.makeText(getContext(), "Old user information is deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        return builder.create();
    }
}
