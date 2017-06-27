package com.gemu.dataserver.entity;

import com.gemu.dataserver.annotation.NeedIndex;

/**
 * 故事评论
 * Created by gemu on 14/06/2017.
 */
public class Comment extends BaseData {

    /**
     * 评论的故事ID
     */
    @NeedIndex
    private String storyId;
    /**
     * 评论人
     */
    @NeedIndex
    private String friendName;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论日期
     */
    private Long date;
    /**
     * 是否已读
     */
    @NeedIndex
    private boolean readed;

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

}
