package com.gemu.dataserver.entity;

import com.gemu.dataserver.annotation.NeedIndex;

/**
 * 故事
 * Created by gemu on 29/05/2017.
 */
public class Story extends BaseData {

    /**
     * 故事标题
     */
    @NeedIndex
    private String title;
    /**
     * 故事副标题
     */
    private String subhead;
    /**
     * 作者
     */
    @NeedIndex
    private String author;
    /**
     * 故事日期
     */
    private Long date;
    /**
     * 故事内容
     */
    private String paragraph;

    public Story() {
    }

    public Story(String title, String subhead, String author, Long date, String paragraph) {
        this.title = title;
        this.subhead = subhead;
        this.author = author;
        this.date = date;
        this.paragraph = paragraph;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubhead() {
        return subhead;
    }

    public void setSubhead(String subhead) {
        this.subhead = subhead;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return "Story{" +
                "id=" + getId() + "\'" +
                ", title='" + title + '\'' +
                ", subhead='" + subhead + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                ", paragraph='" + paragraph + '\'' +
                '}';
    }
}
