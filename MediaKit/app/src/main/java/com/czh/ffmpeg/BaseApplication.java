package com.czh.ffmpeg;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: BaseApplication
 * @Description: Version 1.0.0, 2019-01-26, caizhenghe create file.
 */

public class BaseApplication extends android.app.Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == Process.myPid())
            {
                processName = process.processName;
            }
        }

        Log.e(TAG, "application onCreate: process name = " + processName);
    }
}
