package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/26
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();
    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;
    private Track  mCurrentTrack;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
    }

    public static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
        }else {
            LogUtils.d(TAG,"mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void  getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //返回当前是否在播放的状态
        return mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdate(mCurrentTrack);
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mIPlayerCallbacks.remove(iPlayerCallback);
    }

    //=====================广告相关的回调方法 start======================
    @Override
    public void onStartGetAdsInfo() {
        LogUtils.d(TAG, "onStartGetAdsInfo......");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtils.d(TAG, "onGetAdsInfo......");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtils.d(TAG, "onAdsStartBuffering......");

    }

    @Override
    public void onAdsStopBuffering() {
        LogUtils.d(TAG, "onAdsStopBuffering......");

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtils.d(TAG, "onStartPlayAds......");

    }

    @Override
    public void onCompletePlayAds() {
        LogUtils.d(TAG, "onCompletePlayAds......");

    }

    @Override
    public void onError(int what, int extra) {
        LogUtils.d(TAG, "on ErrOr what  =>" + what + "extra =>" + extra);
    }
    //=====================广告相关的回调方法 end======================

    //=====================播放器相关的回调方法 start======================
    @Override
    public void onPlayStart() {
        LogUtils.d(TAG, "onPlayStart......");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtils.d(TAG, "onPlayPause......");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtils.d(TAG, "onPlayStop......");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtils.d(TAG, "onSoundPlayComplete......");
    }

    @Override
    public void onSoundPrepared() {
        LogUtils.d(TAG, "onSoundPrepared......");
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtils.d(TAG, "onSoundSwitch......");
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtils.d(TAG, "onBufferingStart......");
    }

    @Override
    public void onBufferingStop() {
        LogUtils.d(TAG, "onBufferingStop......");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtils.d(TAG, "onBufferProgress......" + progress);
    }

    @Override
    public void onPlayProgress(int currentPos, int duration) {
        //单位是毫秒
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currentPos, duration);
        }
        LogUtils.d(TAG, "onPlayProgress......");
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtils.d(TAG, "onError......e--->" + e);
        return false;
    }
    //=====================播放器相关的回调方法 end======================

}
