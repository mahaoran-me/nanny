package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.beans.BeanDefinition;

/**
 * Bean 定义注册表，由 BeanFactory 维护，存储某一 BeanFactory 中注册的所有 BeanDefinition，
 * 并提供索引（beanName，beanClass）
 *
 * @author mahaoran
 * @since 2021-01-18
 */
public interface BeanDefinitionRegistry {


    /**
     * 向注册表中添加一条bean定义记录
     * @param beanDefinition bean定义
     */
    void addBeanDefinition(BeanDefinition beanDefinition);

    /**
     * 根据bean名称从注册表删除bean定义记录
     * @param beanName bean名称
     */
    void removeBeanDefinition(String beanName);

    /**
     * 从注册表中删除指定类型的bean定义记录
     * @param beanClass bean类型
     */
    void removeBeanDefinitions(Class<?> beanClass);

    /**
     * 从注册表中获取指定名称的bean定义
     * @param beanName 需要的bean名称
     * @return 合适的bean定义或者null
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 从注册表中获取指定类型的bean定义
     * @param beanClass 需要的bean类型
     * @return 合适的bean定义或者null
     * @throws RuntimeException 注册表中存在多个符合条件的定义
     */
    BeanDefinition getBeanDefinition(Class<?> beanClass);

    /**
     * 从注册表中获取指定类型和指定名称的bean定义
     * @param beanClass 需要的bean类型
     * @param beanName 需要的bean名称
     * @return 合适的bean定义或null
     */
    BeanDefinition getBeanDefinition(Class<?> beanClass, String beanName);

    /**
     * 判断注册表中是否存在相同名称的bean定义
     * @param beanName bean名称
     * @return 是否存在同名bean
     */
    boolean containBeanDefinition(String beanName);
}
