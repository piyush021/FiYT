package com.newgendevelopers3.fiyt;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class MyViewPager extends ViewPager {

    private Boolean enabled=true;
    int totalWidth=0;
    public MyViewPager(Context context){
        super(context);
    }
    public MyViewPager(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

   @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
       totalWidth= Resources.getSystem().getDisplayMetrics().widthPixels;
       int first=(int)totalWidth/4;
       int second=first*3;
       if(x<=first||second<=x){
           return false;
       }
       else
           return true;
    }
}
