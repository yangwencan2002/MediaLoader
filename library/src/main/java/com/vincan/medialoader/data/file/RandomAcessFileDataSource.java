package com.vincan.medialoader.data.file;

import com.vincan.medialoader.data.file.cleanup.DiskLruCache;
import com.vincan.medialoader.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * {@link RandomAccessFile}实现
 *
 * @author vincanyang
 */
public class RandomAcessFileDataSource extends BaseFileDataSource {

    private static final String TEMP_POSTFIX = ".tmp";

    private RandomAccessFile mRandomAccessFile;

    public RandomAcessFileDataSource(File file, DiskLruCache diskLruStorage)  {
        super(file, diskLruStorage);
        try{
            boolean completed = file.exists();
            mOriginFile = completed ? file : new File(file.getParentFile(), file.getName() + TEMP_POSTFIX);
            mRandomAccessFile = new RandomAccessFile(mOriginFile, completed ? "r" : "rw");
        }catch (IOException e){
            //should not happen
            LogUtil.e(e);
        }
    }

    @Override
    public synchronized long length() throws IOException {
        return mRandomAccessFile.length();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        mRandomAccessFile.close();
    }

    @Override
    public synchronized int seekAndRead(long offset, byte[] buffer) throws IOException {
        //两个操作必须在同一个synchronized里，计同一个线程里
        mRandomAccessFile.seek(offset);
        return mRandomAccessFile.read(buffer, 0, buffer.length);
    }

    @Override
    public synchronized void append(byte[] data, int length) throws IOException {
        if (isCompleted()) {
            return;
        }
        mRandomAccessFile.seek(length());
        mRandomAccessFile.write(data, 0, length);
    }

    @Override
    public synchronized void complete() throws IOException {
        if (isCompleted()) {
            return;
        }
        close();
        renameCompletedFile();
    }

    private void renameCompletedFile() throws IOException {
        String newFileName = mOriginFile.getName().substring(0, mOriginFile.getName().length() - TEMP_POSTFIX.length());
        File completedFile = new File(mOriginFile.getParentFile(), newFileName);
        boolean renamed = mOriginFile.renameTo(completedFile);
        if (!renamed) {
            throw new IOException("Error renaming file " + mOriginFile + " to " + completedFile);
        }
        //refresh file
        mOriginFile = completedFile;
        mRandomAccessFile = new RandomAccessFile(mOriginFile, "r");
    }

    @Override
    public boolean isCompleted() {
        return !mOriginFile.getName().endsWith(TEMP_POSTFIX);
    }

    @Override
    public File getFile() {
        return mOriginFile;
    }
}
