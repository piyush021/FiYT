package com.newgendevelopers3.fiyt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Facebook extends Fragment {
    public WebView wv;
    SwipeRefreshLayout myswipefacebook;
    public Facebook() {

    }

    public static Facebook newInstance() {
        Facebook fragment = new Facebook();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        wv = (WebView) view.findViewById(R.id.facebookWebView);
        myswipefacebook = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh1);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setDomStorageEnabled(true);

        wv.setWebViewClient(new MyWebViewClient());
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
            if(s.startsWith("https://m.facebook.com")||s.startsWith("https://www.facebook.com")||s.startsWith("https://facebook.com"))
                wv.loadUrl(s);
            else wv.loadUrl("https://m.facebook.com");
        }
        else wv.loadUrl("https://m.facebook.com");

        setRetainInstance(true);

        myswipefacebook.setOnRefreshListener(
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
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

            if (url.startsWith("https://m.facebook.com") || url.startsWith("https://www.facebook.com")||url.startsWith("https://facebook.com")) {
                view.loadUrl(url);
                return true;
            } else if (url.startsWith("https://scontent")) {
                Intent i = new Intent(getActivity(), ImageViewer.class);
                i.putExtra("URL", url);
                startActivity(i);
                return true;
            } else {
                Intent i = new Intent(getActivity(), ExternalLinkActivity.class);
                i.putExtra("URL", url);
                startActivity(i);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            myswipefacebook.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO: Implement this method
            super.onPageFinished(view, url);
            myswipefacebook.setRefreshing(false);
        }


    }

}