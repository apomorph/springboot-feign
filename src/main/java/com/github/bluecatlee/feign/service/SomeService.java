package com.github.bluecatlee.feign.service;

import com.github.bluecatlee.feign.annotation.FeignClient;
import com.github.bluecatlee.feign.bean.FeignResult;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignClient
public interface SomeService {

    @RequestLine("POST feign.yegoo.cc/a")
    // @Headers("Content-Type:application/x-www-form-urlencoded")
    @Body("param={param}")
    FeignResult mform(@Param("param") String param);

    @RequestLine("POST feign.yegoo.cc/b")
    @Headers("Content-Type:application/json")
    @Body("{param}")
    FeignResult mjson(@Param("param") String jsonStr);

}
