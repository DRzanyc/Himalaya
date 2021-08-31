package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public interface IAlbumDetailPersenter extends IBasePresenter<IAlbunDetailViewCallback> {

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

}
