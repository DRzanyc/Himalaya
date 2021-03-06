package com.example.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalaya.adapters.PlayTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtils;
import com.example.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

/**
 * @author: Liu
 * @date: 2021/8/26
 */
public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

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
    private boolean mIsUserSlidePager = false;
    private ImageView mModeSwitchBtn;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();
    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;


    /**
     * ?????????????????????
     * PLAY_MODEL_LIST????????????    //????????????
     * PLAY_MODEL_SINGLE_LOOP ??????????????????
     * PLAY_MODEL_LIST_LOOP????????????
     * PLAY_MODEL_RANDOM ????????????
     */
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private View mPlayListBtn;
    private SobPopWindow mPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    private final int BG_ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        //??????????????? ???????????????
        mPlayerPresenter.getPlayList();
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //?????????????????????
                updateBgAlpha(value);
            }
        });
        //?????????
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //?????????????????????
                updateBgAlpha(value);
            }
        });
    }


    /**
     * ?????????????????????
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????????????????????
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {
                    //??????????????????????????????
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
                //???????????????????????????????????????
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mTrackPagerView.addOnPageChangeListener(this);

        mTrackPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });

        mModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????????????????????
                switchPlayMode();
            }
        });

        //?????????????????????
        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????????????????
                mPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                mEnterBgAnimator.start();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //popwindow????????????????????????
                mOutBgAnimator.start();
            }
        });

        mPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //??????????????????item????????????
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });
        mPopWindow.setPlayListActionListener(new SobPopWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                //??????????????????
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //??????????????????
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });
    }

    private void switchPlayMode() {
        //????????????mode???????????????mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //??????????????????
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    public void updateBgAlpha(float alpha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_player_mode_order;
        switch (mCurrentMode) {
            case PLAY_MODEL_LIST: //????????????
                resId = R.drawable.selector_player_mode_order;
                break;
            case PLAY_MODEL_RANDOM:  //????????????
                resId = R.drawable.selector_player_mode_random;
                break;
            case PLAY_MODEL_LIST_LOOP: //????????????
                resId = R.drawable.selector_player_mode_order_loop;
                break;
            case PLAY_MODEL_SINGLE_LOOP:  //????????????
                resId = R.drawable.selector_player_mode_single_loop;
                break;
        }
        mModeSwitchBtn.setImageResource(resId);
    }

    /**
     * ?????????
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
        //???????????????
        mTrackPagerAdapter = new PlayTrackPagerAdapter();
        //???????????????
        mTrackPagerView.setAdapter(mTrackPagerAdapter);
        //???????????????????????????
        mModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        mPlayListBtn = this.findViewById(R.id.play_list);
        mPopWindow = new SobPopWindow();
    }

    @Override
    public void onPlayStart() {
        //?????????????????????UI
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_pause);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
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
        //???????????????????????????
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
        //???????????????????????????????????????
        if (mPopWindow != null) {
            mPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {
        //???????????????????????????UI
        mCurrentMode = mode;
        //??????popwindow??????????????????
        mPopWindow.updatePlayModeBtnIconAndText(mCurrentMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mTrackSeekBar.setMax(total);
        //?????????????????????
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
        //??????????????????
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //????????????
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
    public void onTrackUpdate(Track track, int playIndex) {
        if (track == null) {
            LogUtils.d(TAG, "onTrackUpdate-------->track =null");
            return;
        }
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitle != null) {
            //?????????????????????????????????
            mTrackTitle.setText(mTrackTitleText);
        }
        //?????????????????????????????????????????????
        if (mTrackPagerView != null) {
            mTrackPagerView.setCurrentItem(playIndex, true);
        }
        //????????????????????????????????????
        if (mPopWindow != null) {
            mPopWindow.setCurrentPlayPosition(playIndex);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param isReverse
     */
    @Override
    public void updateListOrder(boolean isReverse) {
        mPopWindow.updateOrderIconAndText(isReverse);
    }

    /**
     * ????????????
     */
    @Override
    protected void onDestroy() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //?????????????????????????????????????????????
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
