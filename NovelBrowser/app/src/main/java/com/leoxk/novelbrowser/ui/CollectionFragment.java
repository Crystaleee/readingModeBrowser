package com.leoxk.novelbrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.leoxk.novelbrowser.R;
import com.leoxk.novelbrowser.util.SharedPreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by leoxu on 2017/4/30.
 */
public class CollectionFragment extends BaseFragment {
    private SharedPreferences sp;
    private List<Map<String,String>> data = new ArrayList<>();
    private ListView lvCollection;
    private Button btnDelete;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.list_collection, null);

        lvCollection = (ListView)view.findViewById(R.id.lv_collection);
        final SimpleAdapter adapter = new SimpleAdapter(mContext, data, R.layout.item_url, new String[]{"title","url"}, new int[]{R.id.tv_item_title, R.id.tv_item_url});
        lvCollection.setAdapter(adapter);

        btnDelete = (Button)view.findViewById(R.id.btn_collection_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreUtil.deleteAllCollection(sp);
                data.clear();
                Toast.makeText(mContext, "已清空收藏列表", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });

        lvCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListFragmentActivity activity = (ListFragmentActivity)mContext;
                Intent intent = new Intent();
                intent.putExtra("URL", data.get(position).get("url"));
                activity.setResult(21, intent);
                activity.finish();
            }
        });

        return view;
    }

    @Override
    protected void initData() {
        sp = mContext.getSharedPreferences("Collection", Context.MODE_PRIVATE);
        data.addAll(SharedPreUtil.getAllCollection(sp));
    }
}