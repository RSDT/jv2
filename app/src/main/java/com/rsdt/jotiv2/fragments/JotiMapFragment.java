package com.rsdt.jotiv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 6-1-2016
 * The fragment for the map.
 */
public class JotiMapFragment extends SupportMapFragment {


    public JotiMapFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public static JotiMapFragment newInstance() { return new JotiMapFragment(); }

    public static final String TAG = "JOTI_MAP_FRAGMENT";

}
