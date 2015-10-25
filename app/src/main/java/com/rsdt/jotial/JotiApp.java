package com.rsdt.jotial;

import android.app.Application;
import android.content.Context;

/**
 * @author Mattijn Kreuzen
 * @version 1.0
 * @since 22-10-2015
 * Description...
 */
public class JotiApp extends Application {

    private static JotiApp instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static Context getContext()
    {
        return instance;
    }
}
