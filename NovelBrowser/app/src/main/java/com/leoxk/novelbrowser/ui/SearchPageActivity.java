package com.leoxk.novelbrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.R;
import com.leoxk.novelbrowser.util.PatternUtil;
import com.leoxk.novelbrowser.util.SharedPreUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class SearchPageActivity extends AppCompatActivity {
    @Bind(R.id.tv_search_action)
    TextView tvSearchAction;
    @Bind(R.id.et_search_text)
    EditText etSearchText;
    @Bind(R.id.btn_search_delete)
    Button btnSearchDelete;
    @Bind(R.id.lv_search)
    ListView lvSearch;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //是否全屏显示
        if (NovelBrowserApplication.getInstance().isFullScreen()){
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_search_page);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        initUI();
    }

    //初始化UI
    private void initUI() {
        sp = getSharedPreferences("SearchRecord", Context.MODE_PRIVATE);

        int count = sp.getInt("count", 0);
        if (count == 0) {
            btnSearchDelete.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SharedPreUtil.getAllSearchRecord(sp, count));
            lvSearch.setAdapter(adapter);
        }
    }

    //搜索按钮的点击事件
    @OnClick(R.id.tv_search_action)
    void action() {
        if (tvSearchAction.getText().equals("取消")) {                //结果码 11 表示 直接返回
            setResult(11);
        } else if (tvSearchAction.getText().equals("进入")) {        //结果码 12 表示 进入网址
            Intent intent = new Intent();
            intent.putExtra("URL", etSearchText.getText().toString());
            setResult(12, intent);
        } else if (tvSearchAction.getText().equals("搜索")) {        //结果码 13 表示 关键词搜索
            String wd = etSearchText.getText().toString();

            //添加进搜索记录
            SharedPreUtil.addSearchRecord(sp, wd);

            //设置返回结果
            Intent intent = new Intent();
            intent.putExtra("WD", wd);
            setResult(13, intent);
        }
        finish();
    }

    //搜索框的文本改变事件
    @OnTextChanged(R.id.et_search_text)
    void onTextChange() {
        String text = etSearchText.getText().toString();
        if (text.length() == 0) {
            tvSearchAction.setText(R.string.actionCancel);
        } else if (PatternUtil.isUrl(text)) {
            tvSearchAction.setText(R.string.actionEnter);
        } else {
            tvSearchAction.setText(R.string.actionSearch);
        }
    }

    //清空搜索记录
    @OnClick(R.id.btn_search_delete)
    void deleteAll() {
        SharedPreUtil.deleteAllRecords(sp);
        Toast.makeText(this, "搜索记录已清空", Toast.LENGTH_SHORT).show();
        btnSearchDelete.setVisibility(View.GONE);
        lvSearch.setVisibility(View.GONE);
    }

    //ListView的点击监听
    @OnItemClick(R.id.lv_search)
    void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent intent = new Intent();
        intent.putExtra("WD", ((TextView)view).getText().toString());
        setResult(13, intent);
        finish();
    }
}
