package com.newgendevelopers3.fiyt;

/*
 This activity checks permission to draw over other apps
 FiYT can not run without this permission
*/

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //check permission in async task
        //sending reference of this class async task class will use to make toast etc.
        //async task class is defined below this class
        new CheckInBackground(this).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //This function will check the result of activity started by async task
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CANCELED) {
            finish();
        }

        if (requestCode == Constants.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!android.provider.Settings.canDrawOverlays(this)) {
                //permission not granted
                Toast.makeText(this, "Permission Not Granted.Exiting...", Toast.LENGTH_LONG).show();
                finish();
            } else {
                //permission granted
                //start the main activity
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }

    }
}

//using inner class is a bad idea
//class that checks for permission as AsyncTask
class CheckInBackground extends AsyncTask<Void, Void, Void> {

    //reference to Start activity
    Start start;

    public CheckInBackground(Start start) {
        this.start = start;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //only ask for permission in android version marshmallow or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //ask for permission if not granted already
            if (!android.provider.Settings.canDrawOverlays(start)) {
                start.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(start, "Please Allow FiYT To Draw Over Other Apps", Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + start.getPackageName())
                );
                start.startActivityForResult(intent, Constants.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                start.startActivity(new Intent(start, MainActivity.class));
                start.finish();
            }
        } else {
            //already have permission
            start.startActivity(new Intent(start, MainActivity.class));
            start.finish();
        }
        return null;
    }

}
