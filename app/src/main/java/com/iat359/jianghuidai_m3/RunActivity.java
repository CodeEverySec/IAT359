package com.iat359.jianghuidai_m3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.ui.IconGenerator;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/*  Learn how to implement
    getLocationPermission(),
    onRequestPermissionsResult(),
    updateLocationUI()
    getDeviceLocation()
    from https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java
*/

public class RunActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;

    private static final String TAG = RunActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8696, 151.2094);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private BottomNavigationView bottomNavigationView;

    private Location mLastKnownLocation;

    private LatLng myStartPoint;
    private LatLng myEndPoint;

    private TextView durationTextView;

    private TimerTask timerTask;
    private Timer timer = new Timer();
    private final Handler handler = new Handler();

    private int timerCounter = 0;

    private TextView distanceTextView;
    private TextView caloriesTextView;

    private int distanceValue;
    private int speedValue;
    private int caloriesValue;

    private FloatingActionButton runButton;
    private boolean isRunning = false;

    private FloatingActionButton refreshButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // remove google logo
        // find from https://stackoverflow.com/questions/33729066/how-to-hide-google-logo-from-google-maps-by-adding-overlaid-image-on-google-maps
        final ViewGroup googleLogo = (ViewGroup) findViewById(R.id.map).findViewWithTag("GoogleWatermark").getParent();
        googleLogo.setVisibility(View.GONE);

        durationTextView = findViewById(R.id.duration_TextView);
        distanceTextView = findViewById(R.id.distance_for_run_TextView);
        caloriesTextView = findViewById(R.id.calories_forRun_TextView);

        runButton = findViewById(R.id.runButton);

        // learn how to count timer use timerTask and handler from https://android.okhelp.cz/timer-task-timertask-run-cancel-android-example/
        // runButton is pressed, timer start to count
        // the icon will also change to the stop icon
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    stopTimeTask();
                    runButton.setImageResource(R.drawable.ic_button_run);
                    isRunning = false;
                } else {
                    doTimerTask();
                    runButton.setImageResource(R.drawable.ic_button_stop);
                    isRunning = true;
                }
            }
        });

        // refresh button, set all the data to initial values again
        // only if the user is not running
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    updateTimeTask();
                    durationTextView.setText("00:00");
                    distanceTextView.setText("0");
                    caloriesTextView.setText("0");
                }
            }
        });

        // bottom navigation bar
        bottomNavigationView = findViewById(R.id.navigation_menu_for_run);
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(1).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_today:
                        finish();
                        startActivity(new Intent(RunActivity.this, TodayActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_run:
                        break;
                    case R.id.action_rank:
                        finish();
                        startActivity(new Intent(RunActivity.this, RankActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_me:
                        finish();
                        startActivity(new Intent(RunActivity.this, MeActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d("place", "Current location is null. Using defaults.");
                            Log.e("exception", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            // the original json file I got from https://snazzymaps.com/style/90982/uber-2017
            // then I edit the detail in https://mapstyle.withgoogle.com/
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can'timer find style. Error: ", e);
        }
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    // count timer
    private void doTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        timerCounter++;
                        int minutes = (timerCounter % 3600) / 60;
                        int seconds = timerCounter % 60;

                        String timeString = String.format("%02d:%02d", minutes, seconds);
                        durationTextView.setText(timeString);

                        distanceValue = timerCounter / 20 * 10;
                        distanceTextView.setText(Integer.toString(distanceValue));

                        caloriesValue = 10 * distanceValue;
                        caloriesTextView.setText(Integer.toString(caloriesValue));
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    // stop timer
    private void stopTimeTask() {
        if (timerTask != null) {
            int minutes = (timerCounter % 3600) / 60;
            int seconds = timerCounter % 60;

            String timeString = String.format("%02d:%02d", minutes, seconds);
            durationTextView.setText(timeString);
            timerTask.cancel();
        }

    }

    // restart the timeTask
    public void updateTimeTask() {
        timerCounter = 0;
    }
}
