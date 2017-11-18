//--ADD NT-LWL 17/08/30 Launch FR -
package jp.co.misumi.misumiecapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import jp.co.misumi.misumiecapp.FragmentController;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.SaicataId;
import jp.co.misumi.misumiecapp.activity.MainActivity;
import jp.co.misumi.misumiecapp.widget.IndicatorView;

/**
 * 启动引导画面
 */
public class LaunchFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    public static final String NAME = "flag.xml";
    public static final String IS_FIRST = "isFirst";
    private ViewPager mViewPager;
    private List<View> list;
    private PagerAdapter mPagerAdapter;
    // 立即体验按钮
    private View btnEnter;
    // 指示器
    private IndicatorView indicator;

    @Override
    public String getScreenId() {
        return null;
    }

    @Override
    protected String getSaicataId() {
        return SaicataId.LAUNCH_PAGE;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) mParent).getSlideMenu().setEnable(false);
        View view = inflater.inflate(R.layout.activity_launch, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        indicator = (IndicatorView) view.findViewById(R.id.indicator);
        btnEnter = view.findViewById(R.id.btn_enter);
        btnEnter.setVisibility(View.GONE);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置侧滑菜单 不能滑动
                ((MainActivity) mParent).getSlideMenu().setEnable(true);
                getFragmentController().replaceFragment(new TopFragment(), FragmentController.ANIMATION_FADE_IN);
            }
        });
        initData();
        return view;
    }

    // 判断是否是第一次进入
    public static boolean isFirstEnter(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_FIRST, true);
    }

    // 保存进入标识
    public static void saveFirstFlag(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(IS_FIRST, false).commit();
    }

    // 初始化数据
    private void initData() {
        list = new ArrayList<>();
        ImageView image1 = new ImageView(mParent);
        image1.setScaleType(ImageView.ScaleType.FIT_XY);
        ViewPager.LayoutParams params = new ViewPager.LayoutParams();
        params.width = ViewPager.LayoutParams.MATCH_PARENT;
        params.height = ViewPager.LayoutParams.MATCH_PARENT;
        image1.setImageResource(R.drawable.launch_guide_1);
        image1.setLayoutParams(params);
        list.add(image1);
        ImageView image2 = new ImageView(mParent);
        image2.setScaleType(ImageView.ScaleType.FIT_XY);
        image2.setImageResource(R.drawable.launch_guide_2);
        image2.setLayoutParams(params);
        list.add(image2);
        ImageView image3 = new ImageView(mParent);
        image3.setScaleType(ImageView.ScaleType.FIT_XY);
        image3.setImageResource(R.drawable.launch_guide_3);
        image3.setLayoutParams(params);
        list.add(image3);
        ImageView image4 = new ImageView(mParent);
        image4.setScaleType(ImageView.ScaleType.FIT_XY);
        image4.setImageResource(R.drawable.launch_guide_4);
        image4.setLayoutParams(params);
        list.add(image4);
        ImageView image5 = new ImageView(mParent);
        image5.setScaleType(ImageView.ScaleType.FIT_XY);
        image5.setImageResource(R.drawable.launch_guide_5);
        image5.setLayoutParams(params);
        list.add(image5);
        mPagerAdapter = new ViewPagerAdapter(list);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        // 设置指示器
        indicator.setViewPager(mViewPager);
        indicator.setViewPagerEvent(mViewPager);
        indicator.setPosition(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 设置最后一页可点击
        if (position == 4) {
            btnEnter.setVisibility(View.VISIBLE);
            btnEnter.setEnabled(true);
            btnEnter.setClickable(true);
        } else {
            btnEnter.setVisibility(View.GONE);
            btnEnter.setEnabled(false);
            btnEnter.setClickable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class ViewPagerAdapter extends PagerAdapter {
        // 视图集合
        private List<View> views;

        public ViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            if (views == null) {
                return 0;
            }
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 实例页卡
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 删除页卡
            container.removeView((View) object);
        }
    }
}
//--ADD NT-LWL 17/08/30 Launch FR -