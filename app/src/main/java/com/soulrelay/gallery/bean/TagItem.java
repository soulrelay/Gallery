package com.soulrelay.gallery.bean;

import java.io.Serializable;

/**
 * 标签
 * <p>
 * ZhaoRuYang
 * 10/18/16 11:26 AM
 */
public class TagItem implements Serializable {
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public TagItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TagItem setName(String name) {
        this.name = name;
        return this;
    }
}
