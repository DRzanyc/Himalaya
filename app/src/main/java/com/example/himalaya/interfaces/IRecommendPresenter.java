package com.example.himalaya.interfaces;

/**
 * @author: Liu
 * @date: 2021/8/18
 */
public interface IRecommendPresenter {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     *上拉加载更多
     */
    void loadMore();

    /**
     * 注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消ui的回调
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);
}