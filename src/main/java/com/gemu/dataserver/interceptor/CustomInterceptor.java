package com.gemu.dataserver.interceptor;

import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.Visit;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.VisitService;
import com.gemu.dataserver.tool.AddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 拦截器
 * Created on: 2017/6/26
 *
 * @author: <a href="mailto: gemuandyou@163.com>gemu</a><br/>
 */
@Component
public class CustomInterceptor implements HandlerInterceptor {

    @Autowired
    VisitService visitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 前端请求使用了代理方式，所以这里获取的均是前端服务器IP
//        accessRecord(request);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void accessRecord(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/story/getPage")) return;
        String user = request.getHeader("user");
        if (!"gemu".equals(user)) {
            Visit visit = new Visit();
            String ip = request.getRemoteAddr();
            visit.setIp(ip);
            try {
                String addresses = AddressUtils.getAddresses(ip, "utf-8");
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

}
