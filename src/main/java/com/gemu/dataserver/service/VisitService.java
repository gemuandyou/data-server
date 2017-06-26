package com.gemu.dataserver.service;

import com.gemu.dataserver.core.ModifyData;
import com.gemu.dataserver.core.ReadData;
import com.gemu.dataserver.core.WriteData;
import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.Visit;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>用户访问 业务</p>
 * Created on: 2017/6/26
 *
 * @author: <a href="gemuandyou@163.com>gemu</a><br/>
 */
@Service
public class VisitService {

    @Autowired
    WriteData writeData;
    @Autowired
    ReadData readData;
    @Autowired
    ModifyData modifyData;

    /**
     * 添加访问记录
     * @param visit
     */
    public void addVisit(Visit visit) {
        visit.setDate(new Date().getTime());
        visit.setCount(1);
        writeData.write("friends", "visit", visit);
    }

    /**
     * 修改访问记录
     * @param visitId
     * @param visit
     */
    public boolean updVisit(String visitId, Visit visit) throws SourceNotFoundException, EntityNotFoundException, DataAssetsNotFoundException, NoSuchFieldException {
        Map<String, Object> visitMap = new HashMap<String, Object>();
        visitMap.put("date", new Date().getTime());
        if (visit.getUserName() != null && !"".equals(visit.getUserName())) {
            visitMap.put("userName", visit.getUserName());
        }
        if (visit.getAddress() != null && !"".equals(visit.getAddress())) {
            visitMap.put("address", visit.getAddress());
        }
        if (visit.getIp() != null && !"".equals(visit.getIp())) {
            visitMap.put("ip", visit.getIp());
        }
        if (visit.getCount() != null) {
            visitMap.put("count", visit.getCount());
        }
        boolean success = modifyData.update("friends", "visit", visitId, Visit.class, visitMap);
        return success;
    }

    /**
     * 添加访问记录
     * @param visit 不能为空
     * @return
     */
    public EntityPage<BaseData> getVisit(Visit visit) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        Map<String, String> filterMap = new HashMap<String, String>();
        if (visit.getIp() != null && !"".equals(visit.getAddress())) {
            filterMap.put("ip", visit.getIp());
        }
        EntityPage<BaseData> entityPage = readData.filterRead(1, "friends", "visit", filterMap);
        return entityPage;
    }

    /**
     * 获取访问记录
     * @return
     */
    public EntityPage<BaseData> list(int pageNo) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        EntityPage<BaseData> page = readData.read(pageNo, "friends", "visit");
        return page;
    }

}
