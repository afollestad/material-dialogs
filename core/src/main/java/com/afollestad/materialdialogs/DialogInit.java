package com.afollestad.materialdialogs;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.util.ArrayList;
import java.util.Arrays;

import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

/**
 * Used by MaterialDialog while initializing the dialog. Offloads some of the code to make the main class
 * cleaner and easier to read/maintain.
 *
 * @author Aidan Follestad (afollestad)
 */
class DialogInit {

    @StyleRes
    public static int getTheme(@NonNull MaterialDialog.Builder builder) {
        boolean darkTheme = DialogUtils.resolveBoolean(builder.context, R.attr.md_dark_theme, builder.theme == Theme.DARK);
        builder.theme = darkTheme ? Theme.DARK : Theme.LIGHT;
        return darkTheme ? R.style.MD_Dark : R.style.MD_Light;
    }

    @LayoutRes
    public static int getInflateLayout(MaterialDialog.Builder builder) {
        if (builder.customView != null) {
            return R.layout.md_dialog_custom;
        } else if (builder.items != null && builder.items.size() > 0 || builder.adapter != null) {
            if (builder.checkBoxPrompt != null)
                return R.layout.md_dialog_list_check;
            return R.layout.md_dialog_list;
        } else if (builder.progress > -2) {
            return R.layout.md_dialog_progress;
        } else if (builder.indeterminateProgress) {
            if (builder.indeterminateIsHorizontalProgress)
                return R.layout.md_dialog_progress_indeterminate_horizontal;
            return R.layout.md_dialog_progress_indeterminate;
        } else if (builder.inputCallback != null) {
            if (builder.checkBoxPrompt != null)
                return R.layout.md_dialog_input_check;
            return R.layout.md_dialog_input;
        } else if (builder.checkBoxPrompt != null) {
            return R.layout.md_dialog_basic_check;
        } else {
            return R.layout.md_dialog_basic;
        }
    }

    @UiThread
    public static void init(final MaterialDialog dialog) {
        final MaterialDialog.Builder builder = dialog.mBuilder;

        // Set cancelable flag and dialog background color
        dialog.setCancelable(builder.cancelable);
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        if (builder.backgroundColor == 0)
            builder.backgroundColor = DialogUtils.resolveColor(builder.context, R.attr.md_background_color,
                    DialogUtils.resolveColor(dialog.getContext(), R.attr.colorBackgroundFloating));
        if (builder.backgroundColor != 0) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(builder.context.getResources().getDimension(R.dimen.md_bg_corner_radius));
            drawable.setColor(builder.backgroundColor);
            DialogUtils.setBackgroundCompat(dialog.view, drawable);
        }

        // Retrieve color theme attributes
        if (!builder.positiveColorSet)
            builder.positiveColor = DialogUtils.resolveActionTextColorStateList(builder.context, R.attr.md_positive_color, builder.positiveColor);
        if (!builder.neutralColorSet)
            builder.neutralColor = DialogUtils.resolveActionTextColorStateList(builder.context, R.attr.md_neutral_color, builder.neutralColor);
        if (!builder.negativeColorSet)
            builder.negativeColor = DialogUtils.resolveActionTextColorStateList(builder.context, R.attr.md_negative_color, builder.negativeColor);
        if (!builder.widgetColorSet)
            builder.widgetColor = DialogUtils.resolveColor(builder.context, R.attr.md_widget_color, builder.widgetColor);

        // Retrieve default title/content colors
        if (!builder.titleColorSet) {
            final int titleColorFallback = DialogUtils.resolveColor(dialog.getContext(), android.R.attr.textColorPrimary);
            builder.titleColor = DialogUtils.resolveColor(builder.context, R.attr.md_title_color, titleColorFallback);
        }
        if (!builder.contentColorSet) {
            final int contentColorFallback = DialogUtils.resolveColor(dialog.getContext(), android.R.attr.textColorSecondary);
            builder.contentColor = DialogUtils.resolveColor(builder.context, R.attr.md_content_color, contentColorFallback);
        }
        if (!builder.itemColorSet)
            builder.itemColor = DialogUtils.resolveColor(builder.context, R.attr.md_item_color, builder.contentColor);

        // Retrieve references to views
        dialog.title = (TextView) dialog.view.findViewById(R.id.md_title);
        dialog.icon = (ImageView) dialog.view.findViewById(R.id.md_icon);
        dialog.titleFrame = dialog.view.findViewById(R.id.md_titleFrame);
        dialog.content = (TextView) dialog.view.findViewById(R.id.md_content);
        dialog.recyclerView = (RecyclerView) dialog.view.findViewById(R.id.md_contentRecyclerView);
        dialog.checkBoxPrompt = (CheckBox) dialog.view.findViewById(R.id.md_promptCheckbox);

        // Button views initially used by checkIfStackingNeeded()
        dialog.positiveButton = (MDButton) dialog.view.findViewById(R.id.md_buttonDefaultPositive);
        dialog.neutralButton = (MDButton) dialog.view.findViewById(R.id.md_buttonDefaultNeutral);
        dialog.negativeButton = (MDButton) dialog.view.findViewById(R.id.md_buttonDefaultNegative);

        // Don't allow the submit button to not be shown for input dialogs
        if (builder.inputCallback != null && builder.positiveText == null)
            builder.positiveText = builder.context.getText(android.R.string.ok);

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

        // Setup divider color in case content scrolls
        if (!builder.dividerColorSet) {
            final int dividerFallback = DialogUtils.resolveColor(dialog.getContext(), R.attr.md_divider);
            builder.dividerColor = DialogUtils.resolveColor(builder.context, R.attr.md_divider_color, dividerFallback);
        }
        dialog.view.setDividerColor(builder.dividerColor);

        // Setup title and title frame
        if (dialog.title != null) {
            dialog.setTypeface(dialog.title, builder.mediumFont);
            dialog.title.setTextColor(builder.titleColor);
            dialog.title.setGravity(builder.titleGravity.getGravityInt());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                dialog.title.setTextAlignment(builder.titleGravity.getTextAlignment());
            }

            if (builder.title == null) {
                dialog.titleFrame.setVisibility(View.GONE);
            } else {
                dialog.title.setText(builder.title);
                dialog.titleFrame.setVisibility(View.VISIBLE);
            }
        }

        // Setup content
        if (dialog.content != null) {
            dialog.content.setMovementMethod(new LinkMovementMethod());
            dialog.setTypeface(dialog.content, builder.regularFont);
            dialog.content.setLineSpacing(0f, builder.contentLineSpacingMultiplier);
            if (builder.linkColor == null)
                dialog.content.setLinkTextColor(DialogUtils.resolveColor(dialog.getContext(), android.R.attr.textColorPrimary));
            else dialog.content.setLinkTextColor(builder.linkColor);
            dialog.content.setTextColor(builder.contentColor);
            dialog.content.setGravity(builder.contentGravity.getGravityInt());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                dialog.content.setTextAlignment(builder.contentGravity.getTextAlignment());
            }

            if (builder.content != null) {
                dialog.content.setText(builder.content);
                dialog.content.setVisibility(View.VISIBLE);
            } else {
                dialog.content.setVisibility(View.GONE);
            }
        }

        // Setup prompt checkbox
        if (dialog.checkBoxPrompt != null) {
            dialog.checkBoxPrompt.setText(builder.checkBoxPrompt);
            dialog.checkBoxPrompt.setChecked(builder.checkBoxPromptInitiallyChecked);
            dialog.checkBoxPrompt.setOnCheckedChangeListener(builder.checkBoxPromptListener);
            dialog.setTypeface(dialog.checkBoxPrompt, builder.regularFont);
            dialog.checkBoxPrompt.setTextColor(builder.contentColor);
            MDTintHelper.setTint(dialog.checkBoxPrompt, builder.widgetColor);
        }

        // Setup action buttons
        dialog.view.setButtonGravity(builder.buttonsGravity);
        dialog.view.setButtonStackedGravity(builder.btnStackedGravity);
        dialog.view.setStackingBehavior(builder.stackingBehavior);
        boolean textAllCaps;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            textAllCaps = DialogUtils.resolveBoolean(builder.context, android.R.attr.textAllCaps, true);
            if (textAllCaps)
                textAllCaps = DialogUtils.resolveBoolean(builder.context, R.attr.textAllCaps, true);
        } else {
            textAllCaps = DialogUtils.resolveBoolean(builder.context, R.attr.textAllCaps, true);
        }

        MDButton positiveTextView = dialog.positiveButton;
        dialog.setTypeface(positiveTextView, builder.mediumFont);
        positiveTextView.setAllCapsCompat(textAllCaps);
        positiveTextView.setText(builder.positiveText);
        positiveTextView.setTextColor(builder.positiveColor);
        dialog.positiveButton.setStackedSelector(dialog.getButtonSelector(DialogAction.POSITIVE, true));
        dialog.positiveButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.POSITIVE, false));
        dialog.positiveButton.setTag(DialogAction.POSITIVE);
        dialog.positiveButton.setOnClickListener(dialog);
        dialog.positiveButton.setVisibility(View.VISIBLE);

        MDButton negativeTextView = dialog.negativeButton;
        dialog.setTypeface(negativeTextView, builder.mediumFont);
        negativeTextView.setAllCapsCompat(textAllCaps);
        negativeTextView.setText(builder.negativeText);
        negativeTextView.setTextColor(builder.negativeColor);
        dialog.negativeButton.setStackedSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, true));
        dialog.negativeButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, false));
        dialog.negativeButton.setTag(DialogAction.NEGATIVE);
        dialog.negativeButton.setOnClickListener(dialog);
        dialog.negativeButton.setVisibility(View.VISIBLE);

        MDButton neutralTextView = dialog.neutralButton;
        dialog.setTypeface(neutralTextView, builder.mediumFont);
        neutralTextView.setAllCapsCompat(textAllCaps);
        neutralTextView.setText(builder.neutralText);
        neutralTextView.setTextColor(builder.neutralColor);
        dialog.neutralButton.setStackedSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, true));
        dialog.neutralButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, false));
        dialog.neutralButton.setTag(DialogAction.NEUTRAL);
        dialog.neutralButton.setOnClickListener(dialog);
        dialog.neutralButton.setVisibility(View.VISIBLE);

        // Setup list dialog stuff
        if (builder.listCallbackMultiChoice != null)
            dialog.selectedIndicesList = new ArrayList<>();
        if (dialog.recyclerView != null) {
            if (builder.adapter == null) {
                // Determine list type
                if (builder.listCallbackSingleChoice != null) {
                    dialog.listType = MaterialDialog.ListType.SINGLE;
                } else if (builder.listCallbackMultiChoice != null) {
                    dialog.listType = MaterialDialog.ListType.MULTI;
                    if (builder.selectedIndices != null) {
                        dialog.selectedIndicesList = new ArrayList<>(Arrays.asList(builder.selectedIndices));
                        builder.selectedIndices = null;
                    }
                } else {
                    dialog.listType = MaterialDialog.ListType.REGULAR;
                }
                builder.adapter = new DefaultRvAdapter(dialog,
                        MaterialDialog.ListType.getLayoutForType(dialog.listType));
            } else if (builder.adapter instanceof MDAdapter) {
                // Notify simple list adapter of the dialog it belongs to
                ((MDAdapter) builder.adapter).setDialog(dialog);
            }
        }

        // Setup progress dialog stuff if needed
        setupProgressDialog(dialog);

        // Setup input dialog stuff if needed
        setupInputDialog(dialog);

        // Setup custom views
        if (builder.customView != null) {
            ((MDRootLayout) dialog.view.findViewById(R.id.md_root)).noTitleNoPadding();
            FrameLayout frame = (FrameLayout) dialog.view.findViewById(R.id.md_customViewFrame);
            dialog.customViewFrame = frame;
            View innerView = builder.customView;
            if (innerView.getParent() != null)
                ((ViewGroup) innerView.getParent()).removeView(innerView);
            if (builder.wrapCustomViewInScroll) {
                /* Apply the frame padding to the content, this allows the ScrollView to draw it's
                   over scroll glow without clipping */
                final Resources r = dialog.getContext().getResources();
                final int framePadding = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                final ScrollView sv = new ScrollView(dialog.getContext());
                int paddingTop = r.getDimensionPixelSize(R.dimen.md_content_padding_top);
                int paddingBottom = r.getDimensionPixelSize(R.dimen.md_content_padding_bottom);
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

        // Setup internal show listener
        dialog.setOnShowListenerInternal();

        // Other internal initialization
        dialog.invalidateList();
        dialog.setViewInternal(dialog.view);
        dialog.checkIfListInitScroll();
    }

    private static void fixCanvasScalingWhenHardwareAccelerated(ProgressBar pb) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Canvas scaling when hardware accelerated results in artifacts on older API levels, so
            // we need to use software rendering
            if (pb.isHardwareAccelerated() && pb.getLayerType() != View.LAYER_TYPE_SOFTWARE) {
                pb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    private static void setupProgressDialog(final MaterialDialog dialog) {
        final MaterialDialog.Builder builder = dialog.mBuilder;
        if (builder.indeterminateProgress || builder.progress > -2) {
            dialog.mProgress = (ProgressBar) dialog.view.findViewById(android.R.id.progress);
            if (dialog.mProgress == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (builder.indeterminateProgress) {
                    if (builder.indeterminateIsHorizontalProgress) {
                        IndeterminateHorizontalProgressDrawable d = new IndeterminateHorizontalProgressDrawable(builder.getContext());
                        d.setTint(builder.widgetColor);
                        dialog.mProgress.setProgressDrawable(d);
                        dialog.mProgress.setIndeterminateDrawable(d);
                    } else {
                        IndeterminateProgressDrawable d = new IndeterminateProgressDrawable(builder.getContext());
                        d.setTint(builder.widgetColor);
                        dialog.mProgress.setProgressDrawable(d);
                        dialog.mProgress.setIndeterminateDrawable(d);
                    }
                } else {
                    HorizontalProgressDrawable d = new HorizontalProgressDrawable(builder.getContext());
                    d.setTint(builder.widgetColor);
                    dialog.mProgress.setProgressDrawable(d);
                    dialog.mProgress.setIndeterminateDrawable(d);
                }
            } else {
                MDTintHelper.setTint(dialog.mProgress, builder.widgetColor);
            }

            if (!builder.indeterminateProgress || builder.indeterminateIsHorizontalProgress) {
                dialog.mProgress.setIndeterminate(builder.indeterminateIsHorizontalProgress);
                dialog.mProgress.setProgress(0);
                dialog.mProgress.setMax(builder.progressMax);
                dialog.mProgressLabel = (TextView) dialog.view.findViewById(R.id.md_label);
                if (dialog.mProgressLabel != null) {
                    dialog.mProgressLabel.setTextColor(builder.contentColor);
                    dialog.setTypeface(dialog.mProgressLabel, builder.mediumFont);
                    dialog.mProgressLabel.setText(builder.progressPercentFormat.format(0));
                }
                dialog.mProgressMinMax = (TextView) dialog.view.findViewById(R.id.md_minMax);
                if (dialog.mProgressMinMax != null) {
                    dialog.mProgressMinMax.setTextColor(builder.contentColor);
                    dialog.setTypeface(dialog.mProgressMinMax, builder.regularFont);

                    if (builder.showMinMax) {
                        dialog.mProgressMinMax.setVisibility(View.VISIBLE);
                        dialog.mProgressMinMax.setText(String.format(builder.progressNumberFormat,
                                0, builder.progressMax));
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) dialog.mProgress.getLayoutParams();
                        lp.leftMargin = 0;
                        lp.rightMargin = 0;
                    } else {
                        dialog.mProgressMinMax.setVisibility(View.GONE);
                    }
                } else {
                    builder.showMinMax = false;
                }
            }
        }

        if (dialog.mProgress != null) {
            fixCanvasScalingWhenHardwareAccelerated(dialog.mProgress);
        }
    }

    private static void setupInputDialog(final MaterialDialog dialog) {
        final MaterialDialog.Builder builder = dialog.mBuilder;
        dialog.input = (EditText) dialog.view.findViewById(android.R.id.input);
        if (dialog.input == null) return;
        dialog.setTypeface(dialog.input, builder.regularFont);
        if (builder.inputPrefill != null)
            dialog.input.setText(builder.inputPrefill);
        dialog.setInternalInputCallback();
        dialog.input.setHint(builder.inputHint);
        dialog.input.setSingleLine();
        dialog.input.setTextColor(builder.contentColor);
        dialog.input.setHintTextColor(DialogUtils.adjustAlpha(builder.contentColor, 0.3f));
        MDTintHelper.setTint(dialog.input, dialog.mBuilder.widgetColor);

        if (builder.inputType != -1) {
            dialog.input.setInputType(builder.inputType);
            if (builder.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD &&
                    (builder.inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // If the flags contain TYPE_TEXT_VARIATION_PASSWORD, apply the password transformation method automatically
                dialog.input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        dialog.inputMinMax = (TextView) dialog.view.findViewById(R.id.md_minMax);
        if (builder.inputMinLength > 0 || builder.inputMaxLength > -1) {
            dialog.invalidateInputMinMaxIndicator(dialog.input.getText().toString().length(),
                    !builder.inputAllowEmpty);
        } else {
            dialog.inputMinMax.setVisibility(View.GONE);
            dialog.inputMinMax = null;
        }
    }
}
