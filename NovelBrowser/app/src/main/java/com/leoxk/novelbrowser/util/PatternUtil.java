package com.leoxk.novelbrowser.util;

import com.leoxk.novelbrowser.ui.MainActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leoxu on 2017/4/29.
 */
public class PatternUtil {
    private static Pattern p1, p2;

    static {
        p1 = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$");
        p2 = Pattern.compile("(%[0-9A-Z]{2})+");
    }

    //判断是否是URL
    public static boolean isUrl(String str){
        str = (str.startsWith("http://") || str.startsWith("https://"))?str:"http://"+str;
        return p1.matcher(str).matches();
    }

    //解码标题
    public static String decodeTitle(String title){
        Matcher matcher = p2.matcher(title);

        if (matcher.find()){
            int start = matcher.start();
            int end = matcher.end();

            String str = matcher.group();
            String replace = null;
            try {
                replace = URLDecoder.decode(str, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return title.substring(0, start) + replace + title.substring(end);
        } else {
            return title;
        }
    }

    //小说章节url判断
    public static boolean endMatchPattern(String pattern, String end){
        Pattern p = Pattern.compile(pattern);
        return p.matcher(end).matches();
    }
}
