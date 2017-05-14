package com.leoxk.novelbrowser;

import android.app.Application;
import android.content.SharedPreferences;
import android.webkit.WebBackForwardList;

import com.leoxk.novelbrowser.entity.Page;
import com.leoxk.novelbrowser.entity.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leoxu on 2017/4/29.
 */
public class NovelBrowserApplication extends Application {
    private static NovelBrowserApplication instance;
    private SharedPreferences config;
    private List<Site> sites = new ArrayList<>();
    private Site curSite;
    private Page curPage;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static NovelBrowserApplication getInstance(){
        return instance;
    }

    //初始化SP
    public void init(SharedPreferences config){
        this.config = config;
    }

    //获取主页
    public String getHomePage(){
        return config.getString("homePage", "http://m.baidu.com");
    }

    //设置主页
    public void setHomPage(String newHomePage){
        SharedPreferences.Editor editor = config.edit();
        editor.putString("homePage", newHomePage).commit();
    }

    //是否全屏
    public boolean isFullScreen(){
        return config.getBoolean("isFullScreen", false);
    }

    //设置全屏
    public void toggleFullScreen(){
        boolean flag = config.getBoolean("isFullScreen", false);
        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean("isFullScreen", !flag).commit();
    }

    //初始化规则
    public void initRules(List<Site> list){
        sites.clear();
        sites.addAll(list);
    }

    //获取规则列表
    public List<Site> getRules(){
        return sites;
    }

    //设置当前规则类
    public void setCurrentSite(Site site){
        curSite = site;
    }

    //获取当前规则类
    public Site getCurrentSite(){
        return curSite;
    }

    //获取当前Page
    public Page getCurPage() {
        return curPage;
    }

    //设置当前page
    public void setCurPage(Page curPage) {
        this.curPage = curPage;
    }
}
