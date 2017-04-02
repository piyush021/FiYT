package com.newgendevelopers3.fiyt;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Intent i;
    private MyViewPager mViewPager;
    private Youtube y;
    private Facebook f;
    private Twitter tw;
    private long previousClick=0;
    private Toast t;
    private TabLayout tabLayout;
    private Background mService;
    boolean mBound = false;
    public Boolean isPaused=false;
    public Boolean appIsStarting=true;
    InterstitialAd mInterstitialAd;
    SharedPreferences sp;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Background.LocalBinder binder = (Background.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube1);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
        tabLayout.getTabAt(1).select();
        if(!sp.getBoolean("FLAG",false)) {
            Toast.makeText(this,"Please Enable OverRide Mode",Toast.LENGTH_LONG).show();
        }

        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("ca-app-pub-4882008364654829/1778692792");

        AdRequest request = new AdRequest.Builder().build();//.addTestDevice("250CDAEFF9C850F528FD388ADBE42A0A").build();

        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });


        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int tabPos = tab.getPosition();
                MenuInflater inflater = getMenuInflater();
                if(tabPos==2) {
                    if(!sp.getBoolean("FLAG",false)){
                        y.wv.onPause();
                        y.wv.pauseTimers();
                    }
                    toolbar.getMenu().clear();
                    inflater.inflate(R.menu.menu_facebook,toolbar.getMenu());  //  menu for photospec.
                    if(isFullScreen){
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.VISIBLE);
                        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);

                    }
                    mViewPager.setCurrentItem(tabPos);
                    toolbar.setBackgroundColor(Color.argb(255,59,89,152));
                    tabLayout.setBackgroundColor(Color.argb(255,59,89,152));

                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook1);
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube);

                }
                else if(tabPos==1){


                    y.wv.onResume();
                    y.wv.resumeTimers();
                    y.wv.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");

                    toolbar.getMenu().clear();
                    inflater.inflate(R.menu.menu_main,toolbar.getMenu());

                    if(isFullScreen){
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.GONE);
                        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.GONE);
                    }
                    mViewPager.setCurrentItem(tabPos);
                    toolbar.setBackgroundColor(Color.argb(255,229,45,39));
                    tabLayout.setBackgroundColor(Color.argb(255,229,45,39));

                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube1);
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
                }
                else{

                    if(!sp.getBoolean("FLAG",false)){
                        y.wv.onPause();
                        y.wv.pauseTimers();
                    }
                    toolbar.getMenu().clear();
                    inflater.inflate(R.menu.menu_main,toolbar.getMenu());
                    if(isFullScreen){
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.VISIBLE);
                        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);
                    }
                    mViewPager.setCurrentItem(tabPos);
                    toolbar.setBackgroundColor(Color.argb(255,31,162,242));
                    tabLayout.setBackgroundColor(Color.argb(255,31,162,242));

                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube);
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter1);
                }
            }
        });

        i = new Intent(this, Background.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);

    }

    private void showInterstitial(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        y.wv.loadUrl("javascript:if(document.getElementsByTagName('video')[0].paused){my.paused();}else{my.resume();}");
        try{Thread.sleep(50);}catch(Exception e){}
        if(mBound && sp.getBoolean("FLAG",false)) {
            if(!isPaused){
                if(isFullScreen)
                    y.hideCustomView();
                SwipeRefreshLayout swipe = y.myswipeyoutube;
                MyWebView myWebView = y.wv;
                swipe.removeView(myWebView);
                mService.playInBackground(myWebView);
            }
        }else {
            y.wv.onPause();
            y.wv.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!appIsStarting) {
            if (mBound && sp.getBoolean("FLAG",false)) {
                if (!isPaused) {
                    SwipeRefreshLayout swipe = y.myswipeyoutube;
                    MyWebView myWebView = y.wv;
                    mService.removeFromBackground();
                    swipe.addView(myWebView);
                    myWebView.onResume();
                    myWebView.resumeTimers();
                    myWebView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");
                }
            }else{
                y.wv.onResume();
                y.wv.resumeTimers();
                y.wv.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");
            }
        }else{
            appIsStarting=false;
        }
    }


    @Override
    protected void onDestroy() {
        if (mBound) {

            mService.quit();
            unbindService(mConnection);

            mBound = false;
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        int tabPos=tabLayout.getSelectedTabPosition();
        if (id == R.id.action_exit)
        {

            finish();
            return true;
        }
        if (id == R.id.action_backward) {
            if(tabPos==2){
                if(f.wv.canGoBack())
                    f.wv.goBack();
                else Toast.makeText(this,"Can't Go Back!!",Toast.LENGTH_SHORT).show();
            }
            else if(tabPos==1){
                if(y.wv.canGoBack())
                    y.wv.goBack();
                else Toast.makeText(this,"Can't Go Back!!",Toast.LENGTH_SHORT).show();
            }
            else{
                if(tw.wv.canGoBack())
                    tw.wv.goBack();
                else Toast.makeText(this,"Can't Go Back!!",Toast.LENGTH_SHORT).show();
            }


            return true;
        }
        if (id == R.id.action_reload) {
            if(tabPos==2){
                f.wv.loadUrl(f.wv.getUrl());
            }
            else if(tabPos==1){
                y.wv.reload();
            }
            else{
                tw.wv.reload();
            }
            return true;
        }
        if (id == R.id.action_forward) {
            if(tabPos==2){
                if(f.wv.canGoForward()) f.wv.goForward();
                else Toast.makeText(this,"Can't Go Forward!!",Toast.LENGTH_SHORT).show();

            }
            else if(tabPos==1){
                if(y.wv.canGoForward()) y.wv.goForward();
                else Toast.makeText(this,"Can't Go Forward!!",Toast.LENGTH_SHORT).show();

            }
            else{
                if(tw.wv.canGoForward()) tw.wv.goForward();
                else Toast.makeText(this,"Can't Go Forward!!",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if(id==R.id.action_instagram){
            Intent i=new Intent(MainActivity.this,Instagram.class);
            startActivity(i);

        }

        if(id==R.id.action_settings){
            Intent i=new Intent(MainActivity.this,Settings.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {

        if (mBound) {
            mService.quit();
            unbindService(mConnection);
            mBound = false;
        }
        super.finish();

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position==2)
                return Facebook.newInstance();
            else if(position==1)
                return Youtube.newInstance();
            else
                return Twitter.newInstance();

        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if(position==2) {
                f = (Facebook) super.instantiateItem(container, position);
                return f;
            }
            else if(position==1){
                y=(Youtube) super.instantiateItem(container,position);
                return y;
            }
            else{
                tw=(Twitter)super.instantiateItem(container,position);
                return tw;
            }

        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }
    }



    public Boolean isFullScreen=false;

    void startFullScreen(){
        isFullScreen=true;
        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.GONE);
        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.GONE);
    }

    void stopFullScreen(){
        isFullScreen=false;
        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.VISIBLE);
        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        int tabPos = tabLayout.getSelectedTabPosition();
        if (tabPos == 1) {
            if(event.getAction()==KeyEvent.ACTION_DOWN)
            {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    long thisClick=System.currentTimeMillis();
                    if(isFullScreen){
                        y.hideCustomView();
                        return true;
                    }
                    else if(y.wv.canGoBack()){
                        y.wv.goBack();
                        return true;
                    }
                    else if((thisClick-previousClick)>2000){
                        previousClick=thisClick;
                        t=Toast.makeText(getApplicationContext(),"Press Again To Exit",Toast.LENGTH_SHORT);
                        t.show();
                        return true;
                    }
                }
            }
            if(t!=null)
                t.cancel();


        }
        if(tabPos == 2) {
            if(event.getAction()==KeyEvent.ACTION_DOWN)
            {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    long thisClick=System.currentTimeMillis();
                    if(f.wv.canGoBack()){
                        f.wv.goBack();
                        return true;
                    }
                    else if((thisClick-previousClick)>2000){
                        previousClick=thisClick;
                        t=Toast.makeText(getApplicationContext(),"Press Again To Exit",Toast.LENGTH_SHORT);
                        t.show();
                        return true;
                    }
                }
            }
            if(t!=null)
                t.cancel();
        }
        if (tabPos == 0) {
            if(event.getAction()==KeyEvent.ACTION_DOWN)
            {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    long thisClick=System.currentTimeMillis();
                    if(tw.wv.canGoBack()){
                        tw.wv.goBack();
                        return true;
                    }
                    else if((thisClick-previousClick)>2000){
                        previousClick=thisClick;
                        t=Toast.makeText(getApplicationContext(),"Press Again To Exit",Toast.LENGTH_SHORT);
                        t.show();
                        return true;
                    }
                }
            }
            if(t!=null)
                t.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }



    public void setWebChromeClientInMainActivity(Fragment curr){
        if(curr instanceof Facebook)
            f.wv.setWebChromeClient(new MyWebChromeClient());
        else if(curr instanceof Twitter)
            tw.wv.setWebChromeClient(new MyWebChromeClient());
    }


    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);


        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null){
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    // Create an image file
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

////////////////////this class is used for uploading in Other activities BAD CODE

    public class MyWebChromeClient extends WebChromeClient {

        public MyWebChromeClient(){

            super();
        }

        //For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg){
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
        }
        // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
        public void openFileChooser(ValueCallback uploadMsg, String acceptType){
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FCR);
        }
        //For Android 4.1+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FCR);
        }
        //For Android 5.0+
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams){
            if(mUMA != null){
                mUMA.onReceiveValue(null);
            }

            mUMA = filePathCallback;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCM);
                }catch(IOException ex){
                    Log.e(TAG, "Image file creation failed", ex);
                }
                if(photoFile != null){
                    mCM = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }else{
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if(takePictureIntent != null){
                intentArray = new Intent[]{takePictureIntent};
            }else{
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, FCR);
            return true;
        }


    }



}
