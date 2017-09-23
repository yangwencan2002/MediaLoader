package com.vincan.medialoader.sample;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.VideoView;

/**
 * 视频进度和拖拽条
 *
 * @author vincanyang
 */
public class VideoViewSeekBar extends android.support.v7.widget.AppCompatSeekBar {

    private VideoView mVideoView;

    private final VideoProgressUpdateHandler mVideoProgressUpdateHandler = new VideoProgressUpdateHandler();

    private int mVideoCurrentPosition;

    public VideoViewSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMax(100);
        setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mVideoView != null) {
                    int offset = mVideoView.getDuration() * getProgress() / getMax();
                    mVideoView.seekTo(offset);
                }
            }
        });
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }

    public void start() {
        mVideoView.seekTo(mVideoCurrentPosition);
        mVideoView.start();
        mVideoProgressUpdateHandler.start();
    }

    public void stop() {
        mVideoCurrentPosition = mVideoView.getCurrentPosition();
        mVideoView.pause();
        mVideoProgressUpdateHandler.stop();
    }

    private final class VideoProgressUpdateHandler extends Handler {

        private int WHAT = 0;

        public void start() {
            sendEmptyMessage(WHAT);
        }

        public void stop() {
            removeMessages(WHAT);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mVideoView != null) {
                int playProgress = mVideoView.getCurrentPosition() * getMax() / mVideoView.getDuration();
                setProgress(playProgress);
                sendEmptyMessageDelayed(0, 500);
            }
        }
    }
}
