package com.ymxc.hospital.common.feign;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/*
 * feign客户端bean 
 *    FactoryBean 工厂bean的方式创建
 *    InitializingBean bean初始化之后的处理
 *    ApplicationContextAware 获取上下文
 */
@Data
class FeignClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Class<?> type;

    private String url;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        // Assert.hasText(this.url, "url must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    protected Feign.Builder feign() {
        Feign.Builder builder = get(Feign.Builder.class);

        Encoder encoder = getOptional(Encoder.class);
        if (encoder != null) {
            builder.encoder(encoder);   // 设置feign的encoder
        }

        Decoder decoder = getOptional(Decoder.class);
        if (decoder != null) {
            builder.decoder(decoder);   // 设置feign的decoder
        }

        Client client = getOptional(Client.class);
        if (client != null) {
            builder.client(client);     // 设置feign的http客户端
        }

        Contract contract = getOptional(Contract.class);
        if (contract != null) {
            builder.contract(contract);  // 设置feign的Contract
        }
        Logger.Level level = getOptional(Logger.Level.class);
        if (level != null) {
            builder.logLevel(level);     // 设置feign的日志等级
        }
        Retryer retryer = getOptional(Retryer.class);
        if (retryer != null) {
            builder.retryer(retryer);    // 设置feign的重试器 
        }
        ErrorDecoder errorDecoder = getOptional(ErrorDecoder.class);
        if (errorDecoder != null) {
            builder.errorDecoder(errorDecoder);  // 设置feign的异常处理
        }
        Request.Options options = getOptional(Request.Options.class);
        if (options != null) {
            builder.options(options);            // 设置feign的request
        }

        Map<String, RequestInterceptor> requestInterceptors = getOptionals(RequestInterceptor.class);
        if (requestInterceptors != null) {
            builder.requestInterceptors(requestInterceptors.values());  // 设置feign的请求拦截器
        }
        return builder;
    }

	// 根据bean的类型获取单个bean 如果不存在 则抛出异常
    protected <T> T get(Class<T> type) {
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, type).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, type);
        } else {
            throw new IllegalStateException("No service found of type " + type);
        }
    }

	// 根据bean的类型获取单个bean 如果不存在 返回null
    protected <T> T getOptional(Class<T> type) {
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, type).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, type);
        }
        return null;
    }

	// 根据bean的类型返回所有bean 如果不存在 返回null
    protected <T> Map<String, T> getOptionals(Class<T> type) {
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, type).length > 0) {
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, type);
        }
        return null;
    }

    @Override
    public Object getObject() {
        return feign().target(type, url);
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}