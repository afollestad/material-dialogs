package com.afollestad.materialdialogs;

/** @author Aidan Follestad (afollestad) */
public enum StackingBehavior {
  /** The action buttons are always stacked vertically. */
  ALWAYS,
  /** The action buttons are stacked vertically IF it is necessary for them to fit in the dialog. */
  ADAPTIVE,
  /** The action buttons are never stacked, even if they should be. */
  NEVER
}
