/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.materialdialogs.datetime.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.util.Calendar

/** @author Aidan Follestad (@afollestad) */
internal class TimeChangeListener<T : Any>(
  private var context: Context?,
  private val argument: T?,
  private var onChange: ((arg: T) -> Unit)? = null
) {
  private var lastHour: Int = -1
  private var lastMinute: Int = -1

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(
      context: Context?,
      intent: Intent?
    ) {
      val now = Calendar.getInstance()
      val newHour = now.get(Calendar.HOUR_OF_DAY)
      val newMinute = now.get(Calendar.MINUTE)

      if (argument != null && (lastHour != newHour || lastMinute != newMinute)) {
        onChange?.invoke(argument)
        lastHour = newHour
        lastMinute = newMinute
      }
    }
  }

  init {
    requireNotNull(context)
    requireNotNull(argument)
    requireNotNull(onChange)

    val filter = IntentFilter().apply {
      addAction(Intent.ACTION_TIME_TICK)
      addAction(Intent.ACTION_TIMEZONE_CHANGED)
      addAction(Intent.ACTION_TIME_CHANGED)
    }
    context!!.registerReceiver(receiver, filter)
  }

  fun dispose() {
    context?.unregisterReceiver(receiver)
    context = null
    onChange = null
  }
}
