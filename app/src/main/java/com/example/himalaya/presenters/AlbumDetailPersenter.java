package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IAlbumDetailPersenter;
import com.example.himalaya.interfaces.IAlbunDetailViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public class AlbumDetailPersenter implements IAlbumDetailPersenter {

    private List<IAlbunDetailViewCallback> mCallbacks = new ArrayList<>();

    private Album mTargetAlbum = null;

    private AlbumDetailPersenter() {
    }

    private static AlbumDetailPersenter sInstence = null;

    public static AlbumDetailPersenter getInstance() {
        if (sInstence == null) {
            synchronized (AlbumDetailPersenter.class) {
                if (sInstence == null) {
                    sInstence = new AlbumDetailPersenter();
                }
            }
        }
        return sInstence;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {

    }

    @Override
    public void registerViewCallback(IAlbunDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbunDetailViewCallback detailViewCallback) {
        mCallbacks.remove(detailViewCallback);
    }


    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
