package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.AlbumDetailPersenter;
import com.example.himalaya.presenters.RecommendPersenter;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/13
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.OnRecommendItemClickListener {
    private static final String TAG = "RecommendFragment";
    private View mRootview;
    private RecyclerView mRecommendRv;
    private AlbumListAdapter mRecommendListAdapter;
    private RecommendPersenter mRecommendPersenter;
    private UILoader mUILoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {

        mUILoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccesView(layoutInflater, container);
            }
        };


        //获取到逻辑层对象
        mRecommendPersenter = RecommendPersenter.getInstance();
        //设置通知接口的注册
        mRecommendPersenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPersenter.getRecommendList();

        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }

        mUILoader.setOnRetryClickListener(this);

        //返回View,给界面显示
        return mUILoader;
    }

    private View createSuccesView(LayoutInflater layoutInflater, ViewGroup container) {
        mRootview = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        TwinklingRefreshLayout twinklingRefreshLayout = mRootview.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
        //recyclerView 步骤
        mRecommendRv = mRootview.findViewById(R.id.recommend_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        //设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListener(this);

        return mRootview;
    }

    /**
     * 获取到推荐内容的时候调用这个方法
     *
     * @param result
     */
    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //把数据设置给适配器并更新UI
        mRecommendListAdapter.setData(result);
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onLoading() {
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onEmpty() {
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecommendPersenter != null) {
            mRecommendPersenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //网络错误，重新加载内容
        if (mRecommendPersenter != null) {
            mRecommendPersenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int posstion, Album album) {
        AlbumDetailPersenter.getInstance().setTargetAlbum(album);
        //item被点击了,跳转到详情页面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
