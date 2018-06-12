package com.mitracking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mitracking.MainActivity;
import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.adapter.ConfigAdapter;
import com.mitracking.utils.Constants;

public class ConfigFragment extends Fragment {

    private int lay;
    private ListView config;
    private ConfigAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("setCurrentFragment", this.getClass().getSimpleName());
        Singleton.setCurrentFragment(this);
        Singleton.getToolbar().setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.config, container, false);

        config = (ListView)rootView.findViewById(R.id.config);
        if(Singleton.getSettings().getString(Constants.ShowAdvanceConfig_TAG, "").equals("TRUE")){
            adapter = new ConfigAdapter(ConfigFragment.this, ((MainActivity)Singleton.getCurrentActivity()).getArray());
            config.setAdapter(adapter);
        }

        return rootView;
    }

}
