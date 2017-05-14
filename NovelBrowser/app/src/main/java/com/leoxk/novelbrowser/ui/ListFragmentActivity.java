package com.leoxk.novelbrowser.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.leoxk.novelbrowser.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by leoxu on 2017/4/30.
 */
public class ListFragmentActivity extends FragmentActivity {
    @Bind(R.id.rb_list_collect)
    RadioButton rbListCollect;
    @Bind(R.id.rb_list_history)
    RadioButton rbListHistory;
    @Bind(R.id.rg_list)
    RadioGroup rgList;
    @Bind(R.id.fl_list)
    FrameLayout flList;
    private List<Map<String,String>> history;

    private List<BaseFragment> fragments = new ArrayList<>();
    private int position;
    private BaseFragment cur;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        ButterKnife.bind(this);

        history = (List<Map<String, String>>) getIntent().getSerializableExtra("history");
        
        initFragment();
        setListener();
    }

    public List<Map<String, String>> getHistory() {
        return history;
    }

    //初始化Fragment
    private void initFragment() {
        fragments.add(new CollectionFragment());
        fragments.add(new HistoryFragment());
    }

    private void setListener() {
        rgList.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rgList.check(R.id.rb_list_collect);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_list_collect:
                    position = 0;
                    break;
                case R.id.rb_list_history:
                    position = 1;
                    break;
                default:
                    position = 0;
                    break;
            }

            BaseFragment  to = getFragment();
            switchFrament(cur, to);
        }
    }

    private BaseFragment getFragment() {
        if (fragments != null){
            return fragments.get(position);
        }
        return null;
    }

    private void switchFrament(BaseFragment from, BaseFragment to) {
        if (from != to){
            cur = to;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(!to.isAdded()){
                //隐藏from，添加to
                if (from != null){
                    ft.hide(from);
                }
                if (to != null){
                    ft.add(R.id.fl_list, to).commit();
                }
            } else {
                //隐藏from,显示to
                if (from != null){
                    ft.hide(from);
                }
                if (to != null){
                    ft.show(to).commit();
                }
            }
        }
    }
}
