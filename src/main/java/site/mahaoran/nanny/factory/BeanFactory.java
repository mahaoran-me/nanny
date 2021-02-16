package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.beans.BeanDefinition;

/**
 * bean工厂，为管理bean提供一个统一的接口，
 *
 * @author mahaoran
 * @since 2021-01-22
 */
public interface BeanFactory {

    /**
     * 向工厂中注册一个bean
     * @param beanDefinition bean定义
     */
    void registerBean(BeanDefinition beanDefinition);

    /**
     * 从工厂中移除指定的bean
     * @param beanName bean名称
     */
    void removeBean(String beanName);

    /**
     * 从工厂中获取指定的bean
     * @param beanName bean名称
     * @return bean
     */
    Object getBean(String beanName);

    /**
     * 从工厂中获取指定的bean
     * @param beanClass bean类型
     * @param <T> bean泛型
     * @return bean
     */
    <T> T getBean(Class<T> beanClass);

    /**
     * 从工厂中获取指定的bean
     * @param beanClass bean类型
     * @param beanName bean名称
     * @param <T> bean泛型
     * @return bean
     */
    <T> T getBean(Class<T> beanClass, String beanName);

    /**
     * 判断工厂中是否包含指定的bean
     * @param beanName bean名称
     * @return 是否包含指定的bean
     */
    boolean containBean(String beanName);

    /**
     * 清除工厂中的所有bean
     */
    void clear();

    /**
     * 为工厂指定一个父工厂
     * @param parent 父工厂
     */
    void setParent(BeanFactory parent);

    /**
     * 获取父工厂
     * @return 父工厂
     */
    BeanFactory getParent();

}
