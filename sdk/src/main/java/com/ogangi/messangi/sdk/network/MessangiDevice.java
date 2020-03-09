package com.ogangi.messangi.sdk.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessangiDevice {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("content")
    @Expose
    private List<Content> content = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;
    @SerializedName("reference")
    @Expose
    private String reference;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
