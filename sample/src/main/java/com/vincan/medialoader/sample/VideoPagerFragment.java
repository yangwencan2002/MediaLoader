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
 * 用于ViewPager的VideoFragment
 *
 * @author vincanyang
 */
public class VideoPagerFragment extends Fragment implements DownloadListener {

    private static final String TAG = VideoPagerFragment.class.getSimpleName();

    private static final String BUNDLE_KEY_URL = "url";

    private VideoView mVideoView;

    private VideoViewSeekBar mVideoViewSeekBar;

    private String mUrl;

    private boolean mIsVisibleForUser;

    private MediaLoader mMediaLoader;

    public static Fragment getInstance(String url) {
        VideoPagerFragment fragment = new VideoPagerFragment();
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
        mVideoView.setVideoPath(mMediaLoader.getProxyUrl(mUrl));
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleForUser = isVisibleToUser;
        if (mIsVisibleForUser) {
            if (mVideoViewSeekBar != null) {
                mVideoViewSeekBar.start();
            }
        } else {
            if (mVideoViewSeekBar != null) {
                mVideoViewSeekBar.stop();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoViewSeekBar != null) {
            if (mIsVisibleForUser) {
                mVideoViewSeekBar.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
