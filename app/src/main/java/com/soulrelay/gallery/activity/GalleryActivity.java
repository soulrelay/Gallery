package com.soulrelay.gallery.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.soulrelay.gallery.R;
import com.soulrelay.gallery.adapter.TabHomeAdapter;
import com.soulrelay.gallery.bean.GalleryItem;
import com.soulrelay.gallery.fragment.GalleryFragment;
import com.soulrelay.gallery.fragment.GalleryRelatedFragment;
import com.soulrelay.gallery.view.gallery.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  图集
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class GalleryActivity extends FragmentActivity implements View.OnClickListener,
        MyViewPager.OnNeedScrollListener, GalleryFragment.OnPhotoTapListener {

    @BindView(R.id.viewpager_photos)
    MyViewPager viewPager;
    @BindView(R.id.back_layout)
    View backLl;
    @BindView(R.id.iv_back)
    ImageView mBackBtn;
    @BindView(R.id.activity_gallery_root)
    View mRootView;

    private List<Fragment> fragments;
    private TabHomeAdapter adapter;
    private GalleryItem gallery;
    private boolean isHideView;
    private Fragment mCurrFragment;

    public static void start(Context context, GalleryItem gallery) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("gallery", gallery);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mBackBtn.setOnClickListener(this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnNeedScrollListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (fragments != null) {
                    mCurrFragment = fragments.get(position);
                    switchTitle(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initData() {
        refresh();
    }

    private void refresh() {
      /*  if(getIntent() == null){
            return;
        }
        gallery = (GalleryItem) getIntent().getSerializableExtra("gallery");
        if (gallery == null) {
            return;
        }*/
        createFragmentList();
        updateViewPager();
    }

    private void createFragmentList() {
        fragments = new ArrayList<>();

        Bundle bundlePhotos = new Bundle();
        bundlePhotos.putSerializable("gallery", gallery);
        GalleryFragment galleryFragment = GalleryFragment.newInstance(bundlePhotos);
        galleryFragment.setOnPhotoTapListener(this);
        fragments.add(galleryFragment);

       /* Bundle bundleRecommendation = new Bundle();
        bundleRecommendation.putLong("id", gallery.getId());*/
        fragments.add(GalleryRelatedFragment.newInstance("", ""));

        mCurrFragment = fragments.get(0);
    }


    private void updateViewPager() {
        adapter = new TabHomeAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    private void switchTitle(int position) {
        switch (position) {
            case 0:
                if (isHideView) {
                    //若离开图集时，是隐藏状态，回去时还是隐藏状态
                    onDismissView();
                }
                break;
            case 1:
                if (isHideView) {
                    //在图集隐藏了上方的view时，到图集推荐时需要显示出来
                    backLl.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ButterKnife.unbind(this);
    }

    @Override
    public boolean needScroll() {
        if (mCurrFragment instanceof GalleryFragment) {
            if (!((GalleryFragment) mCurrFragment).isLastItem()) {
                return false;
            } else {
                //当图集滑动到最后一个时，如果图集推荐没有内容，则禁止滑动
                GalleryRelatedFragment relatedFragment = (GalleryRelatedFragment) fragments.get(1);
                if (!relatedFragment.isHasData()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onShowView() {
        isHideView = false;
        backLl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDismissView() {
        isHideView = true;
        backLl.setVisibility(View.INVISIBLE);
    }
}
