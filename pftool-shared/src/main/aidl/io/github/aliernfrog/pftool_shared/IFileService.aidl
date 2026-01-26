package io.github.aliernfrog.pftool_shared;

import android.os.ParcelFileDescriptor;
import io.github.aliernfrog.pftool_shared.data.ServiceFile;

interface IFileService {
    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1;

    void copy(String sourcePath, String targetPath) = 2;

    void createNewFile(String path) = 3;

    void delete(String path) = 4;
    
    boolean exists(String path) = 5;

    ServiceFile getFile(String path) = 6;

    ServiceFile[] listFiles(String path) = 7;

    void mkdirs(String path) = 8;

    void renameFile(String oldPath, String newPath) = 9;

    ParcelFileDescriptor getFd(String path) = 10;
}