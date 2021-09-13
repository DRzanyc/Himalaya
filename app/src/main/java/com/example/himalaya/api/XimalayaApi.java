package com.example.himalaya.api;

import com.example.himalaya.utils.Constents;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Liu
 * @date: 2021/9/10
 */
public class XimalayaApi {

    private XimalayaApi() {
    }

    private static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi() {
        if (sXimalayaApi == null) {
            synchronized (XimalayaApi.class) {
                if (sXimalayaApi == null) {
                    sXimalayaApi = new XimalayaApi();
                }
            }
        }
        return sXimalayaApi;
    }

    /**
     * 获取推荐内容
     *
     * @param callBack 请求结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        //一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constents.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    /**
     * 根据专辑的ID获取专辑内容
     *
     * @param callBack  获取专辑详情的回调接口
     * @param albumId   专辑的ID
     * @param pageIndex 专辑的页码
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack, long albumId, int pageIndex) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constents.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }

}
