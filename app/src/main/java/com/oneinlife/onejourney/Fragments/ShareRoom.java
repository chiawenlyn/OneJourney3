package com.oneinlife.onejourney.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oneinlife.onejourney.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareRoom extends Fragment {


    public ShareRoom() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share_house, container, false);
    }

}
