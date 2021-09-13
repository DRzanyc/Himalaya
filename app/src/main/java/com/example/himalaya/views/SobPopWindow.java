package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.PlayListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/9/8
 */
public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdapter mPlayListAdapter;
    private ImageView mPlayModeIv;
    private TextView mPlayModeTv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClickListener = null;
    private View mOrderBtnContainer;
    private TextView mOrderTv;
    private ImageView mOrderIv;

    public SobPopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置外部可点击 要先设置drawable
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置弹出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找控件
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mTrackList.setAdapter(mPlayListAdapter);
        //播放模式相关
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        //播放顺序相关
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderTv = mPopView.findViewById(R.id.play_list_order_tv);
        mOrderIv = mPopView.findViewById(R.id.play_list_order_iv);
    }

    private void initEvent() {
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });
        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表顺序
                mPlayModeClickListener.onOrderClick();
            }
        });
    }

    /**
     * 给适配器设置数据
     *
     * @param data
     */
    public void setListData(List<Track> data) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position); //列表显示在当前点击的位置
        }
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener) {
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放顺序的文字和图标
     * @param isReverse
     */
    public void updateOrderIconAndText(boolean isReverse) {
        isReverse =!isReverse;
        mOrderIv.setImageResource(isReverse ?
                R.drawable.selector_player_mode_order : R.drawable.selector_player_mode_revers);
        mOrderTv.setText(BaseApplication.getAppContext()
                .getResources().getString(isReverse ? R.string.order_text : R.string.revers));
    }

    /**
     * 根据当前状态更新播放模式的图标和文字
     */
    public void updatePlayModeBtnIconAndText(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_player_mode_order;
        int textId = R.string.play_mode_order_text;
        switch (playMode) {
            case PLAY_MODEL_LIST: //列表播放
                resId = R.drawable.selector_player_mode_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:  //随机播放
                resId = R.drawable.selector_player_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP: //列表循环
                resId = R.drawable.selector_player_mode_order_loop;
                textId = R.string.play_mode_list_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:  //单曲循环
                resId = R.drawable.selector_player_mode_single_loop;
                textId = R.string.play_mode_single_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    public interface PlayListItemClickListener {
        void onItemClick(int position);
    }

    public void setPlayListActionListener(PlayListActionListener playModeListener) {
        mPlayModeClickListener = playModeListener;
    }

    public interface PlayListActionListener {
        //播放模式被点击了
        void onPlayModeClick();

        //播放顺序被点击了
        void onOrderClick();
    }
}
