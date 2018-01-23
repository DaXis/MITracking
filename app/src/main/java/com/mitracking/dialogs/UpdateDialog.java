package com.mitracking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mitracking.R;
import com.mitracking.Singleton;

public class UpdateDialog extends DialogFragment implements View.OnClickListener {

    private TextView download_subText, downloadTitle;
    private ProgressBar progressDownload;
    private Button reintent;
    private boolean up;
    //public int total;

    //public static UpdateDialog newInstance(int arg){
    public static UpdateDialog newInstance(boolean up){
        UpdateDialog updateDialog = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("up", up);
        updateDialog.setArguments(bundle);
        return updateDialog;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo;
        up = getArguments().getBoolean("up");
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.update, null);
        builder.setView(v);

        downloadTitle = (TextView)v.findViewById(R.id.donwloadTitle);//Realizando la carga de vales

        download_subText = (TextView)v.findViewById(R.id.download_subText);
        progressDownload = (ProgressBar)v.findViewById(R.id.progressDownload);
        progressDownload.setMax(100);
        reintent = (Button) v.findViewById(R.id.reintent);
        reintent.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB){
            Log.e("solved super error", "solved super error OK");
        } else
            super.onSaveInstanceState(outState);
    }

    public void updateData(final int arg, final String arg0){
        if(progressDownload != null && download_subText != null) {
            Singleton.getCurrentActivity().runOnUiThread(new Runnable() {
                public void run() {
                    progressDownload.setProgress(arg);
                    download_subText.setText(arg0);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.reintent:
                break;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
