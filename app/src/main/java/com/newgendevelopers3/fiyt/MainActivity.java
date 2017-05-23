package com.newgendevelopers3.fiyt;

import android.annotation.SuppressLint;
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
import com.google.android.gms.ads.InterstitialAd;

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
    private long previousClick = 0;
    private Toast t;
    private TabLayout tabLayout;
    private Background mService;
    boolean mBound = false;
    //getting a service connection in variable mConnection
    //this variable will be used later
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Background.LocalBinder binder = (Background.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

    };
    public Boolean isPaused = false;
    public Boolean appIsStarting = true;
    InterstitialAd mInterstitialAd;
    private SharedPreferences sp;
    public Boolean isFullScreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //keep all fragments in memory
        mViewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //set icons on tabs
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube1);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
        tabLayout.getTabAt(1).select();
        //prompt user to enable override mode
        //override mode will run youtube in background
        if (!sp.getBoolean("FLAG", false)) {
            Toast.makeText(this, "Please Enable OverRide Mode", Toast.LENGTH_LONG).show();
        }
        //displaying ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4882008364654829/1778692792");
        AdRequest request = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
        //handling change of tabs when tab icon is clicked
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tabPos = tab.getPosition();
                MenuInflater inflater = getMenuInflater();
                if (tabPos == 0) {
                    //if override mode is not on, pause youtube
                    if (!sp.getBoolean("FLAG", false)) {
                        y.wv.onPause();
                        y.wv.pauseTimers();
                    }
                    //clearing menu, we'll fill it again in a moment
                    toolbar.getMenu().clear();
                    inflater.inflate(R.menu.menu_main, toolbar.getMenu());
                    //un hide tab and toolbar if hidden because of full screen video in youtube
                    if (isFullScreen) {
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.VISIBLE);
                        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);
                    }
                    mViewPager.setCurrentItem(tabPos);
                    //setting colors of tab and toolbar to twitter color
                    toolbar.setBackgroundColor(Color.argb(255, 31, 162, 242));
                    tabLayout.setBackgroundColor(Color.argb(255, 31, 162, 242));
                    //changing youtube icon to selected icon and remaining icon to unselected
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube);
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter1);

                } else if (tabPos == 1) {
                    //youtube
                    y.wv.onResume();
                    y.wv.resumeTimers();
                    y.wv.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");
                    toolbar.getMenu().clear();
                    inflater.inflate(R.menu.menu_main, toolbar.getMenu());
                    //hide toolbar and tab if youtube video is in full screen
                    if (isFullScreen) {
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.GONE);
                        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.GONE);
                    }
                    mViewPager.setCurrentItem(tabPos);
                    toolbar.setBackgroundColor(Color.argb(255, 229, 45, 39));
                    tabLayout.setBackgroundColor(Color.argb(255, 229, 45, 39));
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube1);
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
                } else {
                    //facebook
                    if (!sp.getBoolean("FLAG", false)) {
                        y.wv.onPause();
                        y.wv.pauseTimers();
                    }
                    toolbar.getMenu().clear();
                    inflater.inflate(R.menu.menu_facebook, toolbar.getMenu());  //  menu for photospec.
                    if (isFullScreen) {
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.VISIBLE);
                        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);
                    }
                    mViewPager.setCurrentItem(tabPos);
                    toolbar.setBackgroundColor(Color.argb(255, 59, 89, 152));
                    tabLayout.setBackgroundColor(Color.argb(255, 59, 89, 152));
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_facebook1);
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_twitter);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_youtube);

                }
            }
        });
        //binding foreground service to mConnection variable so
        //we can use it later to control send messages to service
        //bindService will start the service as well
        i = new Intent(this, Background.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);

    }//end of onCreate

    //INNER CLASS
    //all this class do is return fragment to a given page number
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //this method gets called only when the fragment does not exist
        //it will be called at the start only in our case because we are keeping all fragments in memory
        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return tw = Twitter.newInstance();
                //return Twitter.newInstance();
            else if (position == 1)
                return y = Youtube.newInstance();
                //return Youtube.newInstance();
            else
                return f = Facebook.newInstance();
            //return Facebook.newInstance();
        }

        /*
                //use of this code????
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
        */
        @Override
        public int getCount() {
            //return total number of fragments
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //setting the page title to empty because we have used icons instead of title
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

    //this function shows ads if loaded
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        y.wv.loadUrl("javascript:if(document.getElementsByTagName('video')[0].paused){my.paused();}else{my.resume();}");
        try {
            Thread.sleep(50);
        } catch (Exception e) {
        }
        if (mBound && sp.getBoolean("FLAG", false)) {
            if (!isPaused) {
                if (isFullScreen)
                    y.hideCustomView();
                SwipeRefreshLayout swipe = y.myswipeyoutube;
                MyWebView myWebView = y.wv;
                swipe.removeView(myWebView);
                mService.playInBackground(myWebView);
            }
        } else {
            y.wv.onPause();
            y.wv.pauseTimers();
        }
    }
//////////////////////////////////////////////////////////////////////////////handle later
    @Override
    protected void onResume() {
        super.onResume();
        if (!appIsStarting) {
            if (mBound && sp.getBoolean("FLAG", false)) {
                if (!isPaused) {
                    SwipeRefreshLayout swipe = y.myswipeyoutube;
                    MyWebView myWebView = y.wv;
                    mService.removeFromBackground();
                    swipe.addView(myWebView);
                    myWebView.onResume();
                    myWebView.resumeTimers();
                    myWebView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");
                }
            } else {
                y.wv.onResume();
                y.wv.resumeTimers();
                y.wv.loadUrl("javascript:document.getElementsByTagName('video')[0].play();");
            }
        } else {
            appIsStarting = false;
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

    //inflating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //handle clicks on menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int tabPos = tabLayout.getSelectedTabPosition();
        //handle exit icon on menu
        if (id == R.id.action_exit) {
            finish();
            return true;
        }
        //handle back icon on menu
        if (id == R.id.action_backward) {
            if (tabPos == 0) {
                if (tw.wv.canGoBack())
                    tw.wv.goBack();
                else Toast.makeText(this, "Can't Go Back!!", Toast.LENGTH_SHORT).show();

            } else if (tabPos == 1) {
                if (y.wv.canGoBack())
                    y.wv.goBack();
                else Toast.makeText(this, "Can't Go Back!!", Toast.LENGTH_SHORT).show();
            } else {
                if (f.wv.canGoBack())
                    f.wv.goBack();
                else Toast.makeText(this, "Can't Go Back!!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        //handle reload icon on menu
        if (id == R.id.action_reload) {
            if (tabPos == 0) {
                tw.wv.reload();
            } else if (tabPos == 1) {
                y.wv.reload();
            } else {
                f.wv.reload();
            }
            return true;
        }
        //handle forward icon on menu
        if (id == R.id.action_forward) {
            if (tabPos == 0) {
                if (tw.wv.canGoForward()) tw.wv.goForward();
                else Toast.makeText(this, "Can't Go Forward!!", Toast.LENGTH_SHORT).show();
            } else if (tabPos == 1) {
                if (y.wv.canGoForward()) y.wv.goForward();
                else Toast.makeText(this, "Can't Go Forward!!", Toast.LENGTH_SHORT).show();
            } else {
                if (f.wv.canGoForward()) f.wv.goForward();
                else Toast.makeText(this, "Can't Go Forward!!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        //starting instagram
        if (id == R.id.action_instagram) {
            Intent i = new Intent(MainActivity.this, Instagram.class);
            startActivity(i);
        }
        //opening settings
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, Settings.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {
        //stop service if running
        if (mBound) {
            mService.quit();
            unbindService(mConnection);
            mBound = false;
        }
        super.finish();
    }

    void startFullScreen() {
        isFullScreen = true;
        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.GONE);
        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.GONE);
    }

    void stopFullScreen() {
        isFullScreen = false;
        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.VISIBLE);
        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        int tabPos = tabLayout.getSelectedTabPosition();
        if (tabPos == 1) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    long thisClick = System.currentTimeMillis();
                    if (isFullScreen) {
                        y.hideCustomView();
                        return true;
                    } else if (y.wv.canGoBack()) {
                        y.wv.goBack();
                        return true;
                    } else if ((thisClick - previousClick) > 2000) {
                        previousClick = thisClick;
                        t = Toast.makeText(getApplicationContext(), "Press Again To Exit", Toast.LENGTH_SHORT);
                        t.show();
                        return true;
                    }
                }
            }
            if (t != null)
                t.cancel();
        }
        if (tabPos == 2) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    long thisClick = System.currentTimeMillis();
                    if (f.wv.canGoBack()) {
                        f.wv.goBack();
                        return true;
                    } else if ((thisClick - previousClick) > 2000) {
                        previousClick = thisClick;
                        t = Toast.makeText(getApplicationContext(), "Press Again To Exit", Toast.LENGTH_SHORT);
                        t.show();
                        return true;
                    }
                }
            }
            if (t != null)
                t.cancel();
        }
        if (tabPos == 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    long thisClick = System.currentTimeMillis();
                    if (tw.wv.canGoBack()) {
                        tw.wv.goBack();
                        return true;
                    } else if ((thisClick - previousClick) > 2000) {
                        previousClick = thisClick;
                        t = Toast.makeText(getApplicationContext(), "Press Again To Exit", Toast.LENGTH_SHORT);
                        t.show();
                        return true;
                    }
                }
            }
            if (t != null)
                t.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    //need to override this so fragments can upload file
    //the activity result will automatically be transferred to required fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}//end of activity
