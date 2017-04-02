package com.newgendevelopers3.fiyt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.InputStream;

public class ImageViewer extends AppCompatActivity {


    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.imageViewerToolbar);
        setSupportActionBar(toolbar);


        AdView ad=(AdView)findViewById(R.id.bannerUpper);
        AdRequest adRequest=new AdRequest.Builder().build();//addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();
        ad.loadAd(adRequest);

        AdView adl=(AdView)findViewById(R.id.bannerLower);
        AdRequest adRequestl=new AdRequest.Builder().build();//addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();
        adl.loadAd(adRequestl);


        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-4882008364654829/6208892393");

        AdRequest request = new AdRequest.Builder().build();//.addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();

        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });





        ImageView imageview=(ImageView)findViewById(R.id.imageView1);
        imageview.setFitsSystemWindows(true);

        imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        String link=getIntent().getStringExtra("URL");
        new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
                .execute(link);
    }



    private void showInterstitial(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_imageviewer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_cross) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
