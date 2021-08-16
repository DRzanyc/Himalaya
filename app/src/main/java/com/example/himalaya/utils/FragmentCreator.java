package com.example.himalaya.utils;

import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.fragments.HistoryFragment;
import com.example.himalaya.fragments.RecommendFragment;
import com.example.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Liu
 * @date: 2021/8/13
 */
public class FragmentCreator {
    public static final int INDEX_RECOMMEND = 0;
    public static final int INDEX_SUBSCRIPTION = 1;
    public static final int INDEX_HISTORY = 2;
    public static final int PAGE_COUNT =3;

    private static Map<Integer, BaseFragment> sCache = new HashMap<>();

    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }
        switch (index) {
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
        }
        sCache.put(index, baseFragment);
        return baseFragment;
    }
}
