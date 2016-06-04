package com.logan19gp.flickrviewer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.logan19gp.flickrviewer.network.ServerAPIClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by george on 6/2/2016.
 */
public class MainApplication extends Application
{
    private static Context context;
    private static boolean imageLoaderHasBeenInitialized = false;
    public final static String GENERAL_PREFS = "general_prefs";

    public static Context getAppContext()
    {
        return context;
    }

    public static boolean isDebug()
    {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        MainApplication.context = getApplicationContext();
        ServerAPIClient.initializeRequestQueue(context);
        MainApplication.initializeImageLoader(this);
    }

    public synchronized static void initializeImageLoader(Context context)
    {
        if (!imageLoaderHasBeenInitialized)
        {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(false).
                    cacheOnDisk(true).considerExifParams(true).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .defaultDisplayImageOptions(defaultOptions).imageDownloader(new BaseImageDownloader(context))
                    .build();
            ImageLoader.getInstance().init(config);
            ImageLoader.getInstance().clearDiskCache();
            imageLoaderHasBeenInitialized = true;
        }
    }

    /**
     *
     * @param key
     * @param value
     */
    public static void saveToPrefs(String key, String value)
    {
        SharedPreferences prefs = context.getSharedPreferences(GENERAL_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String loadFromPrefs(String key, String defaultValue)
    {
        SharedPreferences prefs = context.getSharedPreferences(GENERAL_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param value
     */
    public static void saveToPrefsLong(String key, Long value)
    {
        SharedPreferences prefs = context.getSharedPreferences(GENERAL_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static Long loadFromPrefsLong(String key, Long defaultValue)
    {
        SharedPreferences prefs = context.getSharedPreferences(GENERAL_PREFS, Context.MODE_PRIVATE);
        return prefs.getLong(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param value
     */
    public static void saveToPrefsInt(String key, Integer value)
    {
        SharedPreferences prefs = context.getSharedPreferences(GENERAL_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer loadFromPrefsInt(String key, Integer defaultValue)
    {
        SharedPreferences prefs = context.getSharedPreferences(GENERAL_PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

}