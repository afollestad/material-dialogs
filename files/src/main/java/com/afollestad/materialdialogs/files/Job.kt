/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialdialogs.files

import android.os.Handler

internal typealias Execution<T> = (Job<T>) -> T
internal typealias PostExecution<T> = (T) -> Unit

// Can probably be replaced with coroutines
internal class Job<T>(private val execution: Execution<T>) {

  private var thread: Thread? = null
  private var after: ((T) -> Unit)? = null
  private var handler = Handler()

  var isAborted: Boolean = false
    private set

  fun after(after: PostExecution<T>): Job<T> {
    this.after = after
    return execute()
  }

  fun abort() {
    thread?.interrupt()
    thread = null
  }

  private fun execute(): Job<T> {
    thread = Thread(Runnable {
      val result = execution(this@Job)
      if (isAborted) return@Runnable
      handler.post { after?.invoke(result) }
    })
    thread!!.start()
    return this
  }
}

internal fun <T> job(execution: Execution<T>): Job<T> {
  return Job(execution)
}
