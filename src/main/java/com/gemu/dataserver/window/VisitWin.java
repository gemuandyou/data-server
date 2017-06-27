package com.gemu.dataserver.window;

import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.Visit;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.VisitService;
import com.gemu.dataserver.tool.AddressUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * 访问记录 接口
 * Created on: 2017/6/26
 *
 * @author: <a href="mailto: gemuandyou@163.com>gemu</a><br/>
 */
@RestController
@RequestMapping("visit")
public class VisitWin {

    @Autowired
    VisitService visitService;

    @PostMapping("")
    public void visit(HttpServletRequest request) {
        String ip = request.getHeader("Address");
        if (ip != null) {
            int i = ip.lastIndexOf(":");
            if (ip.length() > i + 1) {
                ip = ip.substring(i + 1);
            }
        }
        String user = request.getHeader("User");
        if (!"gemu".equals(user)) {
            Visit visit = new Visit();
            visit.setIp(ip);
            try {
                String addresses = AddressUtils.getAddresses("ip=" + ip, "utf-8");
                visit.setAddress(addresses);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            visit.setUserName(user);
            try {
                EntityPage<BaseData> page = visitService.getVisit(visit);
                if (page.getEntries().size() <= 0) {
                    visitService.addVisit(visit);
                } else {
                    Visit updVisit = (Visit) page.getEntries().get(0);
                    updVisit.setCount(updVisit.getCount() + 1);
                    if (user != null && !"".equals(user)) {
                        updVisit.setUserName(user);
                    }
                    visitService.updVisit(updVisit.getId(), updVisit);
                }
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            } catch (DataAssetsNotFoundException e) {
                e.printStackTrace();
                visitService.addVisit(visit);
            } catch (SourceNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 列出老铁们的访问情况
     * @param verification
     * @param pageNo
     * @return
     */
    @RequestMapping(value = "list/{verification}/{pageNo}", method = RequestMethod.GET)
    public EntityPage<BaseData> list(@PathVariable("verification") String verification, @PathVariable int pageNo) {
        if (!"wyf".equals(verification)) {
            return null;
        }
        EntityPage<BaseData> page = null;
        try {
            page = visitService.list(pageNo);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        }
        return page;
    }

}
