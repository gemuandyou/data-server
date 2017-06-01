package com.gemu.dataserver.entity.auxiliary;

/**
 * 用户接收前端参数的类
 * Created on: 2017/6/1 <br/>
 *
 * @author: Gemu<br/>
 */
public class StorageStory {

    /**
     * 故事预览图（可为空）
     */
    private String prevImg;
    /**
     * 故事简介（不传值为title值）
     */
    private String prevWords;
    /**
     * 作者
     */
    private String author;
    /**
     * 故事标题
     */
    private String title;
    /**
     * 故事子标题
     */
    private String subhead;
    /**
     * 故事日期（时间戳）
     */
    private Long date;
    /**
     * 故事HTML内容
     */
    private String paragraph;

    public String getPrevImg() {
        return prevImg;
    }

    public void setPrevImg(String prevImg) {
        this.prevImg = prevImg;
    }

    public String getPrevWords() {
        return prevWords;
    }

    public void setPrevWords(String prevWords) {
        this.prevWords = prevWords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
}
