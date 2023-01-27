package com.spring.training.util;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

public class BeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(bean instanceof RequestMappingHandlerMapping) {
            ((RequestMappingHandlerMapping) bean).setOrder(2);
        }
        return bean;
    }
}
