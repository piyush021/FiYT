package com.newgendevelopers3.fiyt;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ExternalLinkActivity extends AppCompatActivity {

    WebView w;
    private String myurl, s;
    Boolean isFinished;
    SwipeRefreshLayout swipeExternal;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_link);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.externalToolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.DKGRAY);
        toolbar.setTitle("FiYT");
        swipeExternal = (SwipeRefreshLayout) findViewById(R.id.swipeExternal);

        w=new WebView(this);
        w.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        swipeExternal.addView(w);

        AdView ad=(AdView)findViewById(R.id.externalBanner);
        AdRequest adRequest=new AdRequest.Builder().build();//addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();
        ad.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-4882008364654829/5011360794");

        AdRequest request = new AdRequest.Builder().build();//.addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();

        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });


        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        w.getSettings().setLoadWithOverviewMode(true);
        w.getSettings().setBuiltInZoomControls(true);
        w.getSettings().setDisplayZoomControls(false);
        w.getSettings().setUseWideViewPort(true);
        w.getSettings().setDomStorageEnabled(true);

        w.setWebViewClient(new MyWebViewClient());
        w.setWebChromeClient(new WebChromeClient());
        w.clearCache(true);
        w.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                myurl = url;
                runOnUiThread((new Runnable() {
                    public void run() {
                        makeDialog();
                    }
                }));
            }
        });
        w.clearHistory();
        Intent intent = getIntent();
        String url = (String) intent.getExtras().getString("URL");
        w.loadUrl(url);
        swipeExternal.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        w.reload();
                    }
                }
        );

    }

    private void showInterstitial(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    private void makeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExternalLinkActivity.this);
        builder.setTitle("Enter File Name With Extention");
        final EditText input = new EditText(ExternalLinkActivity.this);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                s = input.getText().toString();
                isFinished = true;

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(myurl));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, s);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

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
            swipeExternal.removeView(w);
            w.clearHistory();
            w.clearCache(true);
            w.loadUrl("about:blank");
            w.onPause();
            w.pauseTimers();
            w = null;
        }
        if(swipeExternal!=null) {
            swipeExternal = null;
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if(w!=null){
            swipeExternal.removeView(w);
            w.clearHistory();
            w.clearCache(true);
            w.loadUrl("about:blank");
            w.onPause();
            w.pauseTimers();
            w = null;
        }
        if(swipeExternal!=null) {
            swipeExternal = null;
        }
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_external, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            w.reload();
            return true;
        }

        if (id == R.id.action_backward) {
            if (w.canGoBack()) {
                w.goBack();
                return true;
            } else
                finish();
            return true;
        }

        if(id==R.id.action_cross){
            finish();
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

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url)
        {
                view.loadUrl(url);
                return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(swipeExternal!=null)
                swipeExternal.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            if(swipeExternal!=null)
                swipeExternal.setRefreshing(false);
        }


    }
}

