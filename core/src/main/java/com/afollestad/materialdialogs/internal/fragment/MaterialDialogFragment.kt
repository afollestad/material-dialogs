package com.afollestad.materialdialogs.internal.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView

abstract class MaterialDialogFragment : DialogFragment() {

  /**
   * This is the place where you should inflate your view and apply the logic to your UI components.
   * This method is called after MaterialDialog is instantiated, so you can use the dialog parameter
   * for further customizations.
   * @return Return a view that will be applied as a customView to the MaterialDialog instance.
   */
  abstract fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?,
                            dialog: MaterialDialog): View

  /**
   * This method won't be called, thus overriding in sub-classes will be source of bugs
   */
  final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }

  /**
   * Result of this method won't be used, this overriding in sub-classes will be source of bugs
   */
  final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  /**
   * This method instantiate MaterialDialog, and pass itself as an argument to #onCreateView(...),
   * thus provide a way for further customization
   * @return Return an instance of MaterialDialog to be displayed by the fragment.
   */
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val materialDialog = MaterialDialog(context!!)
    val layoutInflater = LayoutInflater.from(context)
    val view = onCreateView(layoutInflater, savedInstanceState, materialDialog)
    return materialDialog.customView(view = view, scrollable = true)
  }
}