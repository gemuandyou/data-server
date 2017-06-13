package com.gemu.dataserver.entity;

import com.gemu.dataserver.annotation.NeedIndex;

/**
 * 故事预览
 * Created by gemu on 29/05/2017.
 */
public class PreviewStory extends BaseData {

    /**
     * 预览图（可为空）
     */
    private String image;
    /**
     * 故事简介（没指定就用故事标题）
     */
    @NeedIndex
    private String words;
    /**
     * 作者
     */
    @NeedIndex
    private String author;

    /**
     * 故事ID
     */
    @NeedIndex
    private String storyId;

    public PreviewStory() {
    }

    public PreviewStory(String image, String words, String author, String storyId) {
        this.image = image;
        this.words = words;
        this.author = author;
        this.storyId = storyId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "PreviewStory{" +
                "id=" + getId() + "\'" +
                ", image='" + image + '\'' +
                ", words='" + words + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
