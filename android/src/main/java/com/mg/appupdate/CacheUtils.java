package com.mg.appupdate;

import android.content.Context;

import java.io.File;

public class CacheUtils {

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static File getExternalCacheDir(Context context) {

        // return context.getExternalCacheDir(); API level 8

        // e.g. "<sdcard>/Android/data/<package_name>/cache/"

        return context.getExternalCacheDir();
    }

    /**
     * 转换文件大小名称
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSizeName(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = "B";
        } else if (fileS < 1048576) {
            fileSizeString = "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = "MB";
        } else {
            fileSizeString = "G";
        }
        return fileSizeString;
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS);
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024);
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576);
        } else {
            fileSizeString = df.format((double) fileS / 1073741824);
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
//    public static String formatFileSize(long fileS) {
//        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
//        String fileSizeString = "";
//        if (fileS < 1024) {
//            fileSizeString = df.format((double) fileS) + "B";
//        } else if (fileS < 1048576) {
//            fileSizeString = df.format((double) fileS / 1024) + "KB";
//        } else if (fileS < 1073741824) {
//            fileSizeString = df.format((double) fileS / 1048576) + "MB";
//        } else {
//            fileSizeString = df.format((double) fileS / 1073741824) + "G";
//        }
//        return fileSizeString;
//    }




    /**
     * 清除缓存目录
     * 目录
     * 当前系统时间
     */
    public static int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }
}
