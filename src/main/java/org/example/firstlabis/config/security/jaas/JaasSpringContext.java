package org.example.firstlabis.config.security.jaas;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Вспомогательный компонент для доступа к Spring-бинами в компонентах, которые не менеджатся Spring-ом
 * 
 * Класс JaasSpringContext является вспомогательным компонентом, который решает важную проблему интеграции между JAAS (Java Authentication and Authorization Service) и Spring Framework. Вот почему он нужен:
 *
 * Проблема доступа к Spring-бинам:
 * - JAAS работает вне контекста Spring
 * - Это означает, что компоненты JAAS не могут напрямую использовать Spring-бины через @Autowired или другие механизмы внедрения зависимостей Spring
 *
 * Решение проблемы:
 * - JaasSpringContext реализует интерфейс ApplicationContextAware
 * - При инициализации Spring сохраняет ссылку на свой ApplicationContext в статическое поле
 * - Это позволяет получать доступ к Spring-бинам из любого места в приложении, даже из компонентов, которые не управляются Spring
 *
 * Использование:
 * - В коде, где нужно получить доступ к Spring-бину, можно использовать:
 *
 * ```java
 *    SomeBean bean = JaasSpringContext.getBean(SomeBean.class);
 * ```
 *
 * - Это особенно полезно в JAAS-компонентах, которые не могут использовать обычные механизмы внедрения зависимостей Spring
 * 
 * @author amhphyxs
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