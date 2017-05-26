package com.come335.dosyapaylasim;

public class Post {

    private String postId;
    private String postTitle;
    private String postSubTitle;
    private String postFileUrl;
    private String postFileExt;
    private String postDate;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostSubTitle() {
        return postSubTitle;
    }

    public void setPostSubTitle(String postSubTitle) {
        this.postSubTitle = postSubTitle;
    }

    public String getPostFileUrl() {
        return postFileUrl;
    }

    public void setPostFileUrl(String postFileUrl) {
        this.postFileUrl = postFileUrl;
    }

    public String getPostFileExt() {
        return postFileExt;
    }

    public void setPostFileExt(String postFileExt) {
        this.postFileExt = postFileExt;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public Post(String postId, String postTitle, String postSubTitle, String postFileUrl, String postFileExt, String postDate) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postSubTitle = postSubTitle;
        this.postFileUrl = postFileUrl;
        this.postFileExt = postFileExt;
        this.postDate = postDate;
    }
}