package com.newgendevelopers3.fiyt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class Instagram extends AppCompatActivity {

    public WebView w;
    SwipeRefreshLayout myswipeinstagram;
    public InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.instagramToolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(Color.DKGRAY);

        //MobileAds.initialize(getApplicationContext(),"ca-app-pub-4882008364654829/5504215196");
        AdView ad=(AdView)findViewById(R.id.instagramBanner);
        AdRequest adRequest=new AdRequest.Builder().build();//addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();
        ad.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-4882008364654829/6526949998");

        AdRequest request = new AdRequest.Builder().build();//.addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();

        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });






        myswipeinstagram=(SwipeRefreshLayout)findViewById(R.id.swipeToRefresh3);
        w=new WebView(this);
        w.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        myswipeinstagram.addView(w);

        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        w.getSettings().setBuiltInZoomControls(true);
        w.getSettings().setLoadWithOverviewMode(true);
        w.getSettings().setDisplayZoomControls(false);
        w.getSettings().setUseWideViewPort(true);
        w.getSettings().setDomStorageEnabled(true);
        w.setWebViewClient(new MyWebViewClient());
        w.setWebChromeClient(new WebChromeClient());
        w.clearCache(true);
        w.clearHistory();
        w.loadUrl("https://www.instagram.com");

        myswipeinstagram.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        w.loadUrl(w.getUrl());
                    }
                }
        );
    }



    private void showInterstitial(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url)
        {

            if(url.startsWith("https://www.instagram.com")||url.startsWith("https://m.facebook.com")||url.startsWith("https://www.facebook.com")) {
                view.loadUrl(url);
                return true;
            }

            else{
                Intent i=new Intent(Instagram.this,ExternalLinkActivity.class);
                i.putExtra("URL",url);
                startActivity(i);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(myswipeinstagram!=null)
                myswipeinstagram.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            // TODO: Implement this method
            super.onPageFinished(view, url);
            if(myswipeinstagram!=null)
                myswipeinstagram.setRefreshing(false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(w!=null){
            w.onPause();
            w.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(w!=null) {
            w.onResume();
            w.resumeTimers();
        }
    }

    @Override
    public void finish() {
        if(w!=null){
            myswipeinstagram.removeView(w);
            w.clearHistory();
            w.clearCache(true);
            w.loadUrl("about:blank");
            w.onPause();
            w.pauseTimers();
            w = null;
        }
        if(myswipeinstagram!=null) {
            myswipeinstagram = null;
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if(w!=null){
            myswipeinstagram.removeView(w);
            w.clearHistory();
            w.clearCache(true);
            w.loadUrl("about:blank");
            w.onPause();
            w.pauseTimers();
            w = null;
        }
        if(myswipeinstagram!=null) {
            myswipeinstagram = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_instagram, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            w.reload();
            return true;
        }

        if (id == R.id.action_forward) {
            if(w.canGoForward())
                w.goForward();
            else
                Toast.makeText(this,"Can't Go Forward",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_backward) {
            if(w.canGoBack())
                w.goBack();
            else
             finish();
            return true;
        }
        if (id == R.id.action_cross) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (w.canGoBack()) {
                        w.goBack();
                        return true;
                    }
                }
            }
        return super.onKeyDown(keyCode, event);
    }

}
