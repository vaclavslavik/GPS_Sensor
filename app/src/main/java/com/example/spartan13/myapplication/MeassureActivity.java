package com.example.spartan13.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.spartan13.myapplication.model.GPSTracker;
import com.example.spartan13.myapplication.model.Recorder;
import com.example.spartan13.myapplication.model.RecorderSQLiteHelper;


public class MeassureActivity extends ActionBarActivity {

    private GPSTracker gpsTracker;
    private TextView textView;
    private Button button;
    private Button buttonBegin;
    private Context context;
    private boolean active = true;
    private Button buttonStop;
    private Button buttonSaveMeassure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;
        this.buttonBegin = (Button)findViewById(R.id.buttonBegin);
        this.textView = (TextView)findViewById(R.id.textView);
        this.textView.setText("");
        this.gpsTracker = new GPSTracker(this, textView);
        if (!this.gpsTracker.isGPSEnabled()){
            this.buildAlertMessageNoGps();
        }


        this.buttonSaveMeassure = (Button)findViewById(R.id.button_save_meassure);
        this.buttonSaveMeassure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeassure();
            }
        });


        this.buttonBegin = (Button)findViewById(R.id.buttonBegin);
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active = true;
                buttonStop.setText(R.string.stop_meassure);
                gpsTracker.renew();
            }
        };
        buttonBegin.setOnClickListener(l);

        this.buttonStop = (Button)findViewById(R.id.stopButton);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active = !active;
                if (active == true){
                    buttonStop.setText(R.string.stop_meassure);
                }else{
                    buttonStop.setText(R.string.begin_meassure);
                }
                gpsTracker.setActive(active);
            }
        });

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_not_enabled)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveMeassure(){

        Recorder recorder = this.gpsTracker.getRecorder();
        RecorderSQLiteHelper db = new RecorderSQLiteHelper(this);
        db.addRecorder(recorder);

        // přepnutí do hlavní aktivity


        this.finish();
        Intent nextScreen = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(nextScreen);
        this.finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
