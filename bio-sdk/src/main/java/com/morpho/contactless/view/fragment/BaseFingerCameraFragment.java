package com.morpho.contactless.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.morpho.mrz.R;

/**
 * Created by Alfredo Hernandez Alarcon on 06/06/17.
 * Base Finger Camera Fragment
 */
public class BaseFingerCameraFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.finger_capture_fragment, container, false);
        return view;
    }
}
