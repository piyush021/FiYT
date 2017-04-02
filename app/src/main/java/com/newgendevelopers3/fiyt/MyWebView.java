package com.newgendevelopers3.fiyt;


import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyWebView extends WebView{
    public Context context;
    public MyWebView(Context c){
        super(c);
        context=c;
    }
    public MyWebView(Context c, AttributeSet a){
        super(c,a);
        context=c;
    }
    public MyWebView(Context c,AttributeSet a,int i){
        super(c,a,i);
        context=c;
    }
/*
    @Override
    protected void onCreateContextMenu(final ContextMenu menu) {
        super.onCreateContextMenu(menu);
        final HitTestResult result=getHitTestResult();
        MenuItem.OnMenuItemClickListener handler=new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                    String s=result.getExtra();
                    s=s.replace("https://m.youtube","https://ssyoutube");
                    Intent i= new Intent(context,ExternalLinkActivity.class);
                    i.putExtra("URL",s);
                    context.startActivity(i);
                return true;
            }
        };

        if(result.getType()==HitTestResult.SRC_ANCHOR_TYPE){
            if(result.getExtra().startsWith("https://m.youtube.com")){
                menu.add("Save Video").setOnMenuItemClickListener(handler);
            }
        }

    }
*/


}
