package com.ymxc.hospital.common.feign;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Feign客户端注册 注册bean定义
 */
public class FeignClientRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

	/*
	 * 注册bean定义
	 *    AnnotationMetadata表示主启动类上的注解元数据，即主启动类上的所有注解
	 *    BeanDefinitionRegistry Bean定义注册表
	 */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();    // 获取组件扫描器 ClassPathScanningCandidateComponentProvider扫描到的组件需要自行注册为bean
        scanner.setResourceLoader(this.resourceLoader);                        // 设置默认资源加载器
        scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class)); // 设置过滤器 即只扫描包含@FeignClient注解的组件

        // 迭代包
        for (String basePackage : getBasePackages(metadata)) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);      // 扫描获取指定包下的所有符合条件的组件 此处即获取所有@EnableFeignClients指定包下的所有@FeignClient注解的类/接口
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {  // 判断是注解类型的组件
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent; 
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata(); // 获取该组件的元数据信息
                    Assert.isTrue(annotationMetadata.isInterface(),          
                            "@FeignClient can only be specified on an interface");  // 判断组件是否是接口
                    Map<String, Object> attributes = annotationMetadata     
                            .getAnnotationAttributes(FeignClient.class.getCanonicalName());  // 获取@FeignClient注解的属性
                    // 注册FeignClient为bean
                    registerFeignClient(registry, annotationMetadata, attributes);
                }
            }
        }
    }

	/*
	 * 注册所有带@FeignClient注解的接口为bean
	 */
    private void registerFeignClient(BeanDefinitionRegistry registry,
                                     AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {

        String className = annotationMetadata.getClassName();                             // feign客户端(带@FeignClient注解的接口)的class类名
        String alias = annotationMetadata.getClass().getSimpleName() + "FeignClient";     // 类名 + FeignClient 作为bean的别名

        BeanDefinitionBuilder definition = BeanDefinitionBuilder                 // BeanDefinitionBuilder可以动态的创建bean定义
                .genericBeanDefinition(FeignClientFactoryBean.class);            // 使用FactoryBean生成bean
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
//        beanDefinition.setPrimary(true);
        definition.addPropertyValue("url", getUrl(attributes));   // bean设置属性值
        definition.addPropertyValue("type", className);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE); // 设置注入模式为按照类型注入
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);  // 注册bean定义和别名
    }

	/*
	 * 获取@EnableFeignClients注解指定的所有包名
	 */
    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        // 获取包名
        Set<String> basePackages = new HashSet<>();
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableFeignClients.class.getCanonicalName()); // 获取@EnableFeignClients注解的所有属性map
        for (String pkg : (String[]) attributes.get("basePackages")) {                 // 获取获取@EnableFeignClients注解的basePackages属性的值
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        return basePackages;
    }

	/*
	 * 获取@FeignClient注解的属性值 值代表feign请求的服务端的host
	 */
    private String getUrl(Map<String, Object> attributes) {
        String url = resolve((String) attributes.get("value"));
        if (StringUtils.hasText(url)) {
            if (!url.contains("://")) {
                url = "http://" + url;  // 补上http:// 
            }
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }

	/*
	 * 解析值 用于处理占位符
	 */
    private String resolve(String value) {
        if (StringUtils.hasText(value) && this.resourceLoader instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.resourceLoader).getEnvironment()
                    .resolvePlaceholders(value);
        }
        return value;
    }

	/*
	 * 获取扫描器 用于扫描指定包下的候选组件
	 */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {     // false:不使用默认的filter
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {  // 重写判断是否是候选组件的逻辑
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {       // 该组件必须是独立的(即 不是静态内部类)
                    if (!beanDefinition.getMetadata().isAnnotation()) {   // 该组件必须不是注解
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }
}
