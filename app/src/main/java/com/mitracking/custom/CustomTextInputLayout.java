package com.mitracking.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.util.Log;
import com.mitracking.R;
import com.mitracking.Singleton;

public class CustomTextInputLayout extends TextInputLayout {

    private int defStyle = R.style.MyEditTextTheme;

    public CustomTextInputLayout(Context context) {
        super(context);
        init();
    }

    public CustomTextInputLayout (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        int style = 0;
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

        //Log.d("getCurrentApiVersion", ""+Singleton.getCurrentApiVersion());
        if(Singleton.getCurrentApiVersion() >= Build.VERSION_CODES.M){
            setHintTextAppearance(defStyle);
        } else {
            //setHintTextAppearance(defStyle);
        }

    }

}
