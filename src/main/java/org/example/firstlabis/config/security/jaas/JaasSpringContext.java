package org.example.firstlabis.config.security.jaas;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Вспомогательный компонент для доступа к Spring-бинами в компонентах, которые не менеджатся Spring-ом
 */
@Component
public class JaasSpringContext implements ApplicationContextAware {
    
    private static ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    
    /**
     * Получить бин по его классу.
     * @param beanType класс бина
     * @return инстанс бина
     */
    public static <T> T getBean(Class<T> beanType) {
        return context.getBean(beanType);
    }
} 