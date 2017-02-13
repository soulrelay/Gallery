package com.soulrelay.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soulrelay.gallery.R;
import com.soulrelay.gallery.bean.GalleryItem;
import com.soulrelay.gallery.bean.ImageItem;
import com.soulrelay.library.imageloader.ImageLoaderUtil;

import java.util.List;

/**
 * 图集--推荐
 * Created by chenchongli on 16/8/8.
 */
public class GalleryRelatedAdapter extends BaseAdapter {

    private List<GalleryItem> data;
    private Context context;
    private OnGalleryRelatedCallback callback;

    public GalleryRelatedAdapter(Context context, OnGalleryRelatedCallback callback) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (data == null || data.size() == 0) {
            return 0;
        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GalleryRecommHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gallery_recommendation, null);
            holder = new GalleryRecommHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GalleryRecommHolder) convertView.getTag();
        }
        final GalleryItem item = data.get(position);
        //加一个空格的目的是，如果当title长度正好是宽度多一个字符时，不会显示省略号
        holder.tvTitle.setText(item.getTitle() + " ");
        String picUrl = null;
        List<ImageItem> images = item.getImages();
        if(images != null && images.size() != 0){
            picUrl = images.get(0).getImage();
        }
        ImageLoaderUtil.getInstance().loadImage(picUrl,
                R.drawable.bg_default_video_common_big, holder.imageView);

        holder.llGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(item);
            }
        });
        return convertView;
    }

    public void update(List<GalleryItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class GalleryRecommHolder {
        ImageView imageView;
        TextView tvTitle;
        LinearLayout llGrid;

        public GalleryRecommHolder(View itemView) {
            imageView = (ImageView) itemView.findViewById(R.id.iv_gallery);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            llGrid = (LinearLayout) itemView.findViewById(R.id.ll_grid);
        }
    }

    public interface OnGalleryRelatedCallback {
        void onItemClick(GalleryItem item);
    }
}
