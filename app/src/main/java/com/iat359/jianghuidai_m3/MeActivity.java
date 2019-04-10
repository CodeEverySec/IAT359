package com.iat359.jianghuidai_m3;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iat359.jianghuidai_m3.viewer.LogoutDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MeActivity extends AppCompatActivity {
    private Toolbar meToolBar;
    private ImageView thumbnailImageView;

    public String currentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;

    static final int PERMISSIONS_REQUEST_CAMERA = 0;

    private TextView meMoveMinuteEditText;

    private TextView meGenderEditText;
    private TextView meAgeEditText;
    private TextView meWeightEditText;
    private TextView meHeightEditText;

    private final static String NOT_EXIST = "NOT_EXIST";

    private final static int NO_VALUE = 0;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        meToolBar = findViewById(R.id.me_toolBar);
        setSupportActionBar(meToolBar);

        thumbnailImageView = findViewById(R.id.thumbnail_imageView);

        // whenever user click the image view
        // it will open the camera
        thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        thumbnailImageView.setClickable(true);

        ////////////////
        // get user info
        SharedPreferences prefs = this.getSharedPreferences("USER INFO", MODE_PRIVATE);

        // get user profile picture uri
        String registeredPhotoPath = prefs.getString("path_key", NOT_EXIST);

        // if the image uri is equals to NOT_EXIST, set the profile picture to the default
        // otherwise set the profile picture to the picture user took last time
        if (registeredPhotoPath != NOT_EXIST) {
            setPic(registeredPhotoPath);
        } else {
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.ic_action_me);
            thumbnailImageView.setImageDrawable(drawable);
        }

        // get user info
        String firstName = prefs.getString("first_name_key", NOT_EXIST);
        String lastName = prefs.getString("last_name_key", NOT_EXIST);
        String gender = prefs.getString("gender_key", NOT_EXIST);

        int moveMinute = prefs.getInt("move_minute_key", NO_VALUE);
        int age = prefs.getInt("age_key", NO_VALUE);
        int weight = prefs.getInt("weight_key", NO_VALUE);
        int height = prefs.getInt("height_key", NO_VALUE);

        // set title for tool bar
        meToolBar.setTitle(firstName + "  " + lastName);

        // set move minute
        meMoveMinuteEditText = findViewById(R.id.me_moveMinute_editText);
        meMoveMinuteEditText.setText(Integer.toString(moveMinute));
        meMoveMinuteEditText.setPaintFlags(meMoveMinuteEditText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // set gender
        meGenderEditText = findViewById(R.id.me_gender_editText);
        meGenderEditText.setText(gender);
        meGenderEditText.setPaintFlags(meGenderEditText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // set age
        meAgeEditText = findViewById(R.id.me_age_editText);
        meAgeEditText.setText(Integer.toString(age));
        meAgeEditText.setPaintFlags(meAgeEditText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // set weight
        meWeightEditText = findViewById(R.id.me_weight_editText);
        meWeightEditText.setText(Integer.toString(weight));
        meWeightEditText.setPaintFlags(meWeightEditText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // set height
        meHeightEditText = findViewById(R.id.me_height_editText);
        meHeightEditText.setText(Integer.toString(height));
        meHeightEditText.setPaintFlags(meHeightEditText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // bottom navigation bar
        bottomNavigationView = findViewById(R.id.navigation_menu_for_me);
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(3).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_today:
                        finish();
                        startActivity(new Intent(MeActivity.this, TodayActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_run:
                        finish();
                        startActivity(new Intent(MeActivity.this, RunActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_rank:
                        finish();
                        startActivity(new Intent(MeActivity.this, RankActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_me:
                        break;
                }
                return true;
            }
        });
    }

    // create a image file with a unique name
    // learned from https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // initiate a implicit intent - image capture
    // at the same time, create the image file based on its uri
    // learned from https://developer.android.com/training/camera/photobasics
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.iat359.jianghuidai_m3.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // after user take a picture
    // MeActivity will set the profile picture to that picture
    // and then save the image uri to sharedPreference
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic(currentPhotoPath);

            // save the image uri to sharedPreferences
            SharedPreferences prefs = MeActivity.this.getSharedPreferences("USER INFO", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("path_key", currentPhotoPath);
            editor.commit();
        }
    }

    // set the bitmap view
    // learned from https://developer.android.com/training/camera/photobasics
    private void setPic(String photoPath) {
        // Get the dimensions of the View
        int targetW = 80;
        int targetH = 80;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        thumbnailImageView.setImageBitmap(bitmap);
    }

    // inflate the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.me_toolbar_menu, menu);
        return true;
    }

    // handle the action button select event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // log out
            case R.id.action_logOut:
                LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment();
                logoutDialogFragment.show(getSupportFragmentManager(), "logout");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
