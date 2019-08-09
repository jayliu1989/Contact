package com.jay.contact;

import android.app.Application;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fragmentation.builder()
                .stackViewMode(Fragmentation.NONE)
                .debug(BuildConfig.DEBUG)
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                    }
                })
                .install();
    }
}
