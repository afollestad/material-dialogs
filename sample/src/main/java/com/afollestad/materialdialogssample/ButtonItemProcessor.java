package com.afollestad.materialdialogssample;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.list.ItemProcessor;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ButtonItemProcessor extends ItemProcessor implements View.OnClickListener {

    private Toast mToast;

    public ButtonItemProcessor(Context context) {
        super(context);
    }

    @Override
    protected int getLayout(int forIndex) {
        // Returning 0 would use the default list item layout
        return R.layout.dialog_customlistitem;
    }

    @Override
    protected void onViewInflated(int forIndex, String itemText, View view) {
        ((TextView) view.findViewById(R.id.title)).setText(itemText + " (" + forIndex + ")");
        Button button = (Button) view.findViewById(R.id.button);
        button.setTag(forIndex);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getContext(), "Clicked button " + index, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
