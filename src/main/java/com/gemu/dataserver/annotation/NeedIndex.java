package com.gemu.dataserver.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * 用于标注实体对象字段是否需要索引（是否需要使用该字段进行查询）<br/>
 * Created on: 2017/6/2 <br/>
 *
 * @author: Gemu<br/>
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedIndex {

}
