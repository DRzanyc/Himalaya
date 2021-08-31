package com.example.himalaya;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalaya.adapters.PlayTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/26
 */
public class PlayerActivity extends BaseActivity implements IPlayerCallback {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mTrackSeekBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitle;
    private String mTrackTitleText;
    private ViewPager mTrackPagerView;
    private PlayTrackPagerAdapter mTrackPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        initView();
        //初始化以后 再获取数据
        mPlayerPresenter.getPlayList();
        initEvent();
        startPlay();
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }

    }

    /**
     * 给控件设置事件
     */
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是播放的状态就暂停
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                } else {
                    //非播放状态就开始播放
                    mPlayerPresenter.play();
                }
            }
        });
        mTrackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = false;
                //手离开进度条的时候更新进度
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一首
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });
    }

    /**
     * 找控件
     */
    private void initView() {
        mControlBtn = this.findViewById(R.id.btn_play_or_pause);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mTrackSeekBar = this.findViewById(R.id.track_seek_bar);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mTrackTitle = this.findViewById(R.id.player_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitle.setText(mTrackTitleText);
        }
        mTrackPagerView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayTrackPagerAdapter();
        //设置适配器
        mTrackPagerView.setAdapter(mTrackPagerAdapter);
    }

    @Override
    public void onPlayStart() {
        //开始播放，修改UI
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop_press);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_press);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_press);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        LogUtils.d(TAG, "list------->" + list);
        //把数据设置到适配器
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mTrackSeekBar.setMax(total);
        //更新播放进度条
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        } else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        //更新當前時間
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新進度
        if (!mIsUserTouchProgressBar) {
            mTrackSeekBar.setProgress(currentDuration);
        }
    }

    @Override
    public void onADLoading() {

    }

    @Override
    public void onADFinished() {

    }

    @Override
    public void onTrackUpdate(Track track) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitle != null) {
            //设置当前播放节目的标题
            mTrackTitle.setText(mTrackTitleText);
        }
    }
    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
        super.onDestroy();
    }
}
