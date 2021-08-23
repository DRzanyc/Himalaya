package com.example.himalaya.interfaces;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public interface IAlbumDetailPersenter {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);

    /**
     * 注册UI通知的接口回调
     * @param detailViewCallback
     */
    void registerViewCallback(IAlbunDetailViewCallback detailViewCallback);

    /**
     * 取消UI通知的接口回调
     * @param detailViewCallback
     */
    void unRegisterViewCallback(IAlbunDetailViewCallback detailViewCallback);
}
