package com.ymxc.hospital.common.feign;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/*
 * 使用在主启动类上
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignClientRegistrar.class)
public @interface EnableFeignClients {
    String[] basePackages() default {};
}
