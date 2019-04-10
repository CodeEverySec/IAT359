package com.iat359.jianghuidai_m3;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.iat359.jianghuidai_m3.stepsdb.StepsDbContract;
import com.iat359.jianghuidai_m3.stepsdb.StepsDbManipulator;
import com.iat359.jianghuidai_m3.viewer.InsertDialogFragment;
import com.iat359.jianghuidai_m3.viewer.RemoveDialogFragment;
import com.iat359.jianghuidai_m3.viewer.SearchDialogFragment;
import com.iat359.jianghuidai_m3.viewer.StepsAdapter;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {
    private Toolbar rankToolBar;

    private RecyclerView stepsRecyclerView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        rankToolBar = findViewById(R.id.rank_toolBar);
        setSupportActionBar(rankToolBar);

        stepsRecyclerView = findViewById(R.id.steps_RecyclerView);

        DividerItemDecoration myDividerItemDecoration = new DividerItemDecoration(stepsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable myDivider = this.getResources().getDrawable(R.drawable.recycleview_divider);

        myDividerItemDecoration.setDrawable(myDivider);
        stepsRecyclerView.addItemDecoration(myDividerItemDecoration);

        StepsDbManipulator db = new StepsDbManipulator(getApplicationContext());

        // add 9 fake user data to the table if the table is empty
        if (db.selectAllFromStepsEntry().size() == 0) {
            List<ContentValues> fakeUserData = new ArrayList<>(9);
            // 1
            ContentValues dataOne = new ContentValues();
            dataOne.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Emma");
            dataOne.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "120");
            fakeUserData.add(dataOne);
            // 2
            ContentValues dataTwo = new ContentValues();
            dataTwo.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Olivia");
            dataTwo.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "100");
            fakeUserData.add(dataTwo);
            // 3
            ContentValues dataThree = new ContentValues();
            dataThree.put(StepsDbContract.StepsEntry.COLUMN_NAME, "James");
            dataThree.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "80");
            fakeUserData.add(dataThree);
            // 4
            ContentValues dataFour = new ContentValues();
            dataFour.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Logan");
            dataFour.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "60");
            fakeUserData.add(dataFour);
            // 5
            ContentValues dataFive = new ContentValues();
            dataFive.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Jacob");
            dataFive.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "40");
            fakeUserData.add(dataFive);
            // 6
            ContentValues dataSix = new ContentValues();
            dataSix.put(StepsDbContract.StepsEntry.COLUMN_NAME, "William");
            dataSix.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "20");
            fakeUserData.add(dataSix);
            // 7
            ContentValues dataSeven = new ContentValues();
            dataSeven.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Isabella");
            dataSeven.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "10");
            fakeUserData.add(dataSeven);
            // 8
            ContentValues dataEight = new ContentValues();
            dataEight.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Ethan");
            dataEight.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "5");
            fakeUserData.add(dataEight);
            // 9
            ContentValues dataNine = new ContentValues();
            dataNine.put(StepsDbContract.StepsEntry.COLUMN_NAME, "Michael");
            dataNine.put(StepsDbContract.StepsEntry.COLUMN_TYPE, "0");
            fakeUserData.add(dataNine);
            // insert these fake user data
            for (int i = 0; i < fakeUserData.size(); i++) {
                long rowid = db.insertToStepsEntry(fakeUserData.get(i).get(StepsDbContract.StepsEntry.COLUMN_NAME).toString(),
                        fakeUserData.get(i).get(StepsDbContract.StepsEntry.COLUMN_TYPE).toString());
            }
        }
        List<ContentValues> steps = db.selectAllFromStepsEntry();
        db.close();

        // set the RecyclerView
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        stepsRecyclerView.setAdapter(new StepsAdapter(getApplicationContext(), steps));

        // bottom navigation bar
        bottomNavigationView = findViewById(R.id.navigation_menu_for_rank);
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(2).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_today:
                        finish();
                        startActivity(new Intent(RankActivity.this, TodayActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_run:
                        finish();
                        startActivity(new Intent(RankActivity.this, RunActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.action_rank:
                        break;
                    case R.id.action_me:
                        finish();
                        startActivity(new Intent(RankActivity.this, MeActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });
    }

    // inflate the menu
    // https://developer.android.com/guide/topics/ui/menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rank_toolbar_menu, menu);
        return true;
    }

    // handle item select event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // insert
            case R.id.action_insert:
                InsertDialogFragment insertDialogFragment = new InsertDialogFragment();
                insertDialogFragment.show(getSupportFragmentManager(), "insert");
                return true;
            // remove
            case R.id.action_remove:
                RemoveDialogFragment removeDialogFragment = new RemoveDialogFragment();
                removeDialogFragment.show(getSupportFragmentManager(), "remove");
                return true;
            // search
            case R.id.action_search:
                SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
                searchDialogFragment.show(getSupportFragmentManager(), "search");
                return true;
            // refresh
            case R.id.action_refresh:
                StepsDbManipulator db = new StepsDbManipulator(getApplicationContext());
                List<ContentValues> steps = db.selectAllFromStepsEntry();
                stepsRecyclerView.setAdapter(new StepsAdapter(getApplicationContext(), steps));
                db.close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
