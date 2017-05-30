package com.gemu.dataserver.entity.auxiliary;

import com.gemu.dataserver.entity.BaseData;

import java.util.List;

/**
 * 分页实体对象列表数据
 * Created by gemu on 30/05/2017.
 */
public class EntityPage<T extends BaseData> {

    /**
     * 当前页码
     */
    private int pageNo;
    /**
     * 页的大小
     */
    public static final int pageSize = 20;
    /**
     * 总数
     */
    private int totalCount;

    /**
     * 实体对象集合
     */
    private List<T> entries;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getEntries() {
        return entries;
    }

    public void setEntries(List<T> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "EntityPage{" +
                "pageNo=" + pageNo +
                ", totalCount=" + totalCount +
                ", entries=" + entries +
                '}';
    }
}
