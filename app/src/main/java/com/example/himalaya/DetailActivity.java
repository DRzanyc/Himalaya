package com.example.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adapters.DetailListAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IAlbunDetailViewCallback;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.AlbumDetailPersenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtils;
import com.example.himalaya.views.RoundRectImageView;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public class DetailActivity extends BaseActivity implements IAlbunDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallback {

    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbunAuthor;
    private AlbumDetailPersenter mAlbumDetailPersenter;
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        //???????????????Persenter
        mAlbumDetailPersenter = AlbumDetailPersenter.getInstance();
        mAlbumDetailPersenter.registerViewCallback(this);
        ////????????????Persenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        updatePlaySate(mPlayerPresenter.isPlaying());
        initListener();

    }

    private void initView() {
        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return creatSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbunAuthor = this.findViewById(R.id.tv_album_author);
        //?????????????????????
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTv = this.findViewById(R.id.play_control_tv);
        mPlayControlTv.setSelected(true);
    }


    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //????????????????????????????????????
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }

                }
            }
        });
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        //????????????????????????
        if (mPlayerPresenter.isPlaying()) {
            //?????????????????????
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    private boolean mIsLoaderMore = false;

    private View creatSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refreshLayout);
        //RecyclerView????????????
        //?????????????????????????????????
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        //???????????????????????????
        mDetailListAdapter = new DetailListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        //??????item?????????
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = UIUtil.dip2px(view.getContext(), 3);
                outRect.left = UIUtil.dip2px(view.getContext(), 3);
                outRect.top = UIUtil.dip2px(view.getContext(), 3);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 3);
            }
        });
        mDetailListAdapter.setOnItemClickListener(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mRefreshLayout.finishRefreshing();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //?????????????????????
                if (mAlbumDetailPersenter != null) {
                    mAlbumDetailPersenter.loadMore();
                    mIsLoaderMore = true;
                }
            }
        });

        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mIsLoaderMore = false;
            mRefreshLayout.finishLoadmore();
        }

        this.mCurrentTracks = tracks;
        //????????????
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        //??????????????????
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        // ??????/??????UI??????
        mDetailListAdapter.setData(tracks);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param errorCode
     * @param errorMsg
     */
    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        //???????????????????????????
        if (mAlbumDetailPersenter != null) {
            mAlbumDetailPersenter.getAlbumDetail((int) album.getId(), mCurrentPage);
        }

        //?????????????????????Loading??????
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbunAuthor != null) {
            mAlbunAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mLargeCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtils.d(TAG, "onError");
                }
            });
        }
        if (mSmallCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    /**
     * ?????????????????????????????????
     * @param size size > 0 ???????????? size < 0 ????????????
     */
    @Override
    public void onLoaderMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this,"????????????"+size+"?????????",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"?????????????????????",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        //????????????????????????????????????????????????
        if (mAlbumDetailPersenter != null) {
            mAlbumDetailPersenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //????????????????????????
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        //????????????????????????
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param playing
     */
    private void updatePlaySate(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTv != null) {
            mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTv.setText(R.string.tv_detail_continues_play);
            }else {
                if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayControlTv.setText(mCurrentTrackTitle);
                }
            }
        }
    }

    @Override
    public void onPlayStart() {
        //??????????????????????????????????????????????????????
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
        //???????????????????????????????????????????????????
        updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {
        //???????????????????????????????????????????????????
        updatePlaySate(false);
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

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onADLoading() {

    }

    @Override
    public void onADFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTv != null) {
                mPlayControlTv.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
