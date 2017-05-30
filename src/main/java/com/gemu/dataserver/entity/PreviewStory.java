package com.gemu.dataserver.entity;

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
    private String words;
    /**
     * 作者
     */
    private String author;

    public PreviewStory() {
    }

    public PreviewStory(String image, String words, String author) {
        this.image = image;
        this.words = words;
        this.author = author;
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