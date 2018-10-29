package com.czh.ascpicture;

import java.io.File;

/**
 * Created by Administrator on 2018/10/29.
 */

public class VideoUtils {
    public static boolean checkFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }
}
