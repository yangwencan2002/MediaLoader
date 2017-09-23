package com.vincan.medialoader.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 单个音频场景，演示播放单个音频场景下如何使用{@link com.vincan.medialoader.MediaLoader}
 *
 * @author vincanyang
 */
public class SingleAudioActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_single_video);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerView, MediaFragment.getInstance(MediaConfig.CLOUD.url))
                .commit();
    }
}
