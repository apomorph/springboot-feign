package com.ymxc.hospital.common.feign;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {
    /*
        所有需要动态指定url地址的服务调用在使用该注解时不要设置value属性值 在@RequestLine注解中指定url全路径(不带http://)
        底层代码校验url不能为null和空串,为兼容使用过该注解的老代码 此处设置默认值 但是不支持https
        fixedBy: bluecat lee 2019.4.09
     */
    String value() default "http://";
}
