package com.leoxk.novelbrowser.util;

import android.util.Log;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.entity.Site;

import java.util.List;

/**
 * Created by leo on 2017/5/1.
 */

public class RuleUtil {
    //判断url是否符合拦截规则
    public static boolean judge(String url){
        Log.i("RULE", url);

        List<Site> sites = NovelBrowserApplication.getInstance().getRules();

        for (Site site:sites){
            Log.i("RULE","当前站点:"+site.getHost());

            if (url.contains(site.getHost()) && url.startsWith(site.getStart())){
                Log.i("RULE","通过主机和前缀测试");
                String end = url.substring(site.getStart().length());
                Log.i("RULE",end);
//                if (PatternUtil.endMatchPattern(site.getPattern(), end)){
                NovelBrowserApplication.getInstance().setCurrentSite(site);
                return true;
//                }
            }
        }

        return false;
    }
}
