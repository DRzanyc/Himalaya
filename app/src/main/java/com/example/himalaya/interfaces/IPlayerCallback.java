package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/26
 */
public interface  IPlayerCallback {

    /**
     * 开始播放
     */
    void onPlayStart();
    /**
     * 播放暂停
     */
    void onPlayPause();
    /**
     * 播放停止
     */
    void onPlayStop();
    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首播放
     * @param track
     */
    void onNextPlay(Track track);

    /**
     * 上一首播放
     * @param track
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完成
     * @param list 播放数据列表
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式改变
     * @param mode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode mode);

    /**
     * 进度条的改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(int currentProgress, int total);

    /**
     * 广告正在加载
     */
    void onADLoading();

    /**
     * 广告结束
     */
    void onADFinished();

    /**
     * 更新当前节目
     * @param track 节目
     */
    void onTrackUpdate(Track track);
}
