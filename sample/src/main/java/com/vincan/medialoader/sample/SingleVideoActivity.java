package com.vincan.medialoader.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 单个视频场景，演示播放单个视频场景下如何使用{@link com.vincan.medialoader.MediaLoader}
 *
 * @author vincanyang
 */
public class SingleVideoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_single_video);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerView, MediaFragment.getInstance(MediaConfig.PIG1.url))
                .commit();
    }
}
