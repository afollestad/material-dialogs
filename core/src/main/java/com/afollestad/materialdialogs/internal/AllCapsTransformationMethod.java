package com.afollestad.materialdialogs.internal;

import android.content.Context;
import android.graphics.Rect;
import android.text.method.TransformationMethod;
import android.view.View;
import java.util.Locale;

class AllCapsTransformationMethod implements TransformationMethod {
  private Locale mLocale;

  AllCapsTransformationMethod(Context context) {
    mLocale = context.getResources().getConfiguration().locale;
  }

  @Override
  public CharSequence getTransformation(CharSequence source, View view) {
    return source != null ? source.toString().toUpperCase(mLocale) : null;
  }

  @Override
  public void onFocusChanged(
      View view,
      CharSequence sourceText,
      boolean focused,
      int direction,
      Rect previouslyFocusedRect) {}
}
