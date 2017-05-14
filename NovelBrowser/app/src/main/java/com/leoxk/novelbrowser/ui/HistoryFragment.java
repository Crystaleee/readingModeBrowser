package com.leoxk.novelbrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.leoxk.novelbrowser.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by leoxu on 2017/4/30.
 */

public class HistoryFragment extends BaseFragment {
    private SharedPreferences sp;
    private List<Map<String,String>> data = new ArrayList<>();
    private ListView lvHistory;
    private Button btnDelete;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.list_history, null);

        lvHistory = (ListView)view.findViewById(R.id.lv_history);
        final SimpleAdapter adapter = new SimpleAdapter(mContext, data, R.layout.item_url, new String[]{"title","url"}, new int[]{R.id.tv_item_title,R.id.tv_item_url});
        lvHistory.setAdapter(adapter);

        btnDelete = (Button)view.findViewById(R.id.btn_history_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragmentActivity activity = (ListFragmentActivity)mContext;
                activity.setResult(22);
                data.clear();
                Toast.makeText(mContext, "已清空浏览历史", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                activity.finish();
            }
        });

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListFragmentActivity activity = (ListFragmentActivity)mContext;
                Intent intent = new Intent();
                intent.putExtra("URL", data.get(position).get("url"));
                activity.setResult(23, intent);
                activity.finish();
            }
        });

        return view;
    }

    @Override
    protected void initData() {
        ListFragmentActivity activity = (ListFragmentActivity)mContext;
        data.addAll(activity.getHistory());
    }
}
