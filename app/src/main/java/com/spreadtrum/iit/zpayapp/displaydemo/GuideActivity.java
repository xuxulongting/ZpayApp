package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-26.
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, View.OnTouchListener {
    private ViewPager viewPager;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private static final int[] yindaoPics={
        R.drawable.yindao1,R.drawable.yindao2};
    private ImageView[] points;
    private int currentIndex;

    /**
     * 初始化组件
     */
    private void initView(){
        //实例化ArrayList对象
        views = new ArrayList<View>();

        //实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.id_viewpager_yindao);

        //实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //初始化引导图片列表
        for(int i=0; i<yindaoPics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);//铺满屏幕
            iv.setLayoutParams(mParams);
            iv.setImageResource(yindaoPics[i]);
            views.add(iv);
        }

        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
//        viewPager.setOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        //设置监听，滑动到最后一页，到主界面
        viewPager.setOnTouchListener(this);
        //初始化底部小点
        initPoint();
    }

    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.id_ll_points);

        points = new ImageView[yindaoPics.length];

        //循环取得小点图片
        for (int i = 0; i < yindaoPics.length; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
            points[i].setOnClickListener(this);

            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
            //设置当面默认的位置
            currentIndex = 0;
            //设置为白色，即选中状态
            points[currentIndex].setEnabled(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yindao);
        initView();

        initData();
    }

    @Override
    public void onClick(View view) {
        int position = (Integer)view.getTag();
        setCurView(position);
        setCurDot(position);
        LogUtil.debug("onClick");
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position){
        if (position < 0 || position >= yindaoPics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon){
        if (positon < 0 || positon > yindaoPics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    /**
     * 滑动的时候，一直会执行
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

//        LogUtil.debug("onPageScrolled,position="+position+",positionOffset="+positionOffset);
    }

    /**
     *成功滑动一个页面执行一次
     * @param position 滑动后的页面的Index值
     */
    @Override
    public void onPageSelected(int position) {
        setCurDot(position);
//        LogUtil.debug("onPageSelected");
    }

    /**
     * 开始滑动，滑动中和滑动后都会执行
     *
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {
//        LogUtil.debug("onPageScrollStateChanged");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        float startX=0;
        float startY;//没有用到
        float endX;
        float endY;//没有用到
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                LogUtil.debug("ACTION_DOWN");
                startX=motionEvent.getX();
                startY=motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.debug("ACTION_UP");
                endX=motionEvent.getX();
                endY=motionEvent.getY();
                WindowManager windowManager= (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

                //获取屏幕的宽度
                Point size = new Point();
                windowManager.getDefaultDisplay().getSize(size);
                int width=size.x;

                //首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
//                LogUtil.debug("distance="+(startX-endX)+",width="+width/8);
                if((currentIndex==(views.size()-1)) && (Math.abs(startX-endX)>=(width/12))){
//                    LogUtil.debug("进入了触摸");
                    goToMainActivity();//进入主页
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);//这部分代码是切换Activity时的动画，看起来就不会很生硬
                }
                break;
        }
        return false;
    }

    public void goToMainActivity(){
        Intent intent = new Intent(this,MainDisplayActivity.class);
        startActivity(intent);
        finish();
    }
}
