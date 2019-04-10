package com.iat359.jianghuidai_m3.viewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.iat359.jianghuidai_m3.LoginActivity;
import static android.content.Context.MODE_PRIVATE;

public class LogoutDialogFragment extends DialogFragment {
    private final static boolean STATUS_NOT_EXIST = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Log out")
                .setMessage("Are you sure you want to log out")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear the login status, log out
                        // back to log in activity
                        SharedPreferences prefs = getContext().getSharedPreferences("USER INFO", MODE_PRIVATE);
                        prefs.edit().remove("login_status_key").commit();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No, I want to stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        return builder.create();
    }
}
