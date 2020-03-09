package com.ogangi.messangi.sdk.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {
    @SerializedName("page_num")
    @Expose
    private Integer pageNum;
    @SerializedName("rows_num")
    @Expose
    private Integer rowsNum;
    @SerializedName("count_records")
    @Expose
    private String countRecords;
    @SerializedName("count_page")
    @Expose
    private Integer countPage;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getRowsNum() {
        return rowsNum;
    }

    public void setRowsNum(Integer rowsNum) {
        this.rowsNum = rowsNum;
    }

    public String getCountRecords() {
        return countRecords;
    }

    public void setCountRecords(String countRecords) {
        this.countRecords = countRecords;
    }

    public Integer getCountPage() {
        return countPage;
    }

    public void setCountPage(Integer countPage) {
        this.countPage = countPage;
    }

}
