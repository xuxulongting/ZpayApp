package com.spreadtrum.iit.zpayapp.displaydemo;

import com.spreadtrum.iit.zpayapp.common.MyApplication;

import java.io.File;

/**
 * Created by SPREADTRUM\ting.long on 16-9-22.
 */
public class ImageFileCache {
    public static File getAppDir() {
        File appDir = new File(MyApplication.getContextObject().getExternalCacheDir(), "image");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return appDir;
    }

}
