package com.soulrelay.gallery.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 服务端七大基础的bean
 * <p/>
 * ZhaoRuYang
 * 6/12/16 4:29 PM
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
