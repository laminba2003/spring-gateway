package com.spring.training.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

@Component
public class BeanProcessor implements BeanPostProcessor {

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(bean instanceof RequestMappingHandlerMapping) {
            ((RequestMappingHandlerMapping) bean).setOrder(2);
        }
        return bean;
    }
}
