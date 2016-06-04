package com.logan19gp.flickrviewer.utils;

import android.util.Log;

import com.logan19gp.flickrviewer.MainApplication;

/**
 * Created by george on 6/3/2016.
 */
public class LogUtil
{

    public static void log(String log)
    {
        if (MainApplication.isDebug())
        {
            Log.d("flickr viewer".toUpperCase(), log);
        }
    }
}
