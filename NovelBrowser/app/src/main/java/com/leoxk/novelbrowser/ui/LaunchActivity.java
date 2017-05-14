package com.leoxk.novelbrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.WindowManager;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.R;
import com.leoxk.novelbrowser.entity.Site;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Log.d("launch","on create");

        fullScreen();                                      //全屏显示
        initConfig();                                      //初始化配置

        new Handler().postDelayed(new Runnable() {         //延时启动MainActivity
            @Override
            public void run() {
                startMainActivity();
            }
        }, 3000);

        new Thread(){                                       //初始化规则
            @Override
            public void run() {
                try {
                    initRules();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //全屏显示
    private void fullScreen() {
        getSupportActionBar().hide();

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    //延时启动 MainActivity
    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //初始化配置
    private void initConfig() {
        SharedPreferences sp1 = getSharedPreferences("Config", MODE_PRIVATE);
        if (sp1.getAll().size() == 0){
            SharedPreferences.Editor editor = sp1.edit();
            editor.putString("homePage", "http://m.baidu.com/");
            editor.putBoolean("isFullScreen", false);
            editor.commit();
        }

        NovelBrowserApplication.getInstance().init(sp1);

        SharedPreferences sp2 = getSharedPreferences("SearchRecord", Context.MODE_PRIVATE);
        if (sp2.getAll().size() == 0){
            SharedPreferences.Editor editor = sp2.edit();
            editor.putInt("count", 0).commit();
        }
    }

    //初始化规则
    private void initRules() throws XmlPullParserException, IOException {
        InputStream is = getAssets().open("rules.xml");
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();
        List<Site> sites = new ArrayList<>();
        Site site = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    sites = new ArrayList<Site>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("site")) {
                        site = new Site();
                    } else if (parser.getName().equals("host")) {
                        site.setHost(parser.nextText());
                    } else if (parser.getName().equals("start")) {
                        site.setStart(parser.nextText());
                    } else if (parser.getName().equals("pattern")) {
                        site.setPattern(parser.nextText());
                    } else if (parser.getName().equals("title")) {
                        site.setTitle(parser.nextText());
                    } else if (parser.getName().equals("text")) {
                        site.setText(parser.nextText());
                    } else if (parser.getName().equals("prev")) {
                        site.setPrev(parser.nextText());
                    } else if (parser.getName().equals("next")) {
                        site.setNext(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("site")) {
                        sites.add(site);
                        site = null;
                    }
                    break;
            }
            eventType = parser.next();
        }

        NovelBrowserApplication.getInstance().initRules(sites);
        Log.i("RULES", sites.toString());
    }
}
