package com.gemu.dataserver.entity;

import com.gemu.dataserver.annotation.NeedIndex;

/**
 * 用户
 * Created by gemu on 11/06/2017.
 */
public class Friend extends BaseData {

    /**
     * 登陆名
     */
    @NeedIndex
    private String userName;
    /**
     * 真实名
     */
    private String realName;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 密码
     */
    private String password;
    /**
     * 最后一次登陆时间
     */
    private Long lastLoginTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "userName='" + userName + '\'' +
                ", realName='" + realName + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }
}
