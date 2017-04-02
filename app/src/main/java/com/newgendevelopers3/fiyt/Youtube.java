package com.newgendevelopers3.fiyt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class Youtube extends Fragment {

    public MyWebView wv;
    SwipeRefreshLayout myswipeyoutube;

    WebChromeClient.CustomViewCallback customViewCallback;
    View mCustomView;
    MyWebChromeClient mWebChromeClient;
    FrameLayout customViewContainer;

    public Youtube() {
    }


    public static Youtube newInstance() {
        Youtube fragment = new Youtube();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_youtube, container, false);
        customViewContainer=(FrameLayout)view.findViewById(R.id.customViewContainer);
        wv = (MyWebView) view.findViewById(R.id.youtubeWebView);
        myswipeyoutube = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        this.unregisterForContextMenu(wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setDomStorageEnabled(true);

        wv.setWebViewClient(new MyWebViewClient());
        mWebChromeClient=new MyWebChromeClient();
        wv.setWebChromeClient(mWebChromeClient);
        wv.addJavascriptInterface(new MyJavaScriptInterface(),"my");
        wv.clearCache(true);
        wv.clearHistory();
        Intent i=getActivity().getIntent();

        if(i.getData()!=null){
            Uri u=i.getData();
            String s=u.getScheme();
            s+=":";
            s+=u.getEncodedSchemeSpecificPart();
            if(s.startsWith("https://m.youtube.com")||s.startsWith("https://www.youtube.com")||s.startsWith("https://youtu.be")||s.startsWith("https://youtube.com"))
                wv.loadUrl(s);
            else wv.loadUrl("https://m.youtube.com/");
        }
        else wv.loadUrl("https://m.youtube.com/");
        setRetainInstance(true);

        myswipeyoutube.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        wv.loadUrl(wv.getUrl());
                    }
                }
        );

        wv.reload();

        return view;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if(url.startsWith("https://m.youtube.com")||url.startsWith("https://youtu.be")||url.startsWith("https://www.youtube.com")) {

                view.loadUrl(url);
                return true;
            }

            else{
                Intent i=new Intent(getActivity(),ExternalLinkActivity.class);
                i.putExtra("URL",url);
                startActivity(i);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            myswipeyoutube.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            // TODO: Implement this method
            super.onPageFinished(view, url);
            myswipeyoutube.setRefreshing(false);
        }

    }

    public Boolean inCustomView(){
        return mCustomView!=null;
    }

    public void hideCustomView(){
        mWebChromeClient.onHideCustomView();
    }

    public class MyWebChromeClient extends WebChromeClient {

        public MyWebChromeClient(){
            super();
        }
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if(mCustomView!=null){
                callback.onCustomViewHidden();
                return;
            }
            mCustomView=view;
            wv.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback=callback;
            ((MainActivity)getActivity()).startFullScreen();
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView==null)
            {
                return;
            }
            wv.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);
            mCustomView.setVisibility(View.GONE);
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();
            mCustomView=null;
            ((MainActivity)getActivity()).stopFullScreen();
        }
    }

    public class MyJavaScriptInterface{
        @JavascriptInterface
        public void paused(){
            ((MainActivity)getActivity()).isPaused=true;
        }

        @JavascriptInterface
        public void resume(){
            ((MainActivity)getActivity()).isPaused=false;
        }
    }
}