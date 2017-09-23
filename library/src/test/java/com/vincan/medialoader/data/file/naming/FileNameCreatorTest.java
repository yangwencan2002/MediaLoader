package com.vincan.medialoader.data.file.naming;

import com.vincan.medialoader.BuildConfig;
import com.vincan.medialoader.utils.Util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * {@link FileNameCreator}
 *
 * @author vincanyang
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FileNameCreatorTest {

    @Test(expected = NullPointerException.class)
    public void testAssertNullUrl() throws Exception {
        FileNameCreator fileNameCreator = new Md5FileNameCreator();
        fileNameCreator.create(null);
        fail("Url should be not null");
    }

    @Test
    public void testMd5Name() throws Exception {
        String url = "http://www.vincan.com/videos/video.mp4";
        FileNameCreator nameGenerator = new Md5FileNameCreator();
        String actual = nameGenerator.create(url);
        String expected = Util.getMD5(url) + ".mp4";
        assertEquals(actual, expected);
    }
}
