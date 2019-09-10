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
@file:Suppress("DEPRECATION")

package com.afollestad.materialdialogssample

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission.READ_EXTERNAL_STORAGE
import com.afollestad.assent.Permission.WRITE_EXTERNAL_STORAGE
import com.afollestad.assent.runWithPermissions
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode.WRAP_CONTENT
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.datetime.timePicker
import com.afollestad.materialdialogs.files.fileChooser
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import kotlinx.android.synthetic.main.activity_main.basic
import kotlinx.android.synthetic.main.activity_main.basic_buttons
import kotlinx.android.synthetic.main.activity_main.basic_checkbox_titled_buttons
import kotlinx.android.synthetic.main.activity_main.basic_html_content
import kotlinx.android.synthetic.main.activity_main.basic_icon
import kotlinx.android.synthetic.main.activity_main.basic_long_titled_buttons
import kotlinx.android.synthetic.main.activity_main.basic_stacked_buttons
import kotlinx.android.synthetic.main.activity_main.basic_titled
import kotlinx.android.synthetic.main.activity_main.basic_titled_buttons
import kotlinx.android.synthetic.main.activity_main.bottomsheet_colorPicker
import kotlinx.android.synthetic.main.activity_main.bottomsheet_customView
import kotlinx.android.synthetic.main.activity_main.bottomsheet_dateTimePicker
import kotlinx.android.synthetic.main.activity_main.bottomsheet_grid
import kotlinx.android.synthetic.main.activity_main.bottomsheet_info
import kotlinx.android.synthetic.main.activity_main.bottomsheet_list
import kotlinx.android.synthetic.main.activity_main.buttons_callbacks
import kotlinx.android.synthetic.main.activity_main.buttons_neutral
import kotlinx.android.synthetic.main.activity_main.buttons_stacked
import kotlinx.android.synthetic.main.activity_main.buttons_stacked_checkboxPrompt
import kotlinx.android.synthetic.main.activity_main.colorChooser_accent
import kotlinx.android.synthetic.main.activity_main.colorChooser_customColors
import kotlinx.android.synthetic.main.activity_main.colorChooser_customColorsNoSub
import kotlinx.android.synthetic.main.activity_main.colorChooser_primary
import kotlinx.android.synthetic.main.activity_main.colorChooser_primary_customArgb
import kotlinx.android.synthetic.main.activity_main.colorChooser_primary_customRgb
import kotlinx.android.synthetic.main.activity_main.custom_view
import kotlinx.android.synthetic.main.activity_main.custom_view_webview
import kotlinx.android.synthetic.main.activity_main.date_picker
import kotlinx.android.synthetic.main.activity_main.datetime_picker
import kotlinx.android.synthetic.main.activity_main.file_chooser
import kotlinx.android.synthetic.main.activity_main.file_chooser_buttons
import kotlinx.android.synthetic.main.activity_main.file_chooser_filter
import kotlinx.android.synthetic.main.activity_main.folder_chooser_buttons
import kotlinx.android.synthetic.main.activity_main.folder_chooser_filter
import kotlinx.android.synthetic.main.activity_main.input
import kotlinx.android.synthetic.main.activity_main.input_check_prompt
import kotlinx.android.synthetic.main.activity_main.input_counter
import kotlinx.android.synthetic.main.activity_main.input_message
import kotlinx.android.synthetic.main.activity_main.list
import kotlinx.android.synthetic.main.activity_main.list_buttons
import kotlinx.android.synthetic.main.activity_main.list_checkPrompt_buttons
import kotlinx.android.synthetic.main.activity_main.list_dont_wait_positive
import kotlinx.android.synthetic.main.activity_main.list_long
import kotlinx.android.synthetic.main.activity_main.list_long_titled
import kotlinx.android.synthetic.main.activity_main.list_titled
import kotlinx.android.synthetic.main.activity_main.list_titled_buttons
import kotlinx.android.synthetic.main.activity_main.list_titled_message_buttons
import kotlinx.android.synthetic.main.activity_main.misc_dialog_callbacks
import kotlinx.android.synthetic.main.activity_main.multiple_choice
import kotlinx.android.synthetic.main.activity_main.multiple_choice_buttons
import kotlinx.android.synthetic.main.activity_main.multiple_choice_disabled_items
import kotlinx.android.synthetic.main.activity_main.multiple_choice_long_items
import kotlinx.android.synthetic.main.activity_main.single_choice_buttons_titled
import kotlinx.android.synthetic.main.activity_main.single_choice_disabled_items
import kotlinx.android.synthetic.main.activity_main.single_choice_long_items
import kotlinx.android.synthetic.main.activity_main.single_choice_titled
import kotlinx.android.synthetic.main.activity_main.time_picker

/** @author Aidan Follestad (afollestad) */
class MainActivity : AppCompatActivity() {
  private var debugMode = false
  private lateinit var prefs: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    prefs = getSharedPreferences(KEY_PREFS, MODE_PRIVATE)
    setTheme(
        when (prefs.getString(KEY_THEME, LIGHT)) {
          DARK -> R.style.AppTheme_Dark
          CUSTOM -> R.style.AppTheme_Custom
          else -> R.style.AppTheme
        }
    )
    debugMode = prefs.boolean(KEY_DEBUG_MODE, false)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    basic.onClickDebounced {
      MaterialDialog(this).show {
        message(R.string.shareLocationPrompt)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_titled.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_buttons.onClickDebounced {
      MaterialDialog(this).show {
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_stacked_buttons.onClickDebounced {
      MaterialDialog(this).show {
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(text = "This is a long button")
        negativeButton(text = "So is this, these should stack")
        debugMode(debugMode)
      }
    }

    basic_titled_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_html_content.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.app_name)
        message(R.string.htmlContent) {
          html { toast("Clicked link: $it") }
          lineSpacing(1.4f)
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_long_titled_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.loremIpsum)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_icon.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        icon(R.mipmap.ic_launcher)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    basic_checkbox_titled_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        checkBoxPrompt(R.string.checkboxConfirm) { checked ->
          toast("Checked? $checked")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list.onClickDebounced {
      MaterialDialog(this).show {
        listItems(R.array.socialNetworks) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_buttons.onClickDebounced {
      MaterialDialog(this).show {
        listItems(R.array.socialNetworks) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_dont_wait_positive.onClickDebounced {
      MaterialDialog(this).show {
        listItems(R.array.socialNetworks, waitForPositiveButton = false) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_titled.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItems(R.array.socialNetworks) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_titled_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItems(R.array.socialNetworks) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_titled_message_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        message(R.string.useGoogleLocationServices)
        listItems(R.array.socialNetworks) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_long.onClickDebounced {
      MaterialDialog(this).show {
        listItems(R.array.states) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_long_titled.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.states)
        listItems(R.array.states) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    list_checkPrompt_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItems(R.array.socialNetworks_longItems) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        checkBoxPrompt(R.string.checkboxConfirm) { checked ->
          toast("Checked? $checked")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    single_choice_titled.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsSingleChoice(R.array.socialNetworks, initialSelection = 1) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    single_choice_buttons_titled.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsSingleChoice(R.array.socialNetworks, initialSelection = 2) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.choose)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    single_choice_long_items.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsSingleChoice(R.array.socialNetworks_longItems) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.choose)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    single_choice_disabled_items.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsSingleChoice(
            R.array.socialNetworks, initialSelection = 1, disabledIndices = intArrayOf(1, 3)
        ) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.choose)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    multiple_choice.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsMultiChoice(
            R.array.socialNetworks, initialSelection = intArrayOf(1, 3)
        ) { _, indices, text ->
          toast("Selected items ${text.joinToString()} at indices ${indices.joinToString()}")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    multiple_choice_buttons.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsMultiChoice(
            R.array.socialNetworks, initialSelection = intArrayOf(1, 3)
        ) { _, indices, text ->
          toast("Selected items ${text.joinToString()} at indices ${indices.joinToString()}")
        }
        positiveButton(R.string.choose)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    multiple_choice_long_items.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsMultiChoice(
            R.array.socialNetworks_longItems, initialSelection = intArrayOf(0, 2)
        ) { _, indices, text ->
          toast("Selected items ${text.joinToString()} at indices ${indices.joinToString()}")
        }
        positiveButton(R.string.choose)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    multiple_choice_disabled_items.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.socialNetworks)
        listItemsMultiChoice(
            R.array.socialNetworks,
            initialSelection = intArrayOf(2, 3),
            disabledIndices = intArrayOf(1, 3)
        ) { _, indices, text ->
          toast("Selected items ${text.joinToString()} at indices ${indices.joinToString()}")
        }
        positiveButton(R.string.choose)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    buttons_stacked.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(text = "Hello World")
        negativeButton(text = "How are you doing?")
        neutralButton(text = "Testing long buttons")
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    buttons_stacked_checkboxPrompt.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(text = "Hello World")
        negativeButton(text = "How are you doing?")
        neutralButton(text = "Testing long buttons")
        checkBoxPrompt(R.string.checkboxConfirm) { checked ->
          toast("Checked? $checked")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    buttons_neutral.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        neutralButton(R.string.more_info)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    buttons_callbacks.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree) {
          toast("On positive")
        }
        negativeButton(R.string.disagree) {
          toast("On negative")
        }
        neutralButton(R.string.more_info) {
          toast("On neutral")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    misc_dialog_callbacks.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        onShow { toast("onPreShow") }
        onCancel { toast("onCancel") }
        onDismiss { toast("onDismiss") }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    input.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        input(
            hint = "Type something",
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        ) { _, text ->
          toast("Input: $text")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
      }
    }

    input_message.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        input(
            hint = "Type something",
            prefill = "Pre-filled!",
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        ) { _, text ->
          toast("Input: $text")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    input_counter.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        input(
            hint = "Type something",
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            maxLength = 8
        ) { _, text ->
          toast("Input: $text")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    input_check_prompt.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.useGoogleLocationServices)
        input(
            hint = "Type something",
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        ) { _, text ->
          toast("Input: $text")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        checkBoxPrompt(R.string.checkboxConfirm) { checked ->
          toast("Checked? $checked")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    custom_view.onClickDebounced { showCustomViewDialog() }

    custom_view_webview.onClickDebounced { showWebViewDialog() }

    colorChooser_primary.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.primary_colors)
        colorChooser(
            ColorPalette.Primary,
            ColorPalette.PrimarySub
        ) { _, color ->
          toast("Selected color: ${color.toHex()}")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    colorChooser_accent.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.accent_colors)
        colorChooser(
            ColorPalette.Accent,
            ColorPalette.AccentSub
        ) { _, color ->
          toast("Selected color: ${color.toHex()}")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    colorChooser_customColors.onClickDebounced {
      val topLevel = intArrayOf(TRANSPARENT, Color.RED, Color.YELLOW, Color.BLUE)
      val subLevel = arrayOf(
          intArrayOf(Color.WHITE, TRANSPARENT, Color.BLACK),
          intArrayOf(Color.LTGRAY, Color.GRAY, Color.DKGRAY),
          intArrayOf(Color.GREEN),
          intArrayOf(Color.MAGENTA, Color.CYAN)
      )

      MaterialDialog(this).show {
        title(R.string.custom_colors)
        colorChooser(topLevel, subLevel) { _, color ->
          val colorStr =
            if (color == TRANSPARENT) "transparent"
            else color.toHex()
          toast("Selected color: $colorStr")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    colorChooser_customColorsNoSub.onClickDebounced {
      val topLevel = intArrayOf(Color.RED, Color.YELLOW, Color.BLUE)

      MaterialDialog(this).show {
        title(R.string.custom_colors)
        colorChooser(topLevel) { _, color ->
          toast("Selected color: ${color.toHex()}")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    colorChooser_primary_customRgb.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.custom_colors_rgb)
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            allowCustomArgb = true
        ) { _, color ->
          toast("Selected color: ${color.toHex()}")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    colorChooser_primary_customArgb.onClickDebounced {
      MaterialDialog(this).show {
        title(R.string.custom_colors_argb)
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            allowCustomArgb = true,
            showAlphaSelector = true
        ) { _, color ->
          toast("Selected color: ${color.toHex()}")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    file_chooser.onClickDebounced { showFileChooser() }

    file_chooser_buttons.onClickDebounced { showFileChooserButtons() }

    file_chooser_filter.onClickDebounced { showFileChooserFilter() }

    folder_chooser_buttons.onClickDebounced { showFolderChooserButtons() }

    folder_chooser_filter.onClickDebounced { showFolderChooserFilter() }

    date_picker.onClickDebounced {
      MaterialDialog(this).show {
        datePicker { _, date ->
          toast("Selected date: ${date.formatDate()}")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    time_picker.onClickDebounced {
      MaterialDialog(this).show {
        title(text = "Select Time")
        timePicker { _, time ->
          toast("Selected time: ${time.formatTime()}")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    datetime_picker.onClickDebounced {
      MaterialDialog(this).show {
        title(text = "Select Date and Time")
        dateTimePicker(requireFutureDateTime = true) { _, dateTime ->
          toast("Selected date/time: ${dateTime.formatDateTime()}")
        }
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    bottomsheet_info.onClickDebounced {
      MaterialDialog(this, BottomSheet(WRAP_CONTENT)).show {
        title(R.string.useGoogleLocationServices)
        message(R.string.useGoogleLocationServicesPrompt)
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    bottomsheet_list.onClickDebounced {
      MaterialDialog(this, BottomSheet(WRAP_CONTENT)).show {
        listItems(R.array.states) { _, index, text ->
          toast("Selected item $text at index $index")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    bottomsheet_grid.onClickDebounced {
      val items = listOf(
          BasicGridItem(R.drawable.ic_icon_android, "One"),
          BasicGridItem(R.drawable.ic_icon_android, "Two"),
          BasicGridItem(R.drawable.ic_icon_android, "Three"),
          BasicGridItem(R.drawable.ic_icon_android, "Four"),
          BasicGridItem(R.drawable.ic_icon_android, "Five"),
          BasicGridItem(R.drawable.ic_icon_android, "Six"),
          BasicGridItem(R.drawable.ic_icon_android, "Seven"),
          BasicGridItem(R.drawable.ic_icon_android, "Eight")
      )

      MaterialDialog(this, BottomSheet(WRAP_CONTENT)).show {
        gridItems(items) { _, index, item ->
          toast("Selected item ${item.title} at index $index")
        }
        positiveButton(R.string.agree)
        negativeButton(R.string.disagree)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    bottomsheet_customView.onClickDebounced {
      showCustomViewDialog(BottomSheet(WRAP_CONTENT))
    }

    bottomsheet_colorPicker.onClickDebounced {
      MaterialDialog(this, BottomSheet(WRAP_CONTENT)).show {
        title(R.string.custom_colors_argb)
        colorChooser(
            colors = ColorPalette.Primary,
            subColors = ColorPalette.PrimarySub,
            allowCustomArgb = true,
            showAlphaSelector = true
        ) { _, color ->
          toast("Selected color: ${color.toHex()}")
        }
        positiveButton(R.string.select)
        negativeButton(android.R.string.cancel)
        debugMode(debugMode)
        lifecycleOwner(this@MainActivity)
      }
    }

    bottomsheet_dateTimePicker.onClickDebounced {
      MaterialDialog(this, BottomSheet(WRAP_CONTENT)).show {
        title(text = "Select Date and Time")
        dateTimePicker(requireFutureDateTime = true) { _, dateTime ->
          toast("Selected date/time: ${dateTime.formatDateTime()}")
        }
        lifecycleOwner(this@MainActivity)
        debugMode(debugMode)
      }
    }
  }

  private fun showCustomViewDialog(dialogBehavior: DialogBehavior = ModalDialog) {
    val dialog = MaterialDialog(this, dialogBehavior).show {
      title(R.string.googleWifi)
      customView(R.layout.custom_view, scrollable = true, horizontalPadding = true)
      positiveButton(R.string.connect) { dialog ->
        // Pull the password out of the custom view when the positive button is pressed
        val passwordInput: EditText = dialog.getCustomView()
            .findViewById(R.id.password)
        toast("Password: $passwordInput")
      }
      negativeButton(android.R.string.cancel)
      lifecycleOwner(this@MainActivity)
      debugMode(debugMode)
    }

    // Setup custom view content
    val customView = dialog.getCustomView()
    val passwordInput: EditText = customView.findViewById(R.id.password)
    val showPasswordCheck: CheckBox = customView.findViewById(R.id.showPassword)
    showPasswordCheck.setOnCheckedChangeListener { _, isChecked ->
      passwordInput.inputType =
        if (!isChecked) InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_CLASS_TEXT
      passwordInput.transformationMethod =
        if (!isChecked) PasswordTransformationMethod.getInstance() else null
    }
  }

  private fun showWebViewDialog() {
    val dialog = MaterialDialog(this).show {
      customView(R.layout.custom_view_webview, noVerticalPadding = true)
      debugMode(debugMode)
    }

    dialog.onShow {
      val webView: WebView = it.getCustomView()
          .findViewById(R.id.web_view)
      webView.loadData(
          "<h3>WebView Custom View</h3>\n" +
              "\n" +
              "<ol>\n" +
              "    <li><b>NEW:</b> Hey!</li>\n" +
              "    <li><b>IMPROVE:</b> Hello!</li>\n" +
              "    <li><b>FIX:</b> Hi!</li>\n" +
              "    <li><b>FIX:</b> Hey again!</li>\n" +
              "    <li><b>FIX:</b> What?</li>\n" +
              "    <li><b>FIX:</b> This is an example.</li>\n" +
              "    <li><b>MISC:</b> How are you?</li>\n" +
              "</ol>\n" +
              "<p>Material guidelines for dialogs:\n" +
              "    <a href='http://www.google.com/design/spec/components/dialogs.html'>" +
              "http://www.google.com/design/spec/components/dialogs.html</a>.\n" +
              "</p>",
          "text/html",
          "UTF-8"
      )
    }
  }

  private fun showFileChooser() = runWithPermissions(READ_EXTERNAL_STORAGE) {
    MaterialDialog(this).show {
      fileChooser { _, file ->
        toast("Selected file: ${file.absolutePath}")
      }
      debugMode(debugMode)
      lifecycleOwner(this@MainActivity)
    }
  }

  private fun showFileChooserButtons() = runWithPermissions(WRITE_EXTERNAL_STORAGE) {
    MaterialDialog(this).show {
      fileChooser(allowFolderCreation = true) { _, file ->
        toast("Selected file: ${file.absolutePath}")
      }
      negativeButton(android.R.string.cancel)
      positiveButton(R.string.select)
      debugMode(debugMode)
      lifecycleOwner(this@MainActivity)
    }
  }

  private fun showFileChooserFilter() = runWithPermissions(READ_EXTERNAL_STORAGE) {
    MaterialDialog(this).show {
      fileChooser(filter = { it.extension == "txt" }) { _, file ->
        toast("Selected file: ${file.absolutePath}")
      }
      debugMode(debugMode)
      lifecycleOwner(this@MainActivity)
    }
  }

  private fun showFolderChooserButtons() = runWithPermissions(WRITE_EXTERNAL_STORAGE) {
    MaterialDialog(this).show {
      folderChooser(allowFolderCreation = true) { _, folder ->
        toast("Selected folder: ${folder.absolutePath}")
      }
      negativeButton(android.R.string.cancel)
      positiveButton(R.string.select)
      debugMode(debugMode)
      lifecycleOwner(this@MainActivity)
    }
  }

  private fun showFolderChooserFilter() = runWithPermissions(READ_EXTERNAL_STORAGE) {
    MaterialDialog(this).show {
      folderChooser(filter = { it.name.startsWith("a", true) }) { _, folder ->
        toast("Selected folder: ${folder.absolutePath}")
      }
      debugMode(debugMode)
      lifecycleOwner(this@MainActivity)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    val theme = prefs.getString(KEY_THEME, LIGHT)
    if (theme == LIGHT) {
      menu.findItem(R.id.light_theme)
          .isChecked = true
    }
    if (theme == DARK) {
      menu.findItem(R.id.dark_theme)
          .isChecked = true
    }
    if (theme == CUSTOM) {
      menu.findItem(R.id.custom_theme)
          .isChecked = true
    }
    menu.findItem(R.id.debug_mode)
        .isChecked = debugMode
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.light_theme -> {
        prefs.commit {
          putString(KEY_THEME, LIGHT)
        }
        recreate()
        return true
      }
      R.id.dark_theme -> {
        prefs.commit {
          putString(KEY_THEME, DARK)
        }
        recreate()
        return true
      }
      R.id.custom_theme -> {
        prefs.commit {
          putString(KEY_THEME, CUSTOM)
        }
        recreate()
        return true
      }
      R.id.debug_mode -> {
        debugMode = !debugMode
        prefs.commit {
          putBoolean(KEY_DEBUG_MODE, debugMode)
        }
        invalidateOptionsMenu()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  companion object {
    private const val KEY_PREFS = "prefs"
    private const val KEY_THEME = "KEY_THEME"
    private const val KEY_DEBUG_MODE = "debug_mode"

    private const val LIGHT = "light"
    private const val DARK = "dark"
    private const val CUSTOM = "custom"
  }
}
