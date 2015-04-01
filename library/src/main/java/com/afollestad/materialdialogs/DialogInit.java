package com.afollestad.materialdialogs;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.afollestad.materialdialogs.util.TypefaceHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Used by MaterialDialog while initializing the dialog. Offloads some of the code to make the main class
 * cleaner and easier to read/maintain.
 *
 * @author Aidan Follestad (afollestad)
 */
class DialogInit {

    public static ContextThemeWrapper getTheme(MaterialDialog.Builder builder) {
        TypedArray a = builder.context.getTheme().obtainStyledAttributes(new int[]{R.attr.md_dark_theme});
        boolean darkTheme = builder.theme == Theme.DARK;
        if (!darkTheme) {
            try {
                darkTheme = a.getBoolean(0, false);
                builder.theme = darkTheme ? Theme.DARK : Theme.LIGHT;
            } finally {
                a.recycle();
            }
        }
        return new ContextThemeWrapper(builder.context, darkTheme ? R.style.MD_Dark : R.style.MD_Light);
    }

    public static int getInflateLayout(MaterialDialog.Builder builder) {
        if (builder.customView != null) {
            return R.layout.md_dialog_custom;
        } else if (builder.items != null && builder.items.length > 0 || builder.adapter != null) {
            return R.layout.md_dialog_list;
        } else if (builder.mProgress > -2) {
            return R.layout.md_dialog_progress;
        } else if (builder.mIndeterminateProgress) {
            return R.layout.md_dialog_progress_indeterminate;
        } else {
            return R.layout.md_dialog_basic;
        }
    }

    public static void init(final MaterialDialog dialog) {
        final MaterialDialog.Builder builder = dialog.mBuilder;

        // Check if default library fonts should be used
        if (!builder.useCustomFonts) {
            if (builder.mediumFont == null)
                builder.mediumFont = TypefaceHelper.get(dialog.getContext(), "Roboto-Medium");
            if (builder.regularFont == null)
                builder.regularFont = TypefaceHelper.get(dialog.getContext(), "Roboto-Regular");
        }

        // Set cancelable flag and dialog background color
        dialog.setCancelable(builder.cancelable);
        if (builder.backgroundColor == 0)
            builder.backgroundColor = DialogUtils.resolveColor(builder.context, R.attr.md_background_color);
        if (builder.backgroundColor != 0)
            dialog.view.setBackgroundColor(builder.backgroundColor);

        // Retrieve action button colors from theme attributes or the Builder
        builder.positiveColor = DialogUtils.resolveColor(builder.context, R.attr.md_positive_color, builder.positiveColor);
        builder.neutralColor = DialogUtils.resolveColor(builder.context, R.attr.md_neutral_color, builder.neutralColor);
        builder.negativeColor = DialogUtils.resolveColor(builder.context, R.attr.md_negative_color, builder.negativeColor);

        // Retrieve references to views
        dialog.title = (TextView) dialog.view.findViewById(R.id.title);
        dialog.icon = (ImageView) dialog.view.findViewById(R.id.icon);
        dialog.titleFrame = dialog.view.findViewById(R.id.titleFrame);
        dialog.content = (TextView) dialog.view.findViewById(R.id.content);
        dialog.listView = (ListView) dialog.view.findViewById(R.id.contentListView);

        // Button views initially used by checkIfStackingNeeded()
        dialog.positiveButton = dialog.view.findViewById(R.id.buttonDefaultPositive);
        dialog.neutralButton = dialog.view.findViewById(R.id.buttonDefaultNeutral);
        dialog.negativeButton = dialog.view.findViewById(R.id.buttonDefaultNegative);

        // Set up the initial visibility of action buttons based on whether or not text was set
        dialog.positiveButton.setVisibility(builder.positiveText != null ? View.VISIBLE : View.GONE);
        dialog.neutralButton.setVisibility(builder.neutralText != null ? View.VISIBLE : View.GONE);
        dialog.negativeButton.setVisibility(builder.negativeText != null ? View.VISIBLE : View.GONE);

        // Setup icon
        if (builder.icon != null) {
            dialog.icon.setVisibility(View.VISIBLE);
            dialog.icon.setImageDrawable(builder.icon);
        } else {
            Drawable d = DialogUtils.resolveDrawable(builder.context, R.attr.md_icon);
            if (d != null) {
                dialog.icon.setVisibility(View.VISIBLE);
                dialog.icon.setImageDrawable(d);
            } else {
                dialog.icon.setVisibility(View.GONE);
            }
        }
        // Setup icon size limiting
        int maxIconSize = builder.maxIconSize;
        if (maxIconSize == -1)
            maxIconSize = DialogUtils.resolveDimension(builder.context, R.attr.md_icon_max_size);
        if (builder.limitIconToDefaultSize || DialogUtils.resolveBoolean(builder.context, R.attr.md_icon_limit_icon_to_default_size))
            maxIconSize = builder.context.getResources().getDimensionPixelSize(R.dimen.md_icon_max_size);
        if (maxIconSize > -1) {
            dialog.icon.setAdjustViewBounds(true);
            dialog.icon.setMaxHeight(maxIconSize);
            dialog.icon.setMaxWidth(maxIconSize);
            dialog.icon.requestLayout();
        }

        // Setup title and title frame
        if (builder.title == null) {
            dialog.titleFrame.setVisibility(View.GONE);
        } else {
            dialog.title.setText(builder.title);
            dialog.setTypeface(dialog.title, builder.mediumFont);
            if (builder.titleColorSet) {
                dialog.title.setTextColor(builder.titleColor);
            } else {
                final int fallback = DialogUtils.resolveColor(dialog.getContext(), android.R.attr.textColorPrimary);
                dialog.title.setTextColor(DialogUtils.resolveColor(dialog.getContext(), R.attr.md_title_color, fallback));
            }
            dialog.title.setGravity(MaterialDialog.gravityEnumToGravity(builder.titleGravity));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                dialog.title.setTextAlignment(MaterialDialog.gravityEnumToTextAlignment(builder.titleGravity));
            }
        }

        // Setup content
        if (dialog.content != null && builder.content != null) {
            dialog.content.setText(builder.content);
            dialog.content.setMovementMethod(new LinkMovementMethod());
            dialog.setTypeface(dialog.content, builder.regularFont);
            dialog.content.setLineSpacing(0f, builder.contentLineSpacingMultiplier);
            if (builder.positiveColor == 0) {
                dialog.content.setLinkTextColor(DialogUtils.resolveColor(dialog.getContext(), android.R.attr.textColorPrimary));
            } else {
                dialog.content.setLinkTextColor(builder.positiveColor);
            }
            dialog.content.setGravity(MaterialDialog.gravityEnumToGravity(builder.contentGravity));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                dialog.content.setTextAlignment(MaterialDialog.gravityEnumToTextAlignment(builder.contentGravity));
            }
            if (builder.contentColorSet) {
                dialog.content.setTextColor(builder.contentColor);
            } else {
                final int fallback = DialogUtils.resolveColor(dialog.getContext(), android.R.attr.textColorSecondary);
                final int contentColor = DialogUtils.resolveColor(dialog.getContext(), R.attr.md_content_color, fallback);
                dialog.content.setTextColor(contentColor);
            }
        } else if (dialog.content != null) {
            dialog.content.setVisibility(View.GONE);
        }

        // Load default list item text color
        if (builder.itemColorSet) {
            dialog.defaultItemColor = builder.itemColor;
        } else if (builder.theme == Theme.LIGHT) {
            dialog.defaultItemColor = Color.BLACK;
        } else {
            dialog.defaultItemColor = Color.WHITE;
        }

        // Setup list dialog stuff
        if (builder.listCallbackMultiChoice != null)
            dialog.selectedIndicesList = new ArrayList<>();
        if (dialog.listView != null && (builder.items != null && builder.items.length > 0 || builder.adapter != null)) {
            dialog.listView.setSelector(dialog.getListSelector());

            if (builder.title != null) {
                // Cancel out top padding if there's a title
                dialog.listView.setPadding(dialog.listView.getPaddingLeft(), 0,
                        dialog.listView.getPaddingRight(), dialog.listView.getPaddingBottom());
            }
            if (dialog.hasActionButtons()) {
                // No bottom padding if there's action buttons
                dialog.listView.setPadding(dialog.listView.getPaddingLeft(), 0,
                        dialog.listView.getPaddingRight(), 0);
            }

            // No custom adapter specified, setup the list with a MaterialDialogAdapter.
            // Which supports regular lists and single/multi choice dialogs.
            if (builder.adapter == null) {
                // Determine list type
                if (builder.listCallbackSingleChoice != null) {
                    dialog.listType = MaterialDialog.ListType.SINGLE;
                } else if (builder.listCallbackMultiChoice != null) {
                    dialog.listType = MaterialDialog.ListType.MULTI;
                    if (builder.selectedIndices != null) {
                        dialog.selectedIndicesList = new ArrayList<>(Arrays.asList(builder.selectedIndices));
                    }
                } else {
                    dialog.listType = MaterialDialog.ListType.REGULAR;
                }
                builder.adapter = new MaterialDialogAdapter(dialog,
                        MaterialDialog.ListType.getLayoutForType(dialog.listType), R.id.title, builder.items);
            }
        }

        // Setup progress dialog stuff if needed
        setupProgressDialog(dialog);

        if (builder.customView != null) {
            dialog.invalidateCustomViewAssociations();
            FrameLayout frame = (FrameLayout) dialog.view.findViewById(R.id.customViewFrame);
            dialog.customViewFrame = frame;
            View innerView = builder.customView;
            if (builder.wrapCustomViewInScroll) {
                /* Apply the frame padding to the content, this allows the ScrollView to draw it's
                   overscroll glow without clipping */
                final Resources r = dialog.getContext().getResources();
                final int framePadding = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                final ScrollView sv = new ScrollView(dialog.getContext());
                int paddingTop;
                int paddingBottom;
                if (dialog.titleFrame.getVisibility() != View.GONE)
                    paddingTop = r.getDimensionPixelSize(R.dimen.md_content_vertical_padding);
                else
                    paddingTop = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                if (dialog.hasActionButtons())
                    paddingBottom = r.getDimensionPixelSize(R.dimen.md_content_vertical_padding);
                else
                    paddingBottom = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                sv.setClipToPadding(false);
                if (innerView instanceof EditText) {
                    // Setting padding to an EditText causes visual errors, set it to the parent instead
                    sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom);
                } else {
                    // Setting padding to scroll view pushes the scroll bars out, don't do it if not necessary (like above)
                    sv.setPadding(0, paddingTop, 0, paddingBottom);
                    innerView.setPadding(framePadding, 0, framePadding, 0);
                }
                sv.addView(innerView, new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                innerView = sv;
            }
            frame.addView(innerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            dialog.invalidateCustomViewAssociations();
        }

        // Setup user listeners
        if (builder.showListener != null)
            dialog.setOnShowListener(builder.showListener);
        if (builder.cancelListener != null)
            dialog.setOnCancelListener(builder.cancelListener);
        if (builder.dismissListener != null)
            dialog.setOnDismissListener(builder.dismissListener);
        if (builder.keyListener != null)
            dialog.setOnKeyListener(builder.keyListener);

        // Other internal initialization
        dialog.updateFramePadding();
        dialog.invalidateList();
        dialog._setOnShowListenerInternal();
        dialog._setViewInternal(dialog.view);
        dialog.checkIfListInitScroll();
        dialog.view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (dialog.view.getMeasuredWidth() > 0) {
                            dialog.invalidateCustomViewAssociations();
                        }
                    }
                });

        // Gingerbread compatibility stuff
        if (builder.theme == Theme.LIGHT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            dialog.setInverseBackgroundForced(true);
            if (!builder.titleColorSet)
                dialog.title.setTextColor(Color.BLACK);
            if (!builder.contentColorSet && dialog.content != null)
                dialog.content.setTextColor(Color.BLACK);
        }
    }

    private static void setupProgressDialog(final MaterialDialog dialog) {
        final MaterialDialog.Builder builder = dialog.mBuilder;
        if (builder.mIndeterminateProgress || builder.mProgress > -2) {
            dialog.mProgress = (ProgressBar) dialog.view.findViewById(android.R.id.progress);

            // Manually color progress bar on pre-Lollipop, since Material/AppCompat themes only do it on API 21+
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable indDraw = dialog.mProgress.getIndeterminateDrawable();
                if (indDraw != null) {
                    indDraw.setColorFilter(builder.accentColor, PorterDuff.Mode.SRC_ATOP);
                    dialog.mProgress.setIndeterminateDrawable(indDraw);
                }
                Drawable regDraw = dialog.mProgress.getProgressDrawable();
                if (regDraw != null) {
                    regDraw.setColorFilter(builder.accentColor, PorterDuff.Mode.SRC_ATOP);
                    dialog.mProgress.setProgressDrawable(regDraw);
                }
            }

            if (!builder.mIndeterminateProgress) {
                dialog.mProgress.setProgress(0);
                dialog.mProgress.setMax(builder.mProgressMax);
                dialog.mProgressLabel = (TextView) dialog.view.findViewById(R.id.label);
                dialog.mProgressMinMax = (TextView) dialog.view.findViewById(R.id.minMax);
                if (builder.mShowMinMax) {
                    dialog.mProgressMinMax.setVisibility(View.VISIBLE);
                    dialog.mProgressMinMax.setText("0/" + builder.mProgressMax);
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) dialog.mProgress.getLayoutParams();
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                } else {
                    dialog.mProgressMinMax.setVisibility(View.GONE);
                }
                dialog.mProgressLabel.setText("0%");
            }

            if (builder.title == null) {
                // Redistribute main frame's bottom padding to the top padding if there's no title
                final View mainFrame = dialog.view.findViewById(R.id.mainFrame);
                mainFrame.setPadding(mainFrame.getPaddingLeft(),
                        mainFrame.getPaddingBottom(),
                        mainFrame.getPaddingRight(),
                        mainFrame.getPaddingBottom());
            }
        }
    }
}