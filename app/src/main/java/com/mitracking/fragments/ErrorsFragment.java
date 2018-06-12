package com.mitracking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.adapter.TrackAdapter;
import com.mitracking.objs.TrackObj;

import java.util.ArrayList;

public class ErrorsFragment extends Fragment {

    private int lay;
    private ArrayList<TrackObj> array;
    private ListView errorskList;
    private TrackAdapter adapter;

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
        View rootView = inflater.inflate(R.layout.errors, container, false);

        errorskList = (ListView)rootView.findViewById(R.id.errorskList);
        array = Singleton.getBdh().getTracErrorList();
        adapter = new TrackAdapter(this, array);
        errorskList.setAdapter(adapter);

        return rootView;
    }

}
