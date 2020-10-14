package com.MSMedia;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class App extends Application {

    // pswd fFUQD6YWBFBG
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myappID")
                .clientKey("fFUQD6YWBFBG")
                .server("http://13.235.24.171/parse/")
                .build()
        );

          ParseACL defaultACL = new ParseACL();
          defaultACL.setPublicReadAccess(true);
          defaultACL.setPublicWriteAccess(true);
          ParseACL.setDefaultACL(defaultACL, true);
    }
}
