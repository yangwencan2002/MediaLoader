package com.vincan.medialoader.sample;

import android.support.v4.app.Fragment;

/**
 * 懒加载Fragment
 *
 * @author vincanyang
 */
public class BaseLazyLoadFragment extends Fragment {

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {//来回切换tab被调用，但切Activity不会调用
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            onResume();
        } else if (!isVisibleToUser) {
            onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            //XXX the fragment is visible
            onFragmentVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //XXX the fragment is invisible
        onFragmentInVisible();
    }

    /**
     * Fragment可见
     */
    protected void onFragmentVisible() {

    }

    /**
     * Fragment不可见
     */
    protected void onFragmentInVisible() {

    }
}
