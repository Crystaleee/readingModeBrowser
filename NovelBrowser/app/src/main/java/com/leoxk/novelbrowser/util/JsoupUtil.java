package com.leoxk.novelbrowser.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by leoxu on 2017/4/29.
 */

public class JsoupUtil {
    //获取Document
    public static Document getDoc(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
