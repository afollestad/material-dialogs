package com.afollestad.materialdialogssample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.Alignment;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.basic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBasic();
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
    }

    private void showBasic() {
        new MaterialDialog.Builder(this)
                .title(R.string.permissions)
                .content(R.string.permissionsContent)
                .positiveText(R.string.accept)  // the default is 'Accept', this line could be left out
                .negativeText(R.string.decline)  // leaving this line out will remove the negative button
                .build()
                .show();
    }

    private void showStacked() {
        new MaterialDialog.Builder(this)
                .title(R.string.permissions)
                .content(R.string.permissionsContent)
                .positiveText(R.string.speedBoost)
                .negativeText(R.string.noThanks)
                .build()
                .show();
    }

    private void showNeutral() {
        new MaterialDialog.Builder(this)
                .title(R.string.permissions)
                .content(R.string.permissionsContent)
                .positiveText(R.string.accept)
                .negativeText(R.string.decline)
                .neutralText(R.string.more_info)
                .build()
                .show();
    }

    private void showCallbacks() {
        new MaterialDialog.Builder(this)
                .title(R.string.permissions)
                .content(R.string.permissionsContent)
                .positiveText(R.string.accept)
                .negativeText(R.string.decline)
                .neutralText(R.string.more_info)
                .callback(new MaterialDialog.FullCallback() {
                    @Override
                    public void onPositive() {
                        Toast.makeText(getApplicationContext(), "Positive!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNeutral() {
                        Toast.makeText(getApplicationContext(), "Neutral", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegative() {
                        Toast.makeText(getApplicationContext(), "Negative…", Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    private void showList() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(int which, String text) {
                        Toast.makeText(getApplicationContext(), which + ": " + text, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
    }

    private void showSingleChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackSingleChoice(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(int which, String text) {
                        Toast.makeText(getApplicationContext(), which + ": " + text, Toast.LENGTH_SHORT).show();
                    }
                })
                .positiveText(R.string.choose)
                .build()
                .show();
    }

    private void showMultiChoice() {
        new MaterialDialog.Builder(this)
                .title(R.string.socialNetworks)
                .items(R.array.socialNetworks)
                .itemsCallbackMultiChoice(new MaterialDialog.ListCallbackMulti() {
                    @Override
                    public void onSelection(Integer[] which, String[] text) {
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
                .positiveText("Choose")
                .build()
                .show();
    }

    private void showCustomView() {
        new MaterialDialog.Builder(this)
                .title(R.string.googleWifi)
                .positiveText(R.string.accept)
                .customView(R.layout.dialog_customview)
                .positiveText(R.string.connect)
                .build()
                .show();
    }

    private void showThemed() {
        new MaterialDialog.Builder(this)
                .title(R.string.permissions)
                .content(R.string.permissionsContent)
                .positiveText(R.string.accept)
                .negativeText(R.string.decline)
                .positiveColor(R.color.material_red_500)
                .titleAlignment(Alignment.CENTER)
                .titleColor(R.color.material_red_500)
                .theme(Theme.DARK)
                .build()
                .show();
    }
}
