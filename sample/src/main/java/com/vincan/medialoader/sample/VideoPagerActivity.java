package com.vincan.medialoader.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * ViewPager懒加载场景，演示ViewPager在预加载场景（同时加载3个page）下如何使用{@link com.vincan.medialoader.MediaLoader}实现当前页、上一页、后一页边下边播功能，减少用户等待视频下载时间
 *
 * @author vincanyang
 */
public class VideoPagerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_viewpager_video);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this));
    }

    private static final class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            MediaConfig video = MediaConfig.values()[position];
            return VideoPagerFragment.getInstance(video.url);
        }

        @Override
        public int getCount() {
            return MediaConfig.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MediaConfig.values()[position].name();
        }
    }
}
