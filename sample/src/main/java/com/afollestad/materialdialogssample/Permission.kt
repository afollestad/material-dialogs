package com.afollestad.materialdialogssample

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.annotation.CheckResult
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.Arrays

typealias Callback = (Result) -> Unit

class Permission(private val activity: Activity) {

  companion object {
    const val PERM_RQ = 79
  }

  private val callbacks = mutableMapOf<Array<String>, Callback>()

  @CheckResult
  fun has(permissions: String): Boolean {
    return ContextCompat.checkSelfPermission(activity, permissions) ==
        PERMISSION_GRANTED
  }

  @CheckResult
  fun hasAll(permissions: Array<String>): Boolean {
    for (perm in permissions) {
      if (!has(perm)) return false
    }
    return true
  }

  fun request(
    permissions: Array<String>,
    callback: Callback
  ) {
    if (hasAll(permissions)) {
      val result = Result(permissions,
          IntArray(permissions.size) { PERMISSION_GRANTED })
      callback.invoke(result)
      return
    }
    callbacks[permissions] = callback
    ActivityCompat.requestPermissions(activity, permissions, PERM_RQ)
  }

  fun response(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    if (requestCode != PERM_RQ) return
    val callback = callbacks[permissions]
    val result = Result(permissions, grantResults)
    callback?.invoke(result)
  }
}

class Result(
  val permissions: Array<out String>,
  val grantResults: IntArray
) {

  @CheckResult
  fun granted(permission: String): Boolean {
    val index = permissions.indexOf(permission)
    if (index == -1) return false
    return grantResults[index] == PERMISSION_GRANTED
  }

  @CheckResult
  fun allGranted(): Boolean {
    for (perm in permissions) {
      if (!granted(perm)) return false
    }
    return true
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Result

    if (!Arrays.equals(permissions, other.permissions)) return false
    if (!Arrays.equals(grantResults, other.grantResults)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = Arrays.hashCode(permissions)
    result = 31 * result + Arrays.hashCode(grantResults)
    return result
  }
}