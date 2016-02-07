package com.rsdt.jotiv2;

import android.support.design.widget.Snackbar;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 5-2-2016
 * Description...
 */
public final class SnackbarControl {

    private static ArrayList<OnSnackBarShowCallback> showCallbacks = new ArrayList<>();

    private static ArrayList<OnSnackbarDismissedCallback> dismissedCallbacks = new ArrayList<>();

    public static void addShowListener(OnSnackBarShowCallback callback) { showCallbacks.add(callback); }

    public static void removeShowListener(OnSnackBarShowCallback callback) { showCallbacks.remove(callback); }

    public static void addDismissListener(OnSnackbarDismissedCallback callback) { dismissedCallbacks.add(callback); }

    public static void removeDismissListener(OnSnackbarDismissedCallback callback) { dismissedCallbacks.remove(callback); }

    public static void show(Snackbar snackbar)
    {

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

                OnSnackbarDismissedCallback callback;

                for(int i = 0; i < dismissedCallbacks.size(); i++)
                {
                    callback = dismissedCallbacks.get(i);
                    callback.onSnackbarDismissed(snackbar, event);
                }
            }
        });

        /**
         * Allocate buffer callback outside loop.
         * */
        OnSnackBarShowCallback callback;

        /**
         * Loop through each callback.
         * */
        for(int i = 0; i < showCallbacks.size(); i++)
        {
            callback = showCallbacks.get(i);
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

    public interface OnSnackbarDismissedCallback
    {
        void onSnackbarDismissed(Snackbar snackbar, int event);
    }

}
