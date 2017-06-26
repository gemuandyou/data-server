package com.gemu.dataserver.config;

import com.gemu.dataserver.interceptor.CustomInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * webmvc配置类
 * Created on: 2017/6/26
 *
 * @author: <a href="mailto: gemuandyou@163.com">gemu</a><br/>
 */
@Configuration
@ComponentScan("com.gemu.dataserver.interceptor")
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Autowired
    CustomInterceptor customInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
