package com.leoxk.novelbrowser.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by leo on 2017/5/1.
 */

public class Site {
    private Map<String,String> info = new HashMap<>();
    private Map<String,String> page = new HashMap<>();

    public Site() {
    }

    public String getHost(){
        return info.get("host");
    }

    public String getStart(){
        return info.get("start");
    }

    public String getPattern(){
        return info.get("pattern");
    }

    public String getTitle(){
        return page.get("title");
    }

    public String getText(){
        return page.get("text");
    }

    public String getPrev(){
        return page.get("prev");
    }

    public String getNext(){
        return page.get("next");
    }

    @Override
    public String toString() {
        return "Site{" +
                    "host:"+getHost()+", "+
                    "start:"+getStart()+", "+
                    "pattern:"+getPattern()+", "+
                    "title:"+getTitle()+", "+
                    "text:"+getText()+", "+
                    "prev:"+getPrev()+", "+
                    "next:"+getNext()+
                "}";
    }

    public void setHost(String host){
        info.put("host", host);
    }

    public void setStart(String start){
        info.put("start", start);
    }

    public void setPattern(String pattern){
        info.put("pattern", pattern);
    }

    public void setTitle(String title){
        page.put("title", title);
    }

    public void setText(String text){
        page.put("text", text);
    }

    public void setPrev(String prev){
        page.put("prev", prev);
    }

    public void setNext(String next){
        page.put("next", next);
    }
}
