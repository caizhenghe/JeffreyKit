package com.jeffrey.studio.jeffeystudio.bean;

import java.util.List;

public class HomeArticleList {
    List<HomeArticle> datas;
    int curPage;
    int offset;
    boolean over;
    int pageCount;
    int size;
    int total;

    @Override
    public String toString() {
        return "HomeArticleList{" +
                "datas=" + datas +
                ", curPage=" + curPage +
                ", offset=" + offset +
                ", over=" + over +
                ", pageCount=" + pageCount +
                ", size=" + size +
                ", total=" + total +
                '}';
    }
}
