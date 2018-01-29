package com.globant.demo.model;

import java.io.Serializable;

public class Work implements Serializable{

    private long id;
    private String key;
    private String description;
    private String workerId;
    private String result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "WORK (" + getId() + " " + getKey() + " : " + getWorkerId() + " / " + getResult();
    }
}
