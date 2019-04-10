package com.iat359.jianghuidai_m3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // first name  + last name
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private String firstName;
    private String lastName;

    // username + password
    private EditText usernameEditText;
    private EditText passwordEditText;
    private String username;
    private String password;

    // move minute
    private EditText moveMinuteEditText;
    private int moveMinute;

    // personal info
    private EditText genderEditText;
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private String gender;
    private int age;
    private int height;
    private int weight;

    private Button registerButton;

    private final static String NOT_EXIST = "NOT_EXIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameEditText = findViewById(R.id.firstName_editText);
        lastNameEditText = findViewById(R.id.lastName_editText);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        moveMinuteEditText = findViewById(R.id.moveMinute_editText);

        genderEditText = findViewById(R.id.gender_editText);
        ageEditText = findViewById(R.id.age_editText);
        heightEditText = findViewById(R.id.height_editText);
        weightEditText = findViewById(R.id.weight_editText);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // if user enter all the information and then press the register button
        // they will be navigated to the log in page
        if (!usernameEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !passwordEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !firstNameEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !lastNameEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !moveMinuteEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !genderEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !ageEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !heightEditText.getText().toString().replaceAll("\\s", "").equals("") &&
                !weightEditText.getText().toString().replaceAll("\\s", "").equals("")) {

            firstName = firstNameEditText.getText().toString().replaceAll("\\s", "");
            lastName = lastNameEditText.getText().toString().replaceAll("\\s", "");
            username = usernameEditText.getText().toString().replaceAll("\\s", "");
            password = passwordEditText.getText().toString().replaceAll("\\s", "");
            gender = genderEditText.getText().toString().replaceAll("\\s", "");


            moveMinute = Integer.parseInt(moveMinuteEditText.getText().toString().replaceAll("\\s", ""));
            age = Integer.parseInt(ageEditText.getText().toString().replaceAll("\\s", ""));
            height = Integer.parseInt(heightEditText.getText().toString().replaceAll("\\s", ""));
            weight = Integer.parseInt(weightEditText.getText().toString().replaceAll("\\s", ""));

            SharedPreferences prefs = this.getSharedPreferences("USER INFO", MODE_PRIVATE);

            // clean the old user info
            prefs.edit().clear().commit();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("first_name_key", firstName);
            editor.putString("last_name_key", lastName);
            editor.putString("username_key", username);
            editor.putString("password_key", password);
            editor.putString("gender_key", gender);
            editor.putString("path_key", NOT_EXIST);

            editor.putInt("move_minute_key", moveMinute);
            editor.putInt("age_key", age);
            editor.putInt("height_key", height);
            editor.putInt("weight_key", weight);
            editor.apply();

            Toast.makeText(this, prefs.getString("first_name_key", NOT_EXIST) + " are registered", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "All the information should be filed", Toast.LENGTH_SHORT).show();
        }
    }
}
