package com.example.himalaya.presenters;

import android.util.Log;

import androidx.annotation.Nullable;

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

    /**
     * 根据页码和专辑ID获取详情列表
     *
     * @param albumId
     * @param page
     */
    @Override
    public void getAlbumDetail(int albumId, int page) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constents.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtils.d(TAG, "tracks: " + tracks.size());

                    handlerAlbumDetailResult(tracks);

                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "errorcode ------>" + errorCode);
                LogUtils.d(TAG, "errormsg ------>" + errorMsg);
                handlerError(errorCode,errorMsg);
            }
        });
    }

    /**
     * 网络错误，通知UI
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbunDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode,errorMsg);
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
