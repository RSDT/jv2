package com.rsdt.jotiv2;

import android.support.design.widget.Snackbar;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 5-2-2016
 * Description...
 */
public final class SnackbarManager {

    private static ArrayList<OnSnackBarShowCallback> listeners = new ArrayList<>();

    public static void addListener(OnSnackBarShowCallback callback) { listeners.add(callback); }

    public static void removeListener(OnSnackBarShowCallback callback) { listeners.remove(callback); }

    public static void show(Snackbar snackbar)
    {
        /**
         * Allocate buffer callback outside loop.
         * */
        OnSnackBarShowCallback callback;

        /**
         * Loop through each callback.
         * */
        for(int i = 0; i < listeners.size(); i++)
        {
            callback = listeners.get(i);
            callback.onSnackbarShow(snackbar);
        }

        /**
         * Show the Snackbar.
         * */
        snackbar.show();
    }

    /**
     * Thissfsf
     * */
    public interface OnSnackBarShowCallback
    {
        void onSnackbarShow(Snackbar snackbar);
    }

}
