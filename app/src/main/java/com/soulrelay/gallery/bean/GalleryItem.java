package com.soulrelay.gallery.bean;

import java.util.List;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  该对象表示一个图集信息
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class GalleryItem extends BaseItem {
    private long eventId;
    private String brief;
    private String origin;
    private int nimages;
    private long createTm;
    private String large_image;
    private List<ImageItem> images;


    public String getLarge_image() {
        return large_image;
    }

    public GalleryItem setLarge_image(String large_image) {
        this.large_image = large_image;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getNimages() {
        return nimages;
    }

    public void setNimages(int nimages) {
        this.nimages = nimages;
    }

    public long getCreateTm() {
        return createTm;
    }

    public void setCreateTm(long createTm) {
        this.createTm = createTm;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public List<ImageItem> getImages() {
        return images;
    }

    public void setImages(List<ImageItem> images) {
        this.images = images;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "GalleryItem{" +
                "eventId=" + eventId +
                ", brief='" + brief + '\'' +
                ", origin='" + origin + '\'' +
                ", nimages=" + nimages +
                ", createTm=" + createTm +
                ", large_image='" + large_image + '\'' +
                ", images=" + images +
                "} " + super.toString();
    }
}

