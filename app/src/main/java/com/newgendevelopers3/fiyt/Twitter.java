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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Twitter extends Fragment {

    public WebView wv;
    SwipeRefreshLayout myswipetwitter;

    public Twitter() {
    }
    public static Twitter newInstance() {
        Twitter fragment = new Twitter();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_twitter, container, false);
        wv=(WebView)view.findViewById(R.id.twitterWebView);
        myswipetwitter=(SwipeRefreshLayout)view.findViewById(R.id.swipeToRefresh2);

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setDomStorageEnabled(true);

        wv.setWebViewClient(new MyWebViewClient());
        //wv.setWebChromeClient(new WebChromeClient());

        MainActivity m=(MainActivity)getActivity();
        m.setWebChromeClientInMainActivity(this);

        wv.clearCache(true);
        wv.clearHistory();
        Intent i=getActivity().getIntent();
        if(i.getData()!=null){
            Uri u=i.getData();
            String s=u.getScheme();
            s+=":";
            s+=u.getEncodedSchemeSpecificPart();

            if(s.startsWith("https://mobile.twitter.com")||s.startsWith("https://www.twitter.com")||s.startsWith("https://www.twitter.com"))
                wv.loadUrl(s);
            else wv.loadUrl("https://mobile.twitter.com");
        }
        else wv.loadUrl("https://mobile.twitter.com");

        setRetainInstance(true);

        myswipetwitter.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        wv.loadUrl(wv.getUrl());
                    }
                }
        );

        return view;
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(url.startsWith("https://mobile.twitter.com")||url.startsWith("https://www.twitter.com")) {
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
            myswipetwitter.setRefreshing(true);
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {
            // TODO: Implement this method
            super.onPageFinished(view, url);
            myswipetwitter.setRefreshing(false);
        }

    }

}
