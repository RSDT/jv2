package com.rsdt.jotial;

import android.app.Application;
import android.content.Context;

import com.rsdt.jotial.communication.area348.Auth;
import com.rsdt.jotiv2.Tracker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 22-10-2015
 * Class that represents the JotiApp.
 */
public class JotiApp extends Application {

    /**
     * Holds the instance of JotiApp.
     * */
    private static JotiApp instance;

    /**
     * The MainTracker, it tracks all events, and triggers other events.
     * */
    public static Tracker MainTracker = new Tracker();

    /**
     * The Auth class for the App, it handles all auth procedures.
     * */
    public static Auth Auth = new Auth();

    @Override
    public void onCreate() {
        instance = this;
        Auth.initialize();
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    /**
     * Gets the context of the JotiApp instance.
     * */
    public static Context getContext()
    {
        return instance;
    }
}
