package com.soulrelay.gallery.bean;

import java.io.Serializable;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  标签
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
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
