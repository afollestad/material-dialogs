package com.afollestad.materialdialogssample;

import android.app.Application;
import com.facebook.stetho.Stetho;

/**
 * @author Aidan Follestad (afollestad)
 */
public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}
