package com.gemu.dataserver.entity;

import com.gemu.dataserver.annotation.NeedIndex;

/**
 * 访问记录
 * Created on: 2017/6/26
 *
 * @author: <a href="mailto: gemuandyou@163.com>gemu</a><br/>
 */
public class Visit extends BaseData {

    /**
     * 用户名
     */
    @NeedIndex
    private String userName;
    /**
     * 用户访问地址
     */
    @NeedIndex
    private String address;
    /**
     * 用户访问IP
     */
    @NeedIndex
    private String ip;
    /**
     * 访问次数
     */
    @NeedIndex
    private Integer count;
    /**
     * 访问日期
     */
    @NeedIndex
    private Long date;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
