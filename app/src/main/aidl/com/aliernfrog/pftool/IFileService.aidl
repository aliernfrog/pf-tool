package com.aliernfrog.pftool;

import android.os.ParcelFileDescriptor;
import com.aliernfrog.pftool.data.ServiceFile;

interface IFileService {
    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1;

    void copy(String sourcePath, String targetPath) = 2;

    void delete(String path) = 3;
    
    boolean exists(String path) = 4;

    ServiceFile getFile(String path) = 5;

    ServiceFile[] listFiles(String path) = 6;

    void mkdirs(String path) = 7;

    void renameFile(String oldPath, String newPath) = 8;

    void unzipMap(String path, String targetPath) = 9;

    void zipMap(String path, String targetPath) = 10;

    ParcelFileDescriptor getFd(String path) = 11;
}