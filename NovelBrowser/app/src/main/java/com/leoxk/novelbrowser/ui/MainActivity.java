package com.leoxk.novelbrowser.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.R;
import com.leoxk.novelbrowser.util.RuleUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tv_main_title)
    TextView tvMainTitle;
    @Bind(R.id.pb_main)
    ProgressBar pbMain;
    @Bind(R.id.wv_main)
    WebView webview;
    @Bind(R.id.btn_read)
    ImageButton btnRead;
    @Bind(R.id.btn_main_back)
    ImageButton btnMainBack;
    @Bind(R.id.btn_main_forward)
    ImageButton btnMainForward;
    @Bind(R.id.btn_main_toolbox)
    ImageButton btnMainToolbox;
    @Bind(R.id.btn_main_home)
    ImageButton btnMainHome;
    @Bind(R.id.main_bottom)
    View bottom;

    private long exitTime;              //上次按下返回键的时间

    private Fragment mTempFragment = null;//正在显示的碎片
    private Fragment webviewFragment = null;//webview碎片
    private Fragment readingFragment = null;//阅读模式碎片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("main","on create");

        ButterKnife.bind(this);
        getSupportActionBar().hide();

        if (NovelBrowserApplication.getInstance().isFullScreen()){
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initWebView();
    }

    //初始化WebView
    private void initWebView() {
        WebSettings settings = webview.getSettings();

        settings.setJavaScriptEnabled(true);        //运行运行js
        settings.setGeolocationEnabled(true);       //运行定位
        settings.setAllowFileAccess(true);          //运行文件读写

        //允许使用缓存
        settings.setDomStorageEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAppCacheEnabled(true);

        webview.setWebChromeClient(new NovelWebChromeClient());
        webview.setWebViewClient(new NovelWebViewClient());
        webview.loadUrl(NovelBrowserApplication.getInstance().getHomePage());

        //初始化正在显示的碎片
        mTempFragment = getFragmentManager().findFragmentById(R.id.webview_fragment);
        webviewFragment = mTempFragment;

    }

    //自定义WebClient
    private class NovelWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            pbMain.setVisibility(View.VISIBLE);

//            if (RuleUtil.judge(url)){
//                webview.stopLoading();
//                Intent intent = new Intent(MainActivity.this, NovelPageActivity.class);
//                intent.putExtra("url", url);
//                MainActivity.this.startActivity(intent);
//                return;
//            }

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            pbMain.setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMainTitle.setText(webview.getTitle());
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript:function setTop(){"+
                                        "var eles = document.querySelectorAll('[src*=\"da.hxspc.com\"]');" +
                                        "for(var i=0; i<eles.length; i++){"+
                                        "eles[i].style.display = \"none\";"+
                                        "}"+
                                    "}"+
                                    "setTop();");
                }
            }, 500);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }
    }

    //自定义WebChromeClient
    private class NovelWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pbMain.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    //跳转到搜索页
    @OnClick(R.id.tv_main_title)
    void toSearchPage(){
        startActivityForResult(new Intent(this, SearchPageActivity.class), 1);
    }

    //处理搜索页的返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==12 || resultCode==21 || resultCode==23){
            String url = data.getStringExtra("URL");
            url = (url.startsWith("http://") || url.startsWith("https://"))?url:"http://"+url;
            webview.loadUrl(url);
        } else if (requestCode==1 && resultCode==13){
            String wd = data.getStringExtra("WD");
            String url = "https://m.baidu.com/s?wd="+wd;
            webview.loadUrl(url);
        } else if (requestCode==2 && resultCode==22){
            webview.clearHistory();
        }
    }

    //阅读模式
    @OnClick(R.id.btn_read)
    void read(){
        if(!RuleUtil.judge(webview.getUrl()))//如果网址没有记录在案，则返回
            return;

        if(toggleReadingButton()){
            //在阅读模式下
            readingFragment = ReadingFragment.newInstance(webview.getUrl());
            switchFragment(readingFragment);

        }else{
            switchFragment(webviewFragment);
        }
    }

    //切换碎片
    private void switchFragment(Fragment fragment) {
        if (fragment != mTempFragment) {
            if (!fragment.isAdded()) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.readmode_show, 0)
                        .hide(mTempFragment)
                        .add(R.id.main_layout, fragment)
                        .commit();
            } else {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(0, R.animator.readmode_hide)
                        .hide(mTempFragment)
                        .show(fragment)
                        .commit();
            }
            mTempFragment = fragment;
        }
    }

    //阅读模式按钮切换
    public Boolean toggleReadingButton(){
        int colorId = ((ColorDrawable) btnRead.getBackground()).getColor();

        if(colorId == -1118482){
            //正常模式转为阅读模式
            btnRead.setBackgroundColor(getResources().getColor(R.color.black));
            btnRead.setImageDrawable(getResources().getDrawable(R.drawable.read_white));
            bottom.setVisibility(View.GONE);
            return true;
        }else{
            //阅读模式转为正常模式
            btnRead.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            btnRead.setImageDrawable(getResources().getDrawable(R.drawable.read));
            bottom.setVisibility(View.VISIBLE);
            return false;
        }
    }

    //后退
    @OnClick(R.id.btn_main_back)
    void back(){
        if (webview.canGoBack()){
            webview.goBack();
        }else{
            Toast.makeText(this, "无法后退了", Toast.LENGTH_SHORT).show();
        }
    }

    //前进
    @OnClick(R.id.btn_main_forward)
    void forward(){
        if (webview.canGoForward()){
            webview.goForward();
        }else{
            Toast.makeText(this, "无法前进了", Toast.LENGTH_SHORT).show();
        }
    }

    //返回主页
    @OnClick(R.id.btn_main_home)
    void home(){
        Toast.makeText(this, "返回主页", Toast.LENGTH_SHORT).show();
        webview.loadUrl(NovelBrowserApplication.getInstance().getHomePage());
    }

    //显示工具箱
    @OnClick(R.id.btn_main_toolbox)
    void showToolbox(){
        ToolBoxPopwin toolBoxPopwin = new ToolBoxPopwin(this, null);
        toolBoxPopwin.showAtLocation(findViewById(R.id.main_bottom), Gravity.BOTTOM, 0, 0);
        toolBoxPopwin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0F;
                getWindow().setAttributes(lp);
            }
        });
    }

    //覆盖返回键为后退键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()){
            webview.goBack();
            return false;
        }else{
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return true;
    }
}