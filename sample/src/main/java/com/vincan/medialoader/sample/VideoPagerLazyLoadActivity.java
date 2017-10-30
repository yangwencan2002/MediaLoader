package com.vincan.medialoader.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.download.DownloadListener;

import java.io.File;

/**
 * ViewPager懒加载场景，演示ViewPager在懒加载场景（只加载当前页面）下如何使用{@link com.vincan.medialoader.MediaLoader}实现当前页边下边播，并通过{@link DownloadManager}实现后面页面的预下载
 *
 * @author vincanyang
 */
public class VideoPagerLazyLoadActivity extends FragmentActivity implements DownloadListener {

    private static final String TAG = VideoPagerLazyLoadActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        preDownloadVideos();
        setContentView(R.layout.activity_viewpager_video);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this));
    }

    private void preDownloadVideos() {
        for (MediaConfig video : MediaConfig.values()) {
            DownloadManager.getInstance(this).enqueue(new DownloadManager.Request(video.url), this);
        }
    }

    @Override
    public void onProgress(String url, File file, int progress) {
        Log.e(TAG, "Url " + url + " pre-download progress:" + progress);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance(this).stopAll();
    }

    private static final class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            MediaConfig video = MediaConfig.values()[position];
            return VideoLazyLoadFragment.getInstance(video.url);
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
