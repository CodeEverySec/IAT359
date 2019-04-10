package com.iat359.jianghuidai_m3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import info.abdolahi.CircularMusicProgressBar;
import info.abdolahi.OnCircularSeekBarChangeListener;

public class TodayActivity extends AppCompatActivity implements SensorEventListener {
    private Toolbar todayToolBar;

    private TextView dateTextView;

    private CircularMusicProgressBar circularProgressBar;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float myAcceleration = 0.0f;
    private float myCurrentAcceleration = SensorManager.GRAVITY_EARTH;
    private float myLastAcceleration = SensorManager.GRAVITY_EARTH;

    private TextView todayMoveMinuteTextView;
    private TextView stepsTextView;
    private TextView caloriesTextView;
    private TextView milesTextView;

    private int registeredMoveMinute;

    private int todayMoveMinute;
    private int steps;
    private int calories;
    private float miles;

    private final static String NOT_EXIST = "NOT_EXIST";

    private final static int NO_VALUE = 0;

    private final static float NO_VALUE_FLOAT = 0.00f;

    private BottomNavigationView bottomNavigationView;

    private static final int notificationId = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        // create notification chanel
        createNotificationChannel();

        // I learned how to implement Toolbar from https://guides.codepath.com/android/Using-the-App-ToolBar
        todayToolBar = findViewById(R.id.today_toolBar);
        setSupportActionBar(todayToolBar);

        dateTextView = findViewById(R.id.toolbarData_textView);

        // I learned how to format the data from https://stackoverflow.com/questions/9816459/removing-time-from-a-date-object
        String date = DateFormat.getDateInstance(DateFormat.LONG).format(Calendar.getInstance().getTime());
        dateTextView.setText(date);

        /////////////////////////
        /////////////////////////
        // I used the Horizontal-Calendar library for the Horizontal-Calendar view
        // Learned how to implement Horizontal-Calendar library from https://github.com/Mulham-Raee/Horizontal-Calendar

        // starts before 1 month from now
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        // ends after 1 month from now
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.horizontalCalendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()
                .textSize(14, 14, 14)
                .showBottomText(false)
                .end()
                .defaultSelectedDate(startDate)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                dateTextView.setText(DateFormat.getDateInstance(DateFormat.LONG).format(date.getTime()));
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                // I learned how to insert activity to calendar from https://developer.android.com/guide/components/intents-common#AddEvent
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .putExtra(CalendarContract.Events.TITLE, "Steps Activity")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.getTimeInMillis());
                intent.setData(CalendarContract.Events.CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            }
        });

        /////////////////////////
        /////////////////////////
        // I used the circular-music-progressbar library for the circular progressbar
        // learned how to implement the progressbar from https://github.com/aliab/circular-music-progressbar
        circularProgressBar = findViewById(R.id.circular_progressBar);
        circularProgressBar.setProgressAnimationState(false);
        circularProgressBar.setOnCircularBarChangeListener(new OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularMusicProgressBar circularBar, int progress, boolean fromUser) {
                fromUser = false;
            }

            @Override
            public void onClick(CircularMusicProgressBar circularBar) {
            }

            @Override
            public void onLongPress(CircularMusicProgressBar circularBar) {
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // check if the accelerometer sensor exists in this device
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            Toast.makeText(this, "This device does not have the accelerometer", Toast.LENGTH_SHORT).show();
        }

        // retrieve all the relevant user info from shared preferences
        SharedPreferences prefsForUserInfo = this.getSharedPreferences("USER INFO", MODE_PRIVATE);
        SharedPreferences prefsForActivity = this.getSharedPreferences("USER ACTIVITY", MODE_PRIVATE);

        registeredMoveMinute = prefsForUserInfo.getInt("move_minute_key", NO_VALUE);

        todayMoveMinute = prefsForActivity.getInt("today_move_minute_key", NO_VALUE);
        steps = prefsForActivity.getInt("steps_key", NO_VALUE);
        calories = prefsForActivity.getInt("calories_key", NO_VALUE);
        miles = prefsForActivity.getFloat("miles_key", NO_VALUE_FLOAT);

        // set all the user activity info
        todayMoveMinuteTextView = findViewById(R.id.today_moveMinute_textView);
        todayMoveMinuteTextView.setText(Integer.toString(todayMoveMinute) + "/" + Integer.toString(registeredMoveMinute));

        // set the percentage bar to the according progress
        double percentage = 100.0 * todayMoveMinute / registeredMoveMinute;
        circularProgressBar.setValue((float) (percentage));

        stepsTextView = findViewById(R.id.steps_textView);
        stepsTextView.setText(Integer.toString(steps));

        caloriesTextView = findViewById(R.id.calories_textView);
        caloriesTextView.setText(Integer.toString(calories));

        milesTextView = findViewById(R.id.miles_textView);
        milesTextView.setText(String.format("%.02f", miles));

        // I learned the bottomNavigationView from https://developer.android.com/reference/android/support/design/widget/BottomNavigationView#getselecteditemid
        // I learned how to implement listener to the view from https://medium.com/@hitherejoe/exploring-the-android-design-support-library-bottom-navigation-drawer-548de699e8e0
        bottomNavigationView = findViewById(R.id.navigation_menu_for_today);
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(0).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_today:
                        break;
                    case R.id.action_run:
                        finish();
                        startActivity(new Intent(TodayActivity.this, RunActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_rank:
                        finish();
                        startActivity(new Intent(TodayActivity.this, RankActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_me:
                        finish();
                        startActivity(new Intent(TodayActivity.this, MeActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // learn how to access the web through intent from https://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theweathernetwork.com/ca/weather/british-columbia/surrey"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // learn how to set big icon for notification
        // from https://stackoverflow.com/questions/34225779/how-to-set-the-app-icon-as-the-notification-icon-in-the-notification-drawer
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Steps")
                .setContentText("Weather information, click me")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setDefaults(0)
                .setSound(null);
        notificationManager.notify(notificationId, builder.build());
    }


    // learned from https://developer.android.com/training/notify-user/build-notification
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "Welcome";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
        }
        // save the steps, calories, miles, move minutes to shared preference
        SharedPreferences prefs = this.getSharedPreferences("USER ACTIVITY", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("today_move_minute_key", todayMoveMinute);
        editor.putInt("steps_key", steps);
        editor.putInt("calories_key", calories);
        editor.putFloat("miles_key", miles);
        editor.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // use accelerometer to detect walking steps
        // I used the same way to detect user is walking or not from Assignment 2
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // movement
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float normal_of_g = (float) Math.sqrt(x * x + y * y + z * z);

            // normalize the accelerometer vector
            z = z / normal_of_g;

            myLastAcceleration = myCurrentAcceleration;
            myCurrentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = myCurrentAcceleration - myLastAcceleration;

            // I learned the concept of alpha and low-pass filter from official android website
            // https://developer.android.com/guide/topics/sensors/sensors_motion
            // alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            final float alpha = (float) 0.9;

            // Isolate the force of gravity with the low-pass filter to get accurate myAcceleration
            // Then combine with the delta acceleration to get myAcceleration
            myAcceleration = myAcceleration * alpha + delta;

            // 0.5 is the threshold I got on the physical device
            // if myAcceleration is greater than 0.5
            // you are moving
            // otherwise you are not moving
            // increment current steps by one is user is moving
            // the steps number is a proximate value
            if (myAcceleration > 0.5) {
                float currentSteps = 0;
                currentSteps++;
                if (currentSteps > 10) {
                    currentSteps = currentSteps / 10;
                }
                steps = (int) (currentSteps) + steps;
            }

            stepsTextView.setText(Integer.toString(steps));

            // move minute - my proximate formula
            // 50 steps = 1 min
            todayMoveMinute = (int) (steps / 50);
            todayMoveMinuteTextView.setText(Integer.toString(todayMoveMinute) + "/" + Integer.toString(registeredMoveMinute));

            // set the progressbar to the current value
            double percentage = 100.0 * todayMoveMinute / registeredMoveMinute;
            circularProgressBar.setProgressAnimationState(true);
            circularProgressBar.setValue((float) (percentage));

            // In http://www.kylesconverter.com/length/steps-to-meters
            // I found out 1 step = 0.8 meters
            // since 1 mile = 1609 meters
            // user's miles = (steps * 0.8) / 1609
            miles = (float) ((steps * 0.8) / 1609);
            milesTextView.setText(String.format("%.02f", miles));


            // calories calculation
            // I learned how to calculate calories from https://fitness.stackexchange.com/questions/25472/how-to-calculate-calorie-from-pedometer
            // the formula is Kcal/Min ~= 0.0005 * bodyMassKg * metersWalkedInAMin + 0.0035
            // so calories = (0.0005 * weight * (steps * 0.8)/ today move minute + 0.0035) * today move minute
            // I check the google fit app and found out the correct result at least need to be multiplied by 65
            SharedPreferences prefs = this.getSharedPreferences("USER INFO", MODE_PRIVATE);
            int weight = prefs.getInt("weight_key", NO_VALUE);

            double result = (0.0005 * weight * (steps * 0.8) / todayMoveMinute + 0.0035) * todayMoveMinute;
            calories = (int) (result) * 65;
            caloriesTextView.setText(Integer.toString(calories));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
