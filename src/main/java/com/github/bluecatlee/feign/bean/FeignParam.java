package com.github.bluecatlee.feign.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeignParam<T> {

    /**
     * 认证
     */
    private String auth;

    /**
     * 应用key
     */
    private String appKey;

    /**
     * 业务参数
     */
    private T data;

    /**
     * 服务地址
     */
    private String serviceUrl;

}
