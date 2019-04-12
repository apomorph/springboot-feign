package com.github.bluecatlee.feign.service;


import com.github.bluecatlee.feign.annotation.FeignClient;
import feign.Param;
import feign.RequestLine;
import lombok.Data;

@FeignClient("http://t.weather.sojson.com")
public interface WeatherService {

    @Data
    class Weatcher {
        private String time;
        private CityInfo cityInfo;
    }

    @Data
    class CityInfo {
        private String city;
    }

    @RequestLine("GET /api/weather/city/{code}")
    Weatcher w(@Param("code") String code);

}
