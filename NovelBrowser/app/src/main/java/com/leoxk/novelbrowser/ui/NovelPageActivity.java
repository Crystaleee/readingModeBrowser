package com.leoxk.novelbrowser.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.R;
import com.leoxk.novelbrowser.entity.Page;
import com.leoxk.novelbrowser.entity.Site;
import com.leoxk.novelbrowser.util.JsoupUtil;

import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NovelPageActivity extends AppCompatActivity {

    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;
    private double nLenStart = 0;
    private double nLenEnd = 0;

    @Bind(R.id.tv_page_title)
    TextView tvPageTitle;
    @Bind(R.id.tv_page_text)
    TextView tvPageText;
    @Bind(R.id.layout_page)
    ScrollView layoutPage;
//    @Bind(R.id.)

    private Page prev, cur, next;
    private Site site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_page);
        ButterKnife.bind(this);

        Log.d("novel","on create");

        getSupportActionBar().hide();

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        site = NovelBrowserApplication.getInstance().getCurrentSite();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("novel","on resume");
        new Thread(){
            @Override
            public void run() {
                initCurPage();
            }
        }.start();
    }

    //初始化当前页面
    private void initCurPage() {
        String url = getIntent().getStringExtra("url");
        cur = new Page(url);
        initPage(cur);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvPageTitle.setText(cur.getTitle());
                tvPageText.setText(cur.getText());
            }
        });

        initPrev();
        initNext();
    }

    //初始化页面数据
    private void initPage(Page page) {
        Document doc = page.getDoc()==null?JsoupUtil.getDoc(page.getUrl()):page.getDoc();

        String title = doc.getElementById(site.getTitle()).text();
        String text = doc.getElementById(site.getText()).html().replace("<br><br>","\n").replace("<br>", "").replace("&nbsp;", "  ");

        page.setTitle(title);
        page.setText(text);
        page.setDoc(doc);
    }

    //缓存上一章
    private void initPrev() {
        while (cur.getDoc() == null){
            cur.setDoc(JsoupUtil.getDoc(cur.getUrl()));
        }

        String prevUrl = cur.getDoc().getElementById(site.getPrev()).attr("href");

        if ("index.html".equals(prevUrl) || "./".equals(prevUrl)){
            prev = null;
            return;
        }

        String curUrl = cur.getUrl();
        String url = curUrl.substring(0, curUrl.lastIndexOf('/')+1)+prevUrl;
        prev = new Page(url);
        initPage(prev);
    }

    //缓存下一章
    private void initNext() {
        while (cur.getDoc() == null){
            cur.setDoc(JsoupUtil.getDoc(cur.getUrl()));
        }

        String nextUrl = cur.getDoc().getElementById(site.getNext()).attr("href");

        if ("index.html".equals(nextUrl) || "./".equals(nextUrl)){
            next = null;
            return;
        }

        String curUrl = cur.getUrl();
        String url = curUrl.substring(0, curUrl.lastIndexOf('/')+1)+nextUrl;
        next = new Page(url);
        initPage(next);
    }

    //监听左右滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int pointerCnt = event.getPointerCount();
        int action = event.getAction();

//        //继承了Activity的onTouchEvent方法，直接监听点击事件
//        if(action == MotionEvent.ACTION_DOWN) {
//            //当手指按下的时候
//            x1 = event.getX();
//            y1 = event.getY();
//        }
//        else if(action == MotionEvent.ACTION_UP) {
//            //当手指离开的时候
//            x2 = event.getX();
//            y2 = event.getY();
//
//            if (x1 - x2 > 300) {
//                if (next != null){
//                    Toast.makeText(this, "下一章", Toast.LENGTH_SHORT).show();
//                    prev = cur;
//                    cur = next;
//                    updateUI();
//                    new Thread(){
//                        @Override
//                        public void run() {
//                            initNext();
//                        }
//                    }.start();
//                } else {
//                    Toast.makeText(this, "等更新吧...", Toast.LENGTH_SHORT).show();
//                }
//            } else if (x2 - x1 > 300) {
//                if (prev != null){
//                    Toast.makeText(this, "上一章", Toast.LENGTH_SHORT).show();
//                    next = cur;
//                    cur = prev;
//                    updateUI();
//                    new Thread(){
//                        @Override
//                        public void run() {
//                            initPrev();
//                        }
//                    }.start();
//                } else {
//                    Toast.makeText(this, "这是第一章，前面没有了...", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } else
if((action&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == pointerCnt){
            //当两个手指放下来时
            int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
            int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));

            nLenStart = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
        }
        else if((action&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP && 2 == pointerCnt){
            //当两个手指离开屏幕时
            int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
            int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));

            nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);

            //通过两个手指开始距离和结束距离，来判断放大缩小
            if(nLenEnd > nLenStart){
                Toast.makeText(getApplicationContext(), "放大", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "缩小", Toast.LENGTH_SHORT).show();
            }

            double scale = nLenEnd/nLenStart;
            tvPageText.setTextSize(tvPageText.getTextSize()* (float)scale);
            tvPageTitle.setTextSize(tvPageTitle.getTextSize()* (float)scale);

        }
        return super.dispatchTouchEvent(event);
    }


    //更新页面内容
    private void updateUI() {
        tvPageTitle.setText(cur.getTitle());
        tvPageText.setText(cur.getText());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutPage.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 500);
    }
}
