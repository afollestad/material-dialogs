package com.afollestad.materialdialogssample

import android.app.Application
import leakcanary.LeakCanary

/** @author Aidan Follestad (afollestad) */
class SampleApp : Application() {

  override fun onCreate() {
    super.onCreate()
    LeakCanary.config = LeakCanary.config.copy(retainedVisibleThreshold = 2)
  }
}