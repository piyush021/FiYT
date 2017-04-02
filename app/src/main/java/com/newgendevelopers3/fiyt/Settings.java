package com.newgendevelopers3.fiyt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class Settings extends AppCompatActivity {



    AppCompatCheckBox c;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Toast.makeText(Settings.this,"Restart the app after making changes.",Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.DKGRAY);

        c=(AppCompatCheckBox)findViewById(R.id.checkBox);

        sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(!sp.getBoolean("FLAG",false)){
            c.setChecked(false);
        }else{
            c.setChecked(true);
        }

        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    SharedPreferences.Editor e=sp.edit();
                    e.putBoolean("FLAG",true);
                    e.commit();
                    Background.forceStop();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);

                }else{
                    SharedPreferences.Editor e=sp.edit();
                    e.putBoolean("FLAG",false);
                    e.commit();
                    Background.forceStop();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imageviewer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cross) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
