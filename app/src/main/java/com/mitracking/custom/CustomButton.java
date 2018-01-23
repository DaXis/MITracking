package com.mitracking.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends android.support.v7.widget.AppCompatButton {
    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        int style = 0;
        if (!isInEditMode()) {
            if(getTypeface() != null)
                style = getTypeface().getStyle();

            if(style == Typeface.NORMAL) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Ubuntu-R.ttf");
                setTypeface(tf);
            }
            if(style == Typeface.BOLD) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Ubuntu-B.ttf");
                setTypeface(tf);
            }
        }
    }
}
