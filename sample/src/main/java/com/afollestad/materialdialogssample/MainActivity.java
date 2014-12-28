package com.afollestad.materialdialogssample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.Alignment;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.File;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends ActionBarActivity implements FolderSelectorDialog.FolderSelectCallback {

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

        findViewById(R.id.complex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComplexListeners();
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
                .icon(R.drawable.ic_launcher)
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
                        Toast.makeText(getApplicationContext(), "Positive!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        Toast.makeText(getApplicationContext(), "Neutral", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Toast.makeText(getApplicationContext(), "Negative…", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), which + ": " + text, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showListNoTitle() {
        new MaterialDialog.Builder(this)
                .items(R.array.states)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Toast.makeText(getApplicationContext(), which + ": " + text, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), which + ": " + text, Toast.LENGTH_SHORT).show();
                    }
                })
                .positiveText(android.R.string.ok)
                .show();
    }

    private void showSingleChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Toast.makeText(getApplicationContext(), which + ": " + text, Toast.LENGTH_SHORT).show();
                    }
                })
//                .positiveText(R.string.choose)
                .show();
    }

    private void showMultiChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new Integer[]{1, 3}, new MaterialDialog.ListCallbackMulti() {
                    @Override
                    public void onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        StringBuilder str = new StringBuilder();
                        for (int i = 0; i < which.length; i++) {
                            str.append(which[i]);
                            str.append(": ");
                            str.append(text[i]);
                            str.append('\n');
                        }
                        Toast.makeText(getApplicationContext(), str.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .positiveText(R.string.choose)

                .show();
    }

    private void showComplexListeners() {
        new MaterialDialog.Builder(this)
                .title(R.string.complex)
                .positiveText("Yes")
                .negativeText("No")
                .neutralText("Maybe")
                .items(R.array.socialNetworks)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Toast.makeText(MainActivity.this, "Clicked " + text, Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        Toast.makeText(MainActivity.this, "Maybe", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Toast.makeText(MainActivity.this, "No", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Toast.makeText(MainActivity.this, "Yes", Toast.LENGTH_SHORT).show();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showCustomList() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .adapter(new ButtonItemAdapter(this, R.array.socialNetworks))
                .build();

        ListView listView = dialog.getListView();
        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "Clicked item " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        dialog.show();
    }


    EditText passwordInput;
    View positiveAction;

    private void showCustomView() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.googleWifi)
                .customView(R.layout.dialog_customview)
                .positiveText(R.string.connect)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Toast.makeText(getApplicationContext(), "Password: " + passwordInput.getText().toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
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
        ((CheckBox) dialog.getCustomView().findViewById(R.id.showPassword)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void showThemed() {
        new MaterialDialog.Builder(this)
                .title(R.string.useGoogleLocationServices)
                .content(R.string.useGoogleLocationServicesPrompt)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .positiveColorRes(R.color.material_red_400)
                .negativeColorRes(R.color.material_red_400)
                .titleAlignment(Alignment.CENTER)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
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
                        Toast.makeText(getApplicationContext(), "onShow", Toast.LENGTH_SHORT).show();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(getApplicationContext(), "onDismiss", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
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
                    .build()
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFolderSelection(File folder) {
        Toast.makeText(this, folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
}
