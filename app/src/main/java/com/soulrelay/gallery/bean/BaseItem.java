package com.soulrelay.gallery.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  基础bean
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class BaseItem extends SuperItem implements Serializable {
    @SerializedName("publish_tm")
    @Expose
    private long publishTm;
    private List<TagItem> tags;

    public List<TagItem> getTags() {
        return tags;
    }

    public void setTags(List<TagItem> tags) {
        this.tags = tags;
    }

    public long getPublishTm() {
        return publishTm;
    }

    public void setPublishTm(long publishTm) {
        this.publishTm = publishTm;
    }


    @Override
    public String toString() {
        return "BaseItem{" +
                "publishTm=" + publishTm +
                ", tags=" + tags +
                "} " + super.toString();
    }
}
