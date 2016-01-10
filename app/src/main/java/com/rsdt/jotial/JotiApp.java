package com.rsdt.jotial;

import android.app.Application;
import android.content.Context;

import com.rsdt.jotiv2.Tracker;

/**
 * @author Mattijn Kreuzen
 * @version 1.0
 * @since 22-10-2015
 * Description...
 */
public class JotiApp extends Application {

    private static JotiApp instance;

    public static Tracker MainTracker = new Tracker();

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
