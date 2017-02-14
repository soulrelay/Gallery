package com.soulrelay.gallery.bean;


import com.soulrelay.gallery.constants.Constants;

import java.io.Serializable;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description:  超类 部分字段冗余，捡有用的看
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class SuperItem implements Serializable {

    /************************************
     *
     * 数据固有属性,即自身属性
     *
     ***********************************/
    protected String type;
    protected long id;
    protected String title;
    protected String image;
    protected String key;
    protected int top = 0;


    /************************************
     *
     * 项目中，使用时添加属性
     *
     ***********************************/

    // 用于表现View界面显示
    private boolean isSelect;
    /**
     * 用于表明组分类,参看 {@link com.soulrelay.gallery.constants.Constants.GroupType}
     */
    private int groupType = Constants.GroupType.none;
    // 用于记录该数据的本地更新时间
    private long localUpdateTime;


    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

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

    /************************************
     ***********************************/


    public long getLocalUpdateTime() {
        return localUpdateTime;
    }

    public void setLocalUpdateTime(long localUpdateTime) {
        this.localUpdateTime = localUpdateTime;
    }

    public int getTop() {
        return top;
    }

    public SuperItem setTop(int top) {
        this.top = top;
        return this;
    }

    /**
     * 获取该基础对象的唯一标示
     *
     * @return
     */
    public String getOnlyKey() {
        return type + "_" + id;
    }


    public String getType() {
        return type;
    }

    public SuperItem setType(String type) {
        this.type = type;
        return this;
    }

    public long getId() {
        return id;
    }

    public SuperItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getKey() {
        return key;
    }

    public SuperItem setKey(String key) {
        this.key = key;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public SuperItem setSelect(boolean select) {
        isSelect = select;
        return this;
    }


    /**
     * 仅为报数使用,特殊类型直接进行复写
     *
     * @return
     */
    public String getDTType() {
        return type;
    }

    /**
     * 判断是否置顶
     *
     * @return
     */
    public boolean isTop() {
        return top == 1;
    }


    /**
     * 判断是否常驻置顶
     *
     * @return
     */
    public boolean isResidentTop() {
        return top == 2;
    }

    public boolean isNormalData() {
        return top == 0;
    }


    @Override
    public String toString() {
        return "SuperItem{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", key='" + key + '\'' +
                ", top=" + top +
                ", isSelect=" + isSelect +
                ", groupType=" + groupType +
                ", localUpdateTime=" + localUpdateTime +
                '}';
    }
}
