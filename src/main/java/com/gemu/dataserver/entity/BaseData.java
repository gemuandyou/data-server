package com.gemu.dataserver.entity;

import com.gemu.dataserver.annotation.NeedIndex;

import java.io.Serializable;

/**
 * 基础数据
 * Created by gemu on 29/05/2017.
 */
public class BaseData implements Serializable {

    @NeedIndex
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
