package pt.apps.leandro.portfolio;

import android.app.Application;

import java.util.ArrayList;

public class MyApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        // This is called while the app is starting, before the activity onCreate
        Globals.products = new ArrayList<>();

    }

}
