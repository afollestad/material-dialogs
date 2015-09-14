package com.afollestad.materialdialogssample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.io.File;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AppCompatActivity implements
        FolderSelectorDialog.FolderSelectCallback, ColorChooserDialog.ColorCallback {

    private Toast mToast;
    private Thread mThread;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.basicNoTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasicNoTitle();
            }
        });

        findViewById(R.id.basic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasic();
            }
        });

        findViewById(R.id.basicLongContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasicLongContent();
            }
        });

        findViewById(R.id.basicIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasicIcon();
            }
        });

        findViewById(R.id.stacked).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStacked();
            }
        });

        findViewById(R.id.neutral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNeutral();
            }
        });

        findViewById(R.id.callbacks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallbacks();
            }
        });

        findViewById(R.id.list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

        findViewById(R.id.listNoTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListNoTitle();
            }
        });

        findViewById(R.id.longList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLongList();
            }
        });

        findViewById(R.id.singleChoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChoice();
            }
        });

        findViewById(R.id.multiChoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoice();
            }
        });

        findViewById(R.id.multiChoiceLimited).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoiceLimited();
            }
        });

        findViewById(R.id.simpleList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSimpleList();
            }
        });

        findViewById(R.id.customListItems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomList();
            }
        });

        findViewById(R.id.customView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomView();
            }
        });

        findViewById(R.id.customView_webView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomWebView();
            }
        });

        findViewById(R.id.colorChooser_primary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorChooser(false);
            }
        });

        findViewById(R.id.colorChooser_accent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorChooser(true);
            }
        });

        findViewById(R.id.themed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThemed();
            }
        });

        findViewById(R.id.showCancelDismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShowCancelDismissCallbacks();
            }
        });

        findViewById(R.id.folder_chooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FolderSelectorDialog().show(MainActivity.this);
            }
        });

        findViewById(R.id.input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        findViewById(R.id.input_custominvalidation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogCustomInvalidation();
            }
        });

        findViewById(R.id.progress1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeterminateProgressDialog();
            }
        });

        findViewById(R.id.progress2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIndeterminateProgressDialog(false);
            }
        });

        findViewById(R.id.progress3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIndeterminateProgressDialog(true);
            }
        });

        findViewById(R.id.preference_dialogs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
                    startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(), PreferenceActivityCompat.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mThread != null && !mThread.isInterrupted() && mThread.isAlive())
            mThread.interrupt();
    }

    private void showBasicNoTitle() {
        new MaterialDialog.Builder(this)
                .content(R.string.shareLocationPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    private void showBasic() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    private void showBasicLongContent() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.loremIpsum)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    private void showBasicIcon() {
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.ic_launcher)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .show();
    }

    private void showStacked() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.speedBoost)
                .negativeText(R.string.noThanks)
                .btnStackedGravity(GravityEnum.END)
                .forceStacking(true)  // this generally should not be forced, but is used for demo purposes
                .show();
    }

    private void showNeutral() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .show();
    }

    private void showCallbacks() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .neutralText(R.string.more_info)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        showToast("Positive!");
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        showToast("Neutral");
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        showToast("Negative…");
                    }
                })
                .show();
    }

    private void showList() {
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

    private void showListNoTitle() {
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

    private void showLongList() {
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

    private void showSingleChoice() {
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
                .positiveText(R.string.choose)
                .show();
    }

    private void showMultiChoice() {
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
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        dialog.clearSelectedIndices();
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .positiveText(R.string.choose)
                .autoDismiss(false)
                .neutralText(R.string.clear_selection)
                .show();
    }

    private void showMultiChoiceLimited() {
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

    private void showSimpleList() {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(this);
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content("username@gmail.com")
                .icon(R.drawable.ic_circle_darker)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content("user02@gmail.com")
                .icon(R.drawable.ic_circle_darker)
                .build());
        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.add_account)
                .icon(R.drawable.ic_circle_lighter)
                .build());

        new MaterialDialog.Builder(this)
                .title(R.string.set_backup)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        MaterialSimpleListItem item = adapter.getItem(which);
                        showToast(item.getContent().toString());
                    }
                })
                .show();
    }

    private void showCustomList() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .adapter(new ButtonItemAdapter(this, R.array.socialNetworks),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                showToast("Clicked item " + which);
                            }
                        })
                .show();
    }


    private EditText passwordInput;
    private View positiveAction;

    private void showCustomView() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.googleWifi)
                .customView(R.layout.dialog_customview, true)
                .positiveText(R.string.connect)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        showToast("Password: " + passwordInput.getText().toString());
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
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
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.material_teal_500) : widgetColor);

        MDTintHelper.setTint(passwordInput,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.material_teal_500) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void showCustomWebView() {
        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0)
            accentColor = ContextCompat.getColor(this, R.color.material_teal_500);
        ChangelogDialog.create(false, accentColor)
                .show(getSupportFragmentManager(), "changelog");
    }

    private int primaryPreselect;
    private int accentPreselect;

    private void showColorChooser(boolean accent) {
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.colors)
                .accentMode(accent)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .backButton(R.string.md_back_label)
                .preselect(accent ? accentPreselect : primaryPreselect)
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

    private void showThemed() {
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
                .dividerColorRes(R.color.material_teal_500)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .show();
    }

    private void showShowCancelDismissCallbacks() {
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

    private void showInputDialog() {
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
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        showToast("Hello, " + input.toString() + "!");
                    }
                }).show();
    }

    private void showInputDialogCustomInvalidation() {
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
                    public void onInput(MaterialDialog dialog, CharSequence input) {
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

    private void showIndeterminateProgressDialog(boolean horizontal) {
        new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(horizontal)
                .show();
    }

    private void showDeterminateProgressDialog() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            new MaterialDialog.Builder(this)
                    .title(R.string.about)
                    .positiveText(R.string.dismiss)
                    .content(Html.fromHtml(getString(R.string.about_body)))
                    .contentLineSpacing(1.6f)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFolderSelection(File folder) {
        showToast(folder.getAbsolutePath());
    }
}