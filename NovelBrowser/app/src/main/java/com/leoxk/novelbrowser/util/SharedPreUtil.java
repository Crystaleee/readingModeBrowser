package com.leoxk.novelbrowser.util;

import android.content.SharedPreferences;

import com.leoxk.novelbrowser.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnCheckedChanged;

/**
 * Created by leoxu on 2017/4/29.
 */
public class SharedPreUtil {
    //获取所有搜索记录
    public static List<String> getAllSearchRecord(SharedPreferences sp, int count){
        List<String> data = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            data.add(sp.getString("record" + i, "default"));
        }
        Collections.reverse(data);
        return data;
    }

    //添加一条搜索记录
    public static void addSearchRecord(SharedPreferences sp, String record){
        SharedPreferences.Editor editor = sp.edit();
        int count = sp.getInt("count", 0);
        count++;
        editor.putString("record" + count, record);
        editor.putInt("count", count).commit();
    }

    //清空所有搜索记录
    public static void deleteAllRecords(SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        int count = sp.getInt("count", 0);
        for (int i = 1; i <= count; i++) {
            editor.remove("record" + i);
        }
        editor.putInt("count", 0);
        editor.commit();
    }

    //获取所有收藏
    public static List<Map<String,String>> getAllCollection(SharedPreferences sp){
        List<Map<String,String>> data = new ArrayList<>();
        Map<String,String> map = (Map<String, String>) sp.getAll();
        for (Map.Entry<String,String> entry:map.entrySet()){
            Map<String,String> m = new HashMap<>();
            m.put("title", entry.getValue());
            m.put("url", entry.getKey());
            data.add(m);
        }
        return data;
    }

    //清空所有收藏
    public static void deleteAllCollection(SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        Map<String,String> map = (Map<String, String>) sp.getAll();
        for (Map.Entry<String,String> entry:map.entrySet()){
            editor.remove(entry.getKey());
        }
        editor.commit();
    }
}
