package com.example.myapplication;

import android.app.Application;

import com.wyc.logger.AndroidLogAdapter;
import com.wyc.logger.Logger;

/**
 * @ProjectName: Android Animator
 * @Package: com.example.myapplication
 * @ClassName: App
 * @Description: java类作用描述
 * @Author: wyc
 * @CreateDate: 2022-01-18 10:33
 * @UpdateUser: 更新者
 * @UpdateDate: 2022-01-18 10:33
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
