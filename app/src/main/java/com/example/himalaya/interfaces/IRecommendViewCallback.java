package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/18
 */
public interface IRecommendViewCallback {

    /**
     * 获取推荐内容的结果
     *
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 网络错误
     */
    void onNetworkError();

    /**
     * 正在加载
     */
    void onLoading();

    /**
     * 数据为空
     */
    void onEmpty();

}


