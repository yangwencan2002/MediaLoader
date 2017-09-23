package com.vincan.medialoader.data.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * 大文件读写RandomAccessFile性能较差，所以需要一个具备缓存能力的RandomAccessFile
 * //TODO
 *
 * @author vincanyang
 */
public class BufferedRandomAccessFile extends RandomAccessFile {

    public BufferedRandomAccessFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }
}