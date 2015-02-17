package com.example.spartan13.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.spartan13.myapplication.model.RecorderSQLiteHelper;

import java.io.File;


public class MenuActivity extends ActionBarActivity {

    private Button buttonCreateMeassure;
    private Button buttonTrainingDiary;
    private Button buttonSettings;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.buttonCreateMeassure = (Button)findViewById(R.id.buttonNewMeassure);
        this.buttonCreateMeassure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(getApplicationContext(), MeassureActivity.class);
                startActivity(nextScreen);
                finish();
            }
        });

        File dbFile = getDatabasePath(RecorderSQLiteHelper.DATABASE_NAME);

        this.buttonTrainingDiary = (Button)findViewById(R.id.buttonTrainingDiary);
        this.buttonTrainingDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(getApplicationContext(), TrainingDiaryActivity.class);
                startActivity(nextScreen);
            }
        });

        this.buttonSettings = (Button)findViewById(R.id.buttonSettings);
        this.buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(nextScreen);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
