package com.rsdt.jotial.misc;

import android.preference.PreferenceManager;
import android.util.Log;

import com.rsdt.jotial.JotiApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-2-2016
 * Description...
 */
public class VosUtil {

    public static float calculateRadius(String date)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            return calculateRadius(dateFormat.parse(date));
        }
        catch(Exception e)
        {
            Log.e("VosUtil", "Calculation on Vos radius failed.", e);
        }
        return 0;
    }

    public static float calculateRadius(Date date)
    {
        try {
            long duration = new Date().getTime() - date.getTime();

            float diffInHours = TimeUnit.MILLISECONDS.toSeconds(duration) / 60f / 60f;

            if (diffInHours > MAX_DIFF_HOUR)
                diffInHours = MAX_DIFF_HOUR;
            float mPerHour = ( Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).getString("pref_misc_walking_speed", "6.0f")) * 1000);
            return (diffInHours * mPerHour);
        } catch (Exception e) {
            Log.e("VosUtil", "Calculation on Vos radius failed.", e);
        }
        return 0;
    }

    public static final float MAX_DIFF_HOUR = 30;

}
