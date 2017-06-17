package com.gemu.dataserver.entity.auxiliary.param;

import java.util.Map;

/**
 * 获取故事列表过滤条件
 * Created by gemu on 03/06/2017.
 */
public class PageCondition {

    private int pageNo;

    Map<String, String> filter;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }
}
