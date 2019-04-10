package com.iat359.jianghuidai_m3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.iat359.jianghuidai_m3.viewer.LogInDialogFragment;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView backgroundVideo;
    private MediaPlayer mediaPlayer;

    private EditText usernameEditText;
    private EditText passwordEditText;

    private String username;
    private String password;

    private Button loginButton;

    private TextView signUpTextView;

    private boolean loginStatus = false;

    private final static String NOT_EXIST = "NOT_EXIST";

    private final static boolean STATUS_NOT_EXIST = false;

    private final static int NO_VALUE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // learn how to hide status bar from https://developer.android.com/training/system-ui/status
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        backgroundVideo = findViewById(R.id.VideoView);

        // learn how to get the video uri from https://stackoverflow.com/questions/15675944/how-to-play-video-from-raw-folder-with-android-device
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_file);
        backgroundVideo.setVideoURI(uri);
        backgroundVideo.start();

        backgroundVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mediaPlayer.setLooping(true);
            }
        });

        usernameEditText = findViewById(R.id.login_usernameEditText);
        passwordEditText = findViewById(R.id.login_passwordEditText);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        signUpTextView = findViewById(R.id.signUp_TextView);

        // go to register page, if the user does not have a account
        // clear both user info and user activity data
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = LoginActivity.this.getSharedPreferences("USER INFO", MODE_PRIVATE);
                prefs.edit().clear().commit();
                SharedPreferences prefsForActivity = LoginActivity.this.getSharedPreferences("USER ACTIVITY", MODE_PRIVATE);
                prefsForActivity.edit().clear().commit();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ////////////////////////////////////////////////////////////////////////////
        // if user already logged in the app before, he will be navigated to the TodayActivity automatically
        SharedPreferences prefs = this.getSharedPreferences("USER INFO", MODE_PRIVATE);

        boolean registeredLoginStatus = prefs.getBoolean("login_status_key", STATUS_NOT_EXIST);
        String registeredFirstName = prefs.getString("first_name_key", NOT_EXIST);
        String registeredLastName = prefs.getString("last_name_key", NOT_EXIST);
        String registeredGender = prefs.getString("gender_key", NOT_EXIST);

        int registeredMoveMinute = prefs.getInt("move_minute_key", NO_VALUE);
        int registeredAge = prefs.getInt("age_key", NO_VALUE);
        int registeredWeight = prefs.getInt("weight_key", NO_VALUE);
        int registeredHeight = prefs.getInt("height_key", NO_VALUE);

        if (registeredLoginStatus != STATUS_NOT_EXIST
                && registeredFirstName != NOT_EXIST && registeredLastName != NOT_EXIST && registeredGender != NOT_EXIST
                && registeredMoveMinute != NO_VALUE && registeredAge != NO_VALUE
                && registeredWeight != NO_VALUE && registeredHeight != NO_VALUE) {
            // navigate to TodayActivity
            Intent intent = new Intent(LoginActivity.this, TodayActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {

        if (!usernameEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !passwordEditText.getText().toString().replaceAll("\\s", "").equals("")) {

            username = usernameEditText.getText().toString().replaceAll("\\s", "");
            password = passwordEditText.getText().toString().replaceAll("\\s", "");

            SharedPreferences prefs = this.getSharedPreferences("USER INFO", MODE_PRIVATE);

            String registeredUsername = prefs.getString("username_key", NOT_EXIST);
            String registeredPassword = prefs.getString("password_key", NOT_EXIST);

            // if user enter the right username and password, he will be navigated to settingActivity
            // otherwise old USER INFO are deleted, and there is a dialog pop up
            // if he press 'Yes', he will be navigated back to register page to register again
            // if he press 'No', he will stay in the log in page
            if (username.equals(registeredUsername) && password.equals(registeredPassword)) {
                // if the user log in to the app successfully, the app will remember its log in status unless the user chose to log out
                loginStatus = true;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("login_status_key", loginStatus);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, TodayActivity.class);
                startActivity(intent);
            } else {
                LogInDialogFragment dialog = new LogInDialogFragment();
                dialog.show(getSupportFragmentManager(), "Register Entry");
            }
        } else {
            Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
        }
    }

    // learn how to release the MediaPlayer object from https://www.youtube.com/watch?v=WLwQ3SJjWfY&t=478s
    // in order to hide status bar after the activity lose focus, I implement the same code again in the following methods
    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        backgroundVideo.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        backgroundVideo.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
