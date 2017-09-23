package com.vincan.medialoader.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vincan.medialoader.DefaultConfigFactory;
import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.MediaLoader;
import com.vincan.medialoader.MediaLoaderConfig;
import com.vincan.medialoader.data.file.naming.Md5FileNameCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 *
 * @author vincanyang
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMediaLoader();
        initListView();
        findViewById(R.id.cleanCache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DownloadManager.getInstance(MainActivity.this).cleanCacheDir();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Error clean cache", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initMediaLoader() {
        MediaLoaderConfig mediaLoaderConfig = new MediaLoaderConfig.Builder(this)
                .cacheRootDir(DefaultConfigFactory.createCacheRootDir(this))
                .cacheFileNameGenerator(new Md5FileNameCreator())
                .maxCacheFilesCount(100)
                .maxCacheFilesSize(100 * 1024 * 1024)
                .maxCacheFileTimeLimit(5 * 24 * 60 * 60)
                .downloadThreadPoolSize(3)
                .downloadThreadPriority(Thread.NORM_PRIORITY)
                .build();
        MediaLoader.getInstance(this).init(mediaLoaderConfig);
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getDataList());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DataItem item = (DataItem) adapterView.getItemAtPosition(position);
                startActivity(new Intent(MainActivity.this, item.activityClass));
            }
        });
    }

    private List<DataItem> getDataList() {
        List<DataItem> list = new ArrayList<>();
        list.add(new DataItem(getString(R.string.scene_single_video), SingleVideoActivity.class));
        list.add(new DataItem(getString(R.string.scene_single_audio), SingleAudioActivity.class));
        list.add(new DataItem(getString(R.string.scene_viewpager_preload), VideoPagerActivity.class));
        list.add(new DataItem(getString(R.string.scene_viewpager_lazyload), VideoPagerLazyLoadActivity.class));
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaLoader.getInstance(MainActivity.this).destroy();
    }

    private static final class DataItem {

        private String title;

        private final Class activityClass;

        public DataItem(String title, Class activityClass) {
            this.title = title;
            this.activityClass = activityClass;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
