package com.vincan.medialoader.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.vincan.medialoader.MediaLoader;
import com.vincan.medialoader.download.DownloadListener;

import java.io.File;

/**
 * 播放音视频的懒加载Fragment
 *
 * @author vincanyang
 */
public class VideoLazyLoadFragment extends BaseLazyLoadFragment implements DownloadListener {

    private static final String TAG = VideoLazyLoadFragment.class.getSimpleName();

    private static final String BUNDLE_KEY_URL = "url";

    private VideoView mVideoView;

    private VideoViewSeekBar mVideoViewSeekBar;

    private MediaLoader mMediaLoader;

    private String mUrl;

    private boolean mIsVideoPathSet;

    public static Fragment getInstance(String url) {
        VideoLazyLoadFragment fragment = new VideoLazyLoadFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        mVideoView = (VideoView) rootView.findViewById(R.id.videoView);
        mVideoViewSeekBar = (VideoViewSeekBar) rootView.findViewById(R.id.mediaSeekBar);
        mVideoViewSeekBar.setVideoView(mVideoView);
        mUrl = getArguments().getString(BUNDLE_KEY_URL);

        mMediaLoader = MediaLoader.getInstance(getContext());
        mMediaLoader.addDownloadListener(mUrl, this);
        boolean isCached = mMediaLoader.isCached(mUrl);
        if (isCached) {
            mVideoViewSeekBar.setSecondaryProgress(mVideoViewSeekBar.getMax());
        }
        return rootView;
    }

    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();
        if (!mIsVideoPathSet) {
            String proxyUrl = mMediaLoader.getProxyUrl(mUrl);
            Log.e(TAG, "Use new proxy mUrl " + proxyUrl + " instead of old mUrl " + mUrl);
            mVideoView.setVideoPath(proxyUrl);//该方法调用后会发起视频下载请求
            mIsVideoPathSet = true;
        }
        if (mVideoViewSeekBar != null) {
            mVideoViewSeekBar.start();
        }
    }

    @Override
    protected void onFragmentInVisible() {
        super.onFragmentInVisible();
        if (mVideoViewSeekBar != null) {
            mVideoViewSeekBar.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        mMediaLoader.removeDownloadListener(mUrl, this);
    }

    @Override
    public void onProgress(String url, File file, int progress) {
        mVideoViewSeekBar.setSecondaryProgress(progress);
        Log.e(TAG, "Url " + url + " download progress:" + progress);
    }

    @Override
    public void onError(Throwable e) {

    }
}
