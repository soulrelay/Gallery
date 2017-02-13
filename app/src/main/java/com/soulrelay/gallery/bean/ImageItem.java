package com.soulrelay.gallery.bean;


/**
 * 图片信息
 * <p/>
 * ZhaoRuYang
 * 6/12/16 10:55 AM
 */
public class ImageItem extends BaseItem {
    private String brief;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
