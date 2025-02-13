package com.bht.saigonparking.common.spring;

import java.io.IOException;
import java.lang.reflect.Proxy;

import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.lang.NonNull;

import com.bht.saigonparking.common.base.BaseBean;
import com.bht.saigonparking.common.util.LoggingUtil;

import lombok.AllArgsConstructor;


/**
 *
 * Hook into the life cycle of each spring bean (create, destroy,...)
 *
 * @author bht
 */
@AllArgsConstructor
public final class SpringBeanLifeCycle implements BaseBean, DestructionAwareBeanPostProcessor {

    private final String moduleBasePackage;

    @Override
    public void initialize() throws IOException {
        BaseBean.super.initialize();
        LoggingUtil.log(Level.INFO, "SPRING", "BeanCreation", "springBeanLifeCycle");
    }


    @Override
    public void destroy() {
        BaseBean.super.destroy();
        LoggingUtil.log(Level.INFO, "SPRING", "BeanDestruction", "springBeanLifeCycle");
    }


    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) {
        if (!(bean instanceof Proxy) && bean.getClass().getPackage().getName().startsWith(moduleBasePackage)) {
            LoggingUtil.log(Level.INFO, "SPRING", "BeanCreation", beanName);
        }
        return DestructionAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }


    @Override
    public void postProcessBeforeDestruction(@NonNull Object bean, @NonNull String beanName) {
        if (!(bean instanceof Proxy) && bean.getClass().getPackage().getName().startsWith(moduleBasePackage)) {
            LoggingUtil.log(Level.INFO, "SPRING", "BeanDestruction", beanName);
        }
    }
}