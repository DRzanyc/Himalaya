package com.example.himalaya.presenters;

import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.Nullable;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.interfaces.IAlbumDetailPersenter;
import com.example.himalaya.interfaces.IAlbunDetailViewCallback;
import com.example.himalaya.utils.Constents;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public class AlbumDetailPersenter implements IAlbumDetailPersenter {

    private static final String TAG = "AlbumDetailPersenter";
    private List<IAlbunDetailViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();

    private Album mTargetAlbum = null;
    //当前的专辑ID
    private int mCurrentAlbumId = -1;
    //当前页码
    private int mCurrentPageIndex = 0;

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

    /**
     * 加载更多的内容
     */
    @Override
    public void loadMore() {
        mCurrentPageIndex++;
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoaderMore) {
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtils.d(TAG, "tracks: " + tracks.size());
                    if (isLoaderMore) {
                        //上拉加载，结果放到最后面
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);

                    } else {
                        //下拉加载，结果放到最前面
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "errorcode ------>" + errorCode);
                LogUtils.d(TAG, "errormsg ------>" + errorMsg);
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                handlerError(errorCode, errorMsg);
            }
        }, mCurrentAlbumId, mCurrentPageIndex);
    }

    /**
     * 处理加载更多的结果
     *
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbunDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    /**
     * 根据页码和专辑ID获取详情列表
     *
     * @param albumId
     * @param page
     */
    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        doLoaded(false);
    }

    /**
     * 网络错误，通知UI
     *
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbunDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbunDetailViewCallback callback : mCallbacks) {
            callback.onDetailListLoaded(tracks);
        }
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
