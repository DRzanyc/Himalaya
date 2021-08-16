package com.example.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseFragment;

/**
 * @author: Liu
 * @date: 2021/8/13
 */
public class RecommendFragment extends BaseFragment {
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootview = layoutInflater.inflate(R.layout.fragment_recommend, container,false);
        return rootview;
    }
}
