package com.gemu.dataserver.window;

import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
