package com.example.himalaya.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author: Liu
 * @date: 2021/8/13
 */
public abstract class BaseFragment extends Fragment {

    private View mRootview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootview = onSubViewLoaded(inflater,container);
        return mRootview;
    }

    protected abstract View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container);
}
