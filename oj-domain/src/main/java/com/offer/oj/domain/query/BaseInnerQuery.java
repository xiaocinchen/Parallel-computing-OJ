package com.offer.oj.domain.query;

import java.io.Serializable;

public class BaseInnerQuery implements Serializable {

    /**
     * 开始条数
     */
    private int startRow;

    /**
     * 页码
     */
    private int page;

    /**
     * 页数
     */
    private int pageSize;

    /**
     * 排序规则
     */
    private String orderBy;

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    /**
     * 获取pageSize
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = 10;
        }

        return pageSize;
    }

    /**
     * 设置pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        resetStartRow();
    }

    public int getPage() {
        return page;
    }

    /**
     * setPage
     */
    public void setPage(int page) {
        this.page = page;
        resetStartRow();
    }

    /**
     * init row
     */
    private void resetStartRow() {
        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        this.startRow = (page - 1) * pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "BaseInnerQuery{" +
                "startRow=" + startRow +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
}
