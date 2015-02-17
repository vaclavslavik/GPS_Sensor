package com.example.spartan13.myapplication;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.spartan13.myapplication.model.DateId;
import com.example.spartan13.myapplication.model.RecorderSQLiteHelper;

import java.util.ArrayList;


public class TrainingDiaryActivity extends ListActivity {

    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> listNames = new ArrayList<>();
    private Boolean orderDesc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.createAll();
    }

    private void createAll(){
        boolean orderDesc = SettingsActivity.getOrderDesc(this);
        RecorderSQLiteHelper db = new RecorderSQLiteHelper(this);
        this.ids = db.getTrackIds(orderDesc);
        this.listNames = db.getListNames(orderDesc);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.training_diary_list, this.listNames));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TrainingDiaryActivity.this, OneTrackActivity.class);
                intent.putExtra(OneTrackActivity.TRACK_ID, ids.get(position));

                startActivity(intent);
            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onLongClick(ids.get(position));
                return true;
            }
        });
    }

    private void onLongClick(final int trackId){
        final RecorderSQLiteHelper databaseInClick = new RecorderSQLiteHelper(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.delete_track_id)+""+trackId);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.confirm_delete_track)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        databaseInClick.deleteTrack(trackId);
                        dialog.cancel();
                        createAll();
                    }
                })
                .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training_diary, menu);
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
