package com.example.himalaya;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IAlbumDetailPersenter;
import com.example.himalaya.interfaces.IAlbunDetailViewCallback;
import com.example.himalaya.presenters.AlbumDetailPersenter;
import com.example.himalaya.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * @author: Liu
 * @date: 2021/8/23
 */
public class DetailActivity extends BaseActivity implements IAlbunDetailViewCallback {

    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbunAuthor;
    private AlbumDetailPersenter mAlbumDetailPersenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        mAlbumDetailPersenter = AlbumDetailPersenter.getInstance();
        mAlbumDetailPersenter.registerViewCallback(this);
    }

    private void initView() {
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbunAuthor = this.findViewById(R.id.tv_album_author);
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {

    }

    @Override
    public void onAlbumLoaded(Album album) {
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
            Picasso.get().load(album.getCoverUrlLarge()).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }
}
