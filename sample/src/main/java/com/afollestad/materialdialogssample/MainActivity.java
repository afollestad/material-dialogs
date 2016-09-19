package com.afollestad.materialdialogssample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AppCompatActivity implements
        FolderChooserDialog.FolderCallback, FileChooserDialog.FileCallback, ColorChooserDialog.ColorCallback {

    private final static int STORAGE_PERMISSION_RC = 69;
    // custom view dialog
    private EditText passwordInput;
    private View positiveAction;
    // color chooser dialog
    private int primaryPreselect;

    // UTILITY METHODS
    private int accentPreselect;
    private Toast mToast;
    private Thread mThread;
    private Handler mHandler;

    private int chooserDialog;

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void startThread(Runnable run) {
        if (mThread != null)
            mThread.interrupt();
        mThread = new Thread(run);
        mThread.start();
    }

    private void showToast(@StringRes int message) {
        showToast(getString(message));
    }

    // BEGIN SAMPLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mHandler = new Handler();
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mThread != null && !mThread.isInterrupted() && mThread.isAlive())
            mThread.interrupt();
    }

    @OnClick(R.id.basicNoTitle)
    public void showBasicNoTitle() {
        new MaterialDialog.Builder(this)
                .content(R.string.shareLocationPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    @OnClick(R.id.basic)
    public void showBasic() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    @OnClick(R.id.basicLongContent)
    public void showBasicLongContent() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.loremIpsum)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    @OnClick(R.id.basicIcon)
    public void showBasicIcon() {
        new MaterialDialog.Builder(this)
                .iconRes(R.mipmap.ic_launcher)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    @OnClick(R.id.basicCheckPrompt)
    public void showBasicCheckPrompt() {
        new MaterialDialog.Builder(this)
                .iconRes(R.mipmap.ic_launcher)
                .limitIconToDefaultSize()
                .title(Html.fromHtml(getString(R.string.permissionSample, getString(R.string.app_name))))
                .positiveText(R.string.allow)
                .negativeText(R.string.deny)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showToast("Prompt checked? " + dialog.isPromptCheckBoxChecked());
                    }
                })
                .checkBoxPromptRes(R.string.dont_ask_again, false, null)
                .show();
    }

    @OnClick(R.id.stacked)
    public void showStacked() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.speedBoost)
                .negativeText(R.string.noThanks)
                .btnStackedGravity(GravityEnum.END)
                .stackingBehavior(StackingBehavior.ALWAYS)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    @OnClick(R.id.neutral)
    public void showNeutral() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .show();
    }

    @OnClick(R.id.callbacks)
    public void showCallbacks() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showToast(which.name() + "!");
                    }
                })
                .show();
    }

    @OnClick(R.id.list)
    public void showList() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                    }
                })
                .show();
    }

    @OnClick(R.id.listNoTitle)
    public void showListNoTitle() {
        new MaterialDialog.Builder(this)
                .items(R.array.socialNetworks)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                    }
                })
                .show();
    }

    @OnClick(R.id.longList)
    public void showLongList() {
        new MaterialDialog.Builder(this)
                .title(R.string.states)
                .items(R.array.states)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                    }
                })
                .positiveText(android.R.string.cancel)
                .show();
    }

    @OnClick(R.id.list_longItems)
    public void showListLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks_longItems)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                    }
                })
                .show();
    }

    @OnClick(R.id.list_checkPrompt)
    public void showListCheckPrompt() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                    }
                })
                .checkBoxPromptRes(R.string.example_prompt, true, null)
                .negativeText(android.R.string.cancel)
                .show();
    }

    static int index = 0;

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.list_longPress)
    public void showListLongPress() {
        index = 0;
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                    }
                })
                .autoDismiss(false)
                .itemsLongCallback(new MaterialDialog.ListLongCallback() {
                    @Override
                    public boolean onLongSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        dialog.getItems().remove(position);
                        dialog.notifyItemsChanged();
                        return false;
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        index++;
                        dialog.getItems().add("Item " + index);
                        dialog.notifyItemInserted(dialog.getItems().size() - 1);
                    }
                })
                .neutralText(R.string.add_item)
                .show();
    }

    @OnClick(R.id.singleChoice)
    public void showSingleChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    @OnClick(R.id.singleChoice_longItems)
    public void showSingleChoiceLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks_longItems)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showToast(which + ": " + text);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    @OnClick(R.id.multiChoice)
    public void showMultiChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new Integer[]{1, 3}, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < which.length; i++) {
                            if (i > 0) str.append('\n');
                            str.append(which[i]);
                            str.append(": ");
                            str.append(text[i]);
                        }
                        showToast(str.toString());
                        return true; // allow selection
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.clearSelectedIndices();
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .positiveText(R.string.md_choose_label)
                .autoDismiss(false)
                .neutralText(R.string.clear_selection)
                .show();
    }

    @OnClick(R.id.multiChoiceLimited)
    public void showMultiChoiceLimited() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new Integer[]{1}, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        boolean allowSelection = which.length <= 2; // limit selection to 2, the new selection is included in the which array
                        if (!allowSelection) {
                            showToast(R.string.selection_limit_reached);
                        }
                        return allowSelection;
                    }
                })
                .positiveText(R.string.dismiss)
                .alwaysCallMultiChoiceCallback() // the callback will always be called, to check if selection is still allowed
                .show();
    }

    @OnClick(R.id.multiChoice_longItems)
    public void showMultiChoiceLongItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks_longItems)
                .itemsCallbackMultiChoice(new Integer[]{1, 3}, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < which.length; i++) {
                            if (i > 0) str.append('\n');
                            str.append(which[i]);
                            str.append(": ");
                            str.append(text[i]);
                        }
                        showToast(str.toString());
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();
    }

    @OnClick(R.id.multiChoice_disabledItems)
    public void showMultiChoiceDisabledItems() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new Integer[]{0, 1, 2}, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < which.length; i++) {
                            if (i > 0) str.append('\n');
                            str.append(which[i]);
                            str.append(": ");
                            str.append(text[i]);
                        }
                        showToast(str.toString());
                        return true; // allow selection
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.clearSelectedIndices();
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .positiveText(R.string.md_choose_label)
                .autoDismiss(false)
                .neutralText(R.string.clear_selection)
                .itemsDisabledIndices(0, 1)
                .show();
    }

    @OnClick(R.id.simpleList)
    public void showSimpleList() {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                showToast(item.getContent().toString());
            }
        });
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content("username@gmail.com")
                .icon(R.drawable.ic_account_circle)
                .backgroundColor(Color.WHITE)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content("user02@gmail.com")
                .icon(R.drawable.ic_account_circle)
                .backgroundColor(Color.WHITE)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.add_account)
                .icon(R.drawable.ic_content_add)
                .iconPaddingDp(8)
                .build());

        new MaterialDialog.Builder(this)
                .title(R.string.set_backup)
                .adapter(adapter, null)
                .show();
    }

    @OnClick(R.id.customListItems)
    public void showCustomList() {
        final ButtonItemAdapter adapter = new ButtonItemAdapter(this, R.array.socialNetworks);
        adapter.setCallback(new ButtonItemAdapter.Callback() {
            @Override
            public void onItemClicked(int index) {
                showToast("Item clicked: " + index);
            }

            @Override
            public void onButtonClicked(int index) {
                showToast("Button clicked: " + index);
            }
        });

        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .adapter(adapter, null)
                .show();
    }

    @SuppressWarnings("ResourceAsColor")
    @OnClick(R.id.customView)
    public void showCustomView() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.googleWifi)
                .customView(R.layout.dialog_customview, true)
                .positiveText(R.string.connect)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showToast("Password: " + passwordInput.getText().toString());
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Toggling the show password CheckBox will mask or unmask the password input EditText
        CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
            }
        });

        int widgetColor = ThemeSingleton.get().widgetColor;
        MDTintHelper.setTint(checkbox,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.accent) : widgetColor);

        MDTintHelper.setTint(passwordInput,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.accent) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    @OnClick(R.id.customView_webView)
    public void showCustomWebView() {
        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0)
            accentColor = ContextCompat.getColor(this, R.color.accent);
        ChangelogDialog.create(false, accentColor)
                .show(getSupportFragmentManager(), "changelog");
    }

    @OnClick(R.id.customView_datePicker)
    public void showCustomDatePicker() {
        new MaterialDialog.Builder(this)
                .title(R.string.date_picker)
                .customView(R.layout.dialog_datepicker, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
//                .stackingBehavior(StackingBehavior.ALWAYS)
                .show();
    }

    @OnClick(R.id.colorChooser_primary)
    public void showColorChooserPrimary() {
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.colors)
                .preselect(primaryPreselect)
                .show();
    }

    @OnClick(R.id.colorChooser_accent)
    public void showColorChooserAccent() {
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.colors)
                .accentMode(true)
                .preselect(accentPreselect)
                .show();
    }

    @OnClick(R.id.colorChooser_customColors)
    public void showColorChooserCustomColors() {
        int[][] subColors = new int[][]{
                new int[]{Color.parseColor("#EF5350"), Color.parseColor("#F44336"), Color.parseColor("#E53935")},
                new int[]{Color.parseColor("#EC407A"), Color.parseColor("#E91E63"), Color.parseColor("#D81B60")},
                new int[]{Color.parseColor("#AB47BC"), Color.parseColor("#9C27B0"), Color.parseColor("#8E24AA")},
                new int[]{Color.parseColor("#7E57C2"), Color.parseColor("#673AB7"), Color.parseColor("#5E35B1")},
                new int[]{Color.parseColor("#5C6BC0"), Color.parseColor("#3F51B5"), Color.parseColor("#3949AB")},
                new int[]{Color.parseColor("#42A5F5"), Color.parseColor("#2196F3"), Color.parseColor("#1E88E5")}
        };

        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.colors)
                .preselect(primaryPreselect)
                .customColors(R.array.custom_colors, subColors)
                .show();
    }

    @OnClick(R.id.colorChooser_customColorsNoSub)
    public void showColorChooserCustomColorsNoSub() {
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.colors)
                .preselect(primaryPreselect)
                .customColors(R.array.custom_colors, null)
                .show();
    }

    // Receives callback from color chooser dialog
    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        if (dialog.isAccentMode()) {
            accentPreselect = color;
            ThemeSingleton.get().positiveColor = DialogUtils.getActionTextStateList(this, color);
            ThemeSingleton.get().neutralColor = DialogUtils.getActionTextStateList(this, color);
            ThemeSingleton.get().negativeColor = DialogUtils.getActionTextStateList(this, color);
            ThemeSingleton.get().widgetColor = color;
        } else {
            primaryPreselect = color;
            if (getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
                getWindow().setNavigationBarColor(color);
            }
        }
    }

    @OnClick(R.id.themed)
    public void showThemed() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .positiveColorRes(R.color.material_red_400)
                .negativeColorRes(R.color.material_red_400)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.accent)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .show();
    }

    @OnClick(R.id.showCancelDismiss)
    public void showShowCancelDismissCallbacks() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        showToast("onShow");
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast("onCancel");
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        showToast("onDismiss");
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.file_chooser)
    public void showFileChooser() {
        chooserDialog = R.id.file_chooser;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
            return;
        }
        new FileChooserDialog.Builder(this)
                .show();
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        showToast(file.getAbsolutePath());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.folder_chooser)
    public void showFolderChooser() {
        chooserDialog = R.id.folder_chooser;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_RC);
            return;
        }
        new FolderChooserDialog.Builder(MainActivity.this)
                .chooseButton(R.string.md_choose_label)
                .allowNewFolder(true, 0)
                .show();
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        showToast(folder.getAbsolutePath());
    }

    @OnClick(R.id.input)
    public void showInputDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input)
                .content(R.string.input_content)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText(R.string.submit)
                .input(R.string.input_hint, R.string.input_hint, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        showToast("Hello, " + input.toString() + "!");
                    }
                }).show();
    }

    @OnClick(R.id.input_custominvalidation)
    public void showInputDialogCustomInvalidation() {
        new MaterialDialog.Builder(this)
                .title(R.string.input)
                .content(R.string.input_content_custominvalidation)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(R.string.submit)
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .input(R.string.input_hint, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input.toString().equalsIgnoreCase("hello")) {
                            dialog.setContent("I told you not to type that!");
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.setContent(R.string.input_content_custominvalidation);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }
                    }
                }).show();
    }

    @OnClick(R.id.input_checkPrompt)
    public void showInputDialogCheckPrompt() {
        new MaterialDialog.Builder(this)
                .title(R.string.input)
                .content(R.string.input_content)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText(R.string.submit)
                .input(R.string.input_hint, R.string.input_hint, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        showToast("Hello, " + input.toString() + "!");
                    }
                })
                .checkBoxPromptRes(R.string.example_prompt, true, null)
                .show();
    }

    @OnClick(R.id.progress1)
    public void showProgressDeterminateDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 150, true)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mThread != null)
                            mThread.interrupt();
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                        startThread(new Runnable() {
                            @Override
                            public void run() {
                                while (dialog.getCurrentProgress() != dialog.getMaxProgress() &&
                                        !Thread.currentThread().isInterrupted()) {
                                    if (dialog.isCancelled())
                                        break;
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                    dialog.incrementProgress(1);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mThread = null;
                                        dialog.setContent(getString(R.string.md_done_label));
                                    }
                                });

                            }
                        });
                    }
                }).show();
    }

    @OnClick(R.id.progress2)
    public void showProgressIndeterminateDialog() {
        showIndeterminateProgressDialog(false);
    }

    @OnClick(R.id.progress3)
    public void showProgressHorizontalIndeterminateDialog() {
        showIndeterminateProgressDialog(true);
    }

    private void showIndeterminateProgressDialog(boolean horizontal) {
        new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontal)
                .show();
    }

    @OnClick(R.id.preference_dialogs)
    public void showPreferenceDialogs() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
            startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
        else
            startActivity(new Intent(getApplicationContext(), PreferenceActivityCompat.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            AboutDialog.show(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_RC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(chooserDialog).performClick();
                    }
                }, 1000);
            } else {
                Toast.makeText(this, "The folder or file chooser will not work without permission to read external storage.", Toast.LENGTH_LONG).show();
            }
        }
    }
}