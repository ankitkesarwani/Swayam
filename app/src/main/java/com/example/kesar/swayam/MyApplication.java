package com.example.kesar.swayam;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

import utils.SharedPrefManager;

/**
 * Created by kesar on 3/13/2018.
 */

public class MyApplication extends Application {

    private Locale locale = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (locale != null)
        {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = new SharedPrefManager(this).getLanguage();

        locale = new Locale(lang);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //AccountKit.initialize(getApplicationContext());
    }

}
