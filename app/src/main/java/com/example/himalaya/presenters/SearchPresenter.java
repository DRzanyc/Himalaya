package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Liu
 * @date: 2021/9/13
 */
public class SearchPresenter implements ISearchPresenter {
    private static final String TAG = "SearchPresenter";
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;


    private SearchPresenter() {
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallback> mCallbacks = new ArrayList<>();

    @Override
    public void doSearch(String keyword) {
        //用于重新搜索 网络不好的时候用户会重新搜索
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtils.d(TAG, "albums size----" + albums.size());
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onSearchResultLoaded(albums);
                    }
                } else {
                    LogUtils.d(TAG, "albums is null");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                //LogUtils.d(TAG, "error code----" + errorCode + ",errorMsg-----" + errorMsg);
                for (ISearchCallback callback : mCallbacks) {
                    callback.onError(errorCode, errorMsg);
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> wordList = hotWordList.getHotWordList();
                    LogUtils.d(TAG, "albums size----" + wordList.size());
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onHotWordLoaded(wordList);
                    }

                }

            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "getHotWord    error code----" + errorCode + ", getHotWord    errorMsg-----" + errorMsg);

            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtils.d(TAG, "keyWordList size----" + keyWordList.size());

                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "getRecommendWord    error code----" + errorCode + ", getRecommendWord      errorMsg-----" + errorMsg);
            }
        });

    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
