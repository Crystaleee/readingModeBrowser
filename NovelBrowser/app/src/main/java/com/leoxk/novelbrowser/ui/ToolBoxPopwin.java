package com.leoxk.novelbrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by leoxu on 2017/4/29.
 */
public class ToolBoxPopwin extends PopupWindow implements View.OnTouchListener {
    private MainActivity context;
    private View view;

    @Bind(R.id.btn_tool_down)
    ImageButton btnToolDown;

    @Bind(R.id.btn_tool_collect)
    Button btnCollect;
    @Bind(R.id.btn_tool_list)
    Button btnList;
    @Bind(R.id.btn_tool_screen)
    Button btnScreen;
    @Bind(R.id.btn_tool_nopic)
    Button btnNopic;

    @Bind(R.id.btn_tool_home)
    Button btnHome;
    @Bind(R.id.btn_tool_share)
    Button btnShare;
    @Bind(R.id.btn_tool_reload)
    Button btnReload;
    @Bind(R.id.btn_tool_exit)
    Button btnExit;

    public ToolBoxPopwin(Context mContext, View.OnClickListener itemsOnClick){
        this.context = (MainActivity) mContext;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.toolbox, null);
        ButterKnife.bind(this, view);

        if (NovelBrowserApplication.getInstance().isFullScreen()){
            btnScreen.setText(R.string.cancelScreen);
        } else {
            btnScreen.setText(R.string.screen);
        }

        // 设置外部可点击
        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(this);

        this.setContentView(this.view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        WindowManager.LayoutParams lp = ((AppCompatActivity)mContext).getWindow().getAttributes();
        lp.alpha = 0.5F;
        ((AppCompatActivity)mContext).getWindow().setAttributes(lp);

        this.setAnimationStyle(R.style.toolbox_anim);
    }

    //设置外部可点击
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = view.findViewById(R.id.layout_toolbox).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }

    //退出工具栏
    @OnClick(R.id.btn_tool_down)
    void hideToolbox(){
        dismiss();
    }

    //功能——添加收藏
    @OnClick(R.id.btn_tool_collect)
    void addCollection(){
        SharedPreferences sp = context.getSharedPreferences("Collection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.webview.getUrl(), context.tvMainTitle.getText().toString()).commit();
        Toast.makeText(context, "已添加", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    //跳转到收藏|历史Activity
    @OnClick(R.id.btn_tool_list)
    void showList(){
        dismiss();
        Intent intent = new Intent(context, ListFragmentActivity.class);

        WebBackForwardList webBackForwardList = context.webview.copyBackForwardList();
        int count = webBackForwardList.getSize();
        List<Map<String,String>> list = new ArrayList<>();
        for (int i=count-1; i>=0; i--){
            WebHistoryItem itemAtIndex = webBackForwardList.getItemAtIndex(i);
            Map<String,String> map = new HashMap<>();
            map.put("title", itemAtIndex.getTitle());
            map.put("url", itemAtIndex.getUrl());
            list.add(map);
        }
        intent.putExtra("history", (Serializable) list);

        context.startActivityForResult(intent, 2);
    }

    //功能——全屏
    @OnClick(R.id.btn_tool_screen)
    void screen(){
        dismiss();
        if (NovelBrowserApplication.getInstance().isFullScreen()){
            WindowManager.LayoutParams attr = context.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context.getWindow().setAttributes(attr);
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            context.getWindow().setAttributes(lp);
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        NovelBrowserApplication.getInstance().toggleFullScreen();
    }

    //功能——无图模式
    @OnClick(R.id.btn_tool_nopic)
    void noPic(){
        WebSettings settings = context.webview.getSettings();
        if (settings.getLoadsImagesAutomatically()){
            settings.setLoadsImagesAutomatically(false);
            Toast.makeText(context, "开启无图模式", Toast.LENGTH_SHORT).show();
        } else {
            settings.setLoadsImagesAutomatically(true);
            Toast.makeText(context, "关闭无图模式", Toast.LENGTH_SHORT).show();
        }
        context.webview.reload();
        dismiss();
    }

    //功能——设为主页
    @OnClick(R.id.btn_tool_home)
    void setHomePage(){
        NovelBrowserApplication.getInstance().setHomPage(context.webview.getUrl());
        dismiss();
    }

    //功能——分享
    @OnClick(R.id.btn_tool_share)
    void share(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, context.webview.getUrl());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getTitle()));
        dismiss();
    }

    //功能——刷新
    @OnClick(R.id.btn_tool_reload)
    void refresh(){
        dismiss();
        context.webview.reload();
    }

    //功能——退出
    @OnClick(R.id.btn_tool_exit)
    void exit(){
        dismiss();
        context.finish();
    }
}
