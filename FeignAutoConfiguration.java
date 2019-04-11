package com.ymxc.hospital.common.feign;

import feign.Client;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/*
 * feign自动配置类
 */
@Configuration
@ConditionalOnClass(Feign.class)
public class FeignAutoConfiguration {

    @Bean
    @Scope("prototype") // 多例
    @ConditionalOnMissingBean
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

    @Configuration
    @ConditionalOnClass({JacksonDecoder.class, JacksonEncoder.class})
    protected static class JacksonFeignConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public Decoder feignDecoder() {
            return new JacksonDecoder();   // 设置feign的decoder实现为JacksonDecoder
        }

        @Bean
        @ConditionalOnMissingBean
        public Encoder feignEncoder() {
            return new JacksonEncoder();   // 设置feign的encoder实现为JacksonEncoder
        }
    }

    @Configuration
    @ConditionalOnClass(okhttp3.OkHttpClient.class)
    @ConditionalOnProperty(value = "feign.okhttp.enabled", matchIfMissing = true)
    protected static class OkHttpFeignConfiguration {

        @Autowired(required = false)
        private okhttp3.OkHttpClient okHttpClient;

        @Bean
        @ConditionalOnMissingBean(Client.class)
        public Client feignClient() {
            if (this.okHttpClient != null) {
                return new OkHttpClient(this.okHttpClient);   // 设置feign的http实现为OkHttpClient
            }
            return new OkHttpClient();
        }
    }

}
