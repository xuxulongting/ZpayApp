package com.spreadtrum.iit.zpayapp.network.io_file;

import com.spreadtrum.iit.zpayapp.common.MyApplication;

import java.io.File;

/**
 * Created by SPREADTRUM\ting.long on 16-9-22.
 */
public class ImageFileCache {
    /**
     * 创建缓存目录
     * @return
     */
    public static File getAppDir() {
        //创建缓存目录，存放图片，添加允许访问存储设备权限 "/storage/emulated/0/image"
        File appDir = new File(MyApplication.getContextObject().getExternalCacheDir(), "image");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return appDir;
    }

}
