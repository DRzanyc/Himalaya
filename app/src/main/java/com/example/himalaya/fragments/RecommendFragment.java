package com.example.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.RecommendPersenter;
import com.example.himalaya.utils.Constents;
import com.example.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Liu
 * @date: 2021/8/13
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private static final String TAG = "RecommendFragment";
    private View mRootview;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPersenter mRecommendPersenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootview = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //recyclerView 步骤
        mRecommendRv = mRootview.findViewById(R.id.recommend_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
               outRect.right = UIUtil.dip2px(view.getContext(),5);
               outRect.left = UIUtil.dip2px(view.getContext(),5);
               outRect.top = UIUtil.dip2px(view.getContext(),5);
               outRect.bottom = UIUtil.dip2px(view.getContext(),5);
            }
        });

        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);

        //获取到逻辑层对象
        mRecommendPersenter = RecommendPersenter.getInstance();
        //设置通知接口的注册
        mRecommendPersenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPersenter.getRecommendList();

        //返回View,给界面显示
        return mRootview;
    }

    /**
     * 获取到推荐内容的时候调用这个方法
     * @param result
     */
    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //把数据设置给适配器并更新UI
        mRecommendListAdapter.setData(result);
    }

    @Override
    public void onLoadMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecommendPersenter != null) {
            mRecommendPersenter.unRegisterViewCallback(this);
        }
    }
}
