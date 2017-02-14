package com.soulrelay.gallery.bean;


/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  图片信息
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
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
