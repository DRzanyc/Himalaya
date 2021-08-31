package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constents;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Liu
 * @date: 2021/8/18
 */
public class RecommendPersenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPersenter";
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    private RecommendPersenter() {
    }

    private static RecommendPersenter sInstance = null;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPersenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPersenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPersenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        upDataLoading();
        //获取推荐内容
        Map<String, String> map = new HashMap<>();
        //一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constents.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                //成功获取数据
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //获取数据后更新UI
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //获取数据失败
                LogUtils.d(TAG, "error code -----" + i);
                LogUtils.d(TAG, "error Msg -----" + s);
                handleError();
            }
        });
    }

    private void handleError() {
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            } else {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
            }
        }
    }

    private void upDataLoading() {
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
