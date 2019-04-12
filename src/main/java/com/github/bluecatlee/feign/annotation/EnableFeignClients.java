package com.github.bluecatlee.feign.annotation;

import com.github.bluecatlee.feign.configuration.FeignClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignClientRegistrar.class)
public @interface EnableFeignClients {
    String[] basePackages() default {};
}
