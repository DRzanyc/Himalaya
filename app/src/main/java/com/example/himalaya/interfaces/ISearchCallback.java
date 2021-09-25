package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/9/13
 */
public interface ISearchCallback {
    /**
     * 搜索结果的回调方法
     *
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的结果回调方法
     *
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     *
     * @param result 结果
     * @param isOkey true 成功加载更多 false 没有更多数据
     */
    void onLoadMoreResult(List<Album> result, boolean isOkey);

    /**
     * 联系关键字的结果
     *
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 错误页面回调
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, String errorMsg);
}
