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
package com.afollestad.materialdialogs.files.utilext

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment.getExternalStorageDirectory
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import java.io.File

internal fun File.hasParent() = betterParent() != null

internal fun File.isExternalStorage() =
  absolutePath == getExternalStorageDirectory().absolutePath

internal fun File.isRoot() = absolutePath == "/"

internal fun File.betterParent(): File? {
  if (this.isExternalStorage()) {
    // Emulated external storage's parent is empty so jump over it
    return getExternalStorageDirectory().parentFile.parentFile
  }
  if (parentFile?.isRoot() == true) {
    val rootContent = parentFile.list() ?: emptyArray()
    if (rootContent.isEmpty()) {
      // If device isn't rooted, don't allow root dir access so we don't get stuck
      return null
    }
  }
  // Else normal operation
  return parentFile
}

internal fun File.jumpOverEmulated(): File {
  if (absolutePath == getExternalStorageDirectory().parentFile.absolutePath) {
    // Emulated external storage's parent is empty so jump over it
    return getExternalStorageDirectory()
  }
  return this
}

internal fun File.friendlyName() = when {
  isExternalStorage() -> "External Storage"
  isRoot() -> "Root"
  else -> name
}

internal fun Context.hasPermission(permission: String): Boolean {
  return ContextCompat.checkSelfPermission(this, permission) ==
      PackageManager.PERMISSION_GRANTED
}

internal fun MaterialDialog.hasReadStoragePermission(): Boolean {
  return windowContext.hasPermission(permission.READ_EXTERNAL_STORAGE)
}

internal fun MaterialDialog.hasWriteStoragePermission(): Boolean {
  return windowContext.hasPermission(permission.WRITE_EXTERNAL_STORAGE)
}
