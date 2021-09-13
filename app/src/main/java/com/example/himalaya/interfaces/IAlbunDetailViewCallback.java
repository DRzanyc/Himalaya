package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public interface IAlbunDetailViewCallback {

    /**
     * 加载专辑详情内容
     *
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 网络错误
     *
     * @param errorCode
     * @param errorMsg
     */
    void onNetworkError(int errorCode, String errorMsg);

    /**
     * 把Album传给UI
     *
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 加载更多的结果
     *
     * @param size size > 0 加载成功 size < 0 加载失败
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     *
     * @param size size > 0 加载成功 size < 0 加载失败
     */
    void onRefreshFinished(int size);
}
