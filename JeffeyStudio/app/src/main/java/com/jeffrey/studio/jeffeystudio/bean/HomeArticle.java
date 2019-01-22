package com.jeffrey.studio.jeffeystudio.bean;

import java.util.List;

public class HomeArticle {
    String apkLink;
    String author;
    int chapterId;
    String chaptreName;
    boolean collect;
    int courseId;
    String desc;
    String envelopePic;
    boolean fresh;
    int id;
    String link;
    String niceDate;
    String origin;
    String projectLink;
    long publishTime;
    int superChapterId;
    String superChapterName;
    List<Tag> tags;
    String title;
    int type;
    int userId;
    int visible;
    int zan;

    @Override
    public String toString() {
        return "HomeArticle{" +
                "apkLink='" + apkLink + '\'' +
                ", author='" + author + '\'' +
                ", chapterId=" + chapterId +
                ", chaptreName='" + chaptreName + '\'' +
                ", collect=" + collect +
                ", courseId=" + courseId +
                ", desc='" + desc + '\'' +
                ", envelopePic='" + envelopePic + '\'' +
                ", fresh=" + fresh +
                ", id=" + id +
                ", link='" + link + '\'' +
                ", niceDate='" + niceDate + '\'' +
                ", origin='" + origin + '\'' +
                ", projectLink='" + projectLink + '\'' +
                ", publishTime=" + publishTime +
                ", superChapterId=" + superChapterId +
                ", superChapterName='" + superChapterName + '\'' +
                ", tags=" + tags +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", userId=" + userId +
                ", visible=" + visible +
                ", zan=" + zan +
                '}';
    }
}
