package com.mitracking.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText{

    //private Drawable imgCloseButton = getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        //imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());
        //handleClearButton();

        /*setBackgroundResource(R.drawable.line_bg);
        setTextColor(getResources().getColor(R.color.secondary_text));
        setHintTextColor(getResources().getColor(R.color.text_app));*/

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

        this.setOnTouchListener(new OnTouchListener() {

            @Override

            public boolean onTouch(View v, MotionEvent event) {
                CustomEditText et = CustomEditText.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                /*if (event.getX() > et.getWidth() - et.getPaddingRight() - imgCloseButton.getIntrinsicWidth()) {
                    et.setText("");
                    CustomEditText.this.handleClearButton();
                }*/
                return false;
            }

        });


        this.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CustomEditText.this.handleClearButton();
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

    }

    void handleClearButton() {
        if (this.getText().toString().equals("")) {
            this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1],
                    null, this.getCompoundDrawables()[3]);
        }

        else {
            //this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton,
           //         this.getCompoundDrawables()[3]);
        }
    }

}

