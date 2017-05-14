package com.leoxk.novelbrowser.entity;

import org.jsoup.nodes.Document;

/**
 * Created by leo on 2017/5/1.
 */

public class Page {
    private String url;
    private String title;
    private String text;
    private Document doc;

    public Page(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public void setText(String text) {
        this.text = text;
    }
}
