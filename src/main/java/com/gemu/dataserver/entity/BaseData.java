package com.gemu.dataserver.entity;

import java.io.Serializable;

/**
 * 基础数据
 * Created by gemu on 29/05/2017.
 */
public class BaseData implements Serializable {

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
