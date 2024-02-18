package com.aliernfrog.pftool;

interface IFileService {
    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1;

    String[] listFiles(String path) = 2;
}