package com.leoxk.novelbrowser.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.leoxk.novelbrowser.NovelBrowserApplication;
import com.leoxk.novelbrowser.R;
import com.leoxk.novelbrowser.entity.Page;
import com.leoxk.novelbrowser.entity.Site;
import com.leoxk.novelbrowser.util.JsoupUtil;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.text.Html.fromHtml;

public class ReadingFragment extends Fragment{
    @Bind(R.id.tv_reading_title)
    TextView tvTitle;
    @Bind(R.id.tv_reading_text)
    TextView tvText;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.et_reading_search)
    EditText et_reading_search;
    @Bind(R.id.reading_search)
    View readingSearch;
    @Bind(R.id.scrollView_reading)
    ScrollView readingScroll;
    @Bind(R.id.btn_next)
    ImageButton next;
    @Bind(R.id.btn_previous)
    ImageButton previous;

    private String url;
    private Page cur;
    private Site site;

    private String tvt;             //阅读模式中的文本内容
    private Spannable WordtoSpan;
    private int selectLength;       //所搜索的文字长度
    private List<Integer> selectIndex = new ArrayList<Integer>(); //记录所有搜索结果起始位置
    private int curSelectIndex = -1;

    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;


    final static float STEP = 200;  //调整字体大小最小距离
    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;

    private OnFragmentInteractionListener mListener;

    public ReadingFragment() {
    }

    public static ReadingFragment newInstance(String url) {
        ReadingFragment fragment = new ReadingFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            url = getArguments().getString("url");

        }

        site = NovelBrowserApplication.getInstance().getCurrentSite();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reading, container, false);
        ButterKnife.bind(this, view);

        tvText.setTextSize(mRatio+20);
        tvTitle.setTextSize(mRatio+30);

        readingScroll.setOnTouchListener(touchListener);

        readingSearch.animate().translationY(-readingSearch.getHeight());
        readingSearch.setVisibility(View.GONE);

        return view;
    }

    //监听触摸事件
    View.OnTouchListener touchListener = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("touch","ontouchlistener");
            if (event.getPointerCount() == 2) {//两个手指缩放
                int action = event.getAction();
                int pureaction = action & MotionEvent.ACTION_MASK;
                if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                    mBaseDist = getDistance(event);
                    mBaseRatio = mRatio;
                } else {
                    float delta = (getDistance(event) - mBaseDist) / STEP;
                    float multi = (float) Math.pow(2, delta);
                    mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                    tvText.setTextSize(mRatio + 20);
                    tvTitle.setTextSize(mRatio + 30);
                }
            }
            else if(event.getPointerCount() ==1){//一个手指滑动
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                    y1 = event.getY();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    x2 = event.getX();
                    y2 = event.getY();

                    if (y2 - y1 > 300) {//向下滑动

                        if (readingScroll.getScrollY() == 0) {
                            //显示页内搜索
                            readingSearch.setVisibility(View.VISIBLE);
                            readingSearch.animate()
                                    .translationY(0)
                                    .setDuration(500)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            readingSearch.setVisibility(View.VISIBLE);
                                            readingSearch.requestFocus();
                                        }
                                    });
                        }
                    }else if(Math.abs(x2 - x1) > 300){//水平滑动
                        WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
                        layout.screenBrightness = 1F;
                        getActivity().getWindow().setAttributes(layout);

                    }
                }
            }
            return false;
        }
    };

    //计算触摸滑动距离
    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(){
            @Override
            public void run() {
                initCurPage();
            }
        }.start();
    }

    //初始化当前页面
    private void initCurPage() {
        cur = new Page(this.url);
        initPage(cur);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText(cur.getTitle());
                tvText.setText(fromHtml(cur.getText()));

                WordtoSpan = new SpannableString( tvText.getText() );
                tvt = tvText.getText().toString();
            }
        });
    }

    //初始化页面数据
    private void initPage(Page page) {
        Document doc = page.getDoc()==null? JsoupUtil.getDoc(page.getUrl()):page.getDoc();

        String title = doc.getElementsByClass(site.getTitle()).text();
        String text = doc.getElementsByClass(site.getText()).html().replace("<br><br>","\n").replace("<br>", "").replace("&nbsp;", "  ");
        if(text == ""){
            text = doc.getElementById(site.getText()).html().replace("<br><br>","\n").replace("<br>", "").replace("&nbsp;", "  ");
        }
        page.setTitle(title);
        page.setText(text);
        page.setDoc(doc);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //取消按钮隐藏页内搜索框
    @OnClick(R.id.btn_cancel)
    void hideReadingSearch(){

        // Start the animation
        readingSearch.animate()
                .translationY(-readingSearch.getHeight())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        readingSearch.setVisibility(View.GONE);
                    }
                });
    }

    //页内搜索框的文本改变事件
    @OnTextChanged(R.id.et_reading_search)
    void onTextChange() {

        String text = et_reading_search.getText().toString();

        //去除所有高亮
        Object spansToRemove[] = WordtoSpan.getSpans(0, tvt.length(), Object.class);
        for(Object span: spansToRemove){
            if(span instanceof CharacterStyle)
                WordtoSpan.removeSpan(span);
        }
        //去除搜索记录
        selectIndex.clear();
        curSelectIndex = -1;


        if ((selectLength = text.length()) != 0) {
            int ofe = tvt.indexOf(text,0);
            for(int ofs=0; ofs<tvt.length() && ofe!=-1; ofs=ofe+1)
            {
                ofe = tvt.indexOf(text,ofs);
                if(ofe == -1)
                    break;
                else
                {
                    WordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe+text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    selectIndex.add(ofe);
                }
            }
            tvText.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
            nextWord();
        }
    }

    @OnClick(R.id.btn_next)
    void nextWord(){
        if(curSelectIndex < selectIndex.size()-1){
            curSelectIndex++;
            int start = selectIndex.get(curSelectIndex);
            scrollToIndex(start, start + selectLength);
        }
    }

    @OnClick(R.id.btn_previous)
    void previousWord(){
        if(curSelectIndex > 0){
            curSelectIndex--;
            int start = selectIndex.get(curSelectIndex);
            scrollToIndex(start, start + selectLength);
        }
    }

     public void scrollToIndex(int pos, int pos2){
         Layout layout = tvText.getLayout();
         readingScroll.scrollTo(0, layout.getLineTop(layout.getLineForOffset(pos2)));
    }

}
