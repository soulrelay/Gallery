package com.soulrelay.gallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soulrelay.gallery.R;
import com.soulrelay.gallery.bean.ImageItem;
import com.soulrelay.gallery.view.gallery.PhotoView;
import com.soulrelay.gallery.view.gallery.PhotoViewAttacher;
import com.soulrelay.library.imageloader.ImageLoaderUtil;
import com.soulrelay.library.imageloader.glideprogress.ProgressLoadListener;

import java.util.List;

/**
 * 图集详情
 * Created by chenchongli on 16/8/9.
 */
public class GalleryAdapter extends PagerAdapter {


    private List<ImageItem> items;
    private Context context;
    private OnGalleryAdapterCallback callback;
    private Object mPrimaryItem;

    public GalleryAdapter(Context context, OnGalleryAdapterCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void update(List<ImageItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (items == null) {
            return 0;
        } else {
            return items.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object != null) {
            container.removeView((View) object);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.gallery_photo_view, container, false);
        final PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
        final TextView progressStr = (TextView) view.findViewById(R.id.photo_progress);
        final View progressLayout = view.findViewById(R.id.photo_progress_layout);
        final View retryBtn = view.findViewById(R.id.gallery_load_fail_root);
        final ImageItem item = getGalleryItem(position);
        if (item == null) {
            return null;
        }
        showImage(retryBtn, item, photoView, progressStr, progressLayout);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryBtn.setVisibility(View.GONE);
                showImage(retryBtn, item, photoView, progressStr, progressLayout);
            }
        });
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                callback.onPhotoTap(item);
            }
        });
        ViewGroup parent = ((ViewGroup) view.getParent());
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(view);
        return view;
    }

    public ImageItem getGalleryItem(int position) {
        if (items == null || items.size() == 0) {
            return null;
        }
        return items.get(position % items.size());
    }

    public int getPosition(int position) {
        if (items == null || items.size() == 0) {
            return 0;
        }
        return (position % items.size());
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryItem = object;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object == mPrimaryItem) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }

    private void showImage(final View retryView, ImageItem item, final PhotoView photoView, final TextView progressStr, final View progressLayout) {
        if (item == null) {
            return;
        }
        String url = item.getImage();
        ImageLoaderUtil.getInstance().loadImageWithProgress(url, photoView, new ProgressLoadListener() {
            @Override
            public void update(int bytesRead, int contentLength) {
                progressStr.setText(bytesRead * 100 / contentLength + "%");
            }

            @Override
            public void onException() {
                retryView.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onResourceReady() {
                progressLayout.setVisibility(View.GONE);
            }
        });
    }

    public interface OnGalleryAdapterCallback {
        void onPhotoTap(ImageItem item);
    }
}
