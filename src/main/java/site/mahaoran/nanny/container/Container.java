package site.mahaoran.nanny.container;

import site.mahaoran.nanny.beans.BeanLifecycle;
import site.mahaoran.nanny.factory.BeanFactory;

import java.util.function.Supplier;

public interface Container extends BeanFactory {

    void registerBean(Class<?> beanClass);

    void registerBean(Class<?> beanClass, String beanName);

    void registerBean(Class<?> beanClass, String beanName, BeanLifecycle lifecycle);

    void registerBean(Object object);

    void registerBean(String beanName, Object object);

    void registerBean(Class<?> beanClass, String beanName, Supplier<?> supplier);

    void registerBean(Class<?> beanClass, String beanName, Supplier<?> supplier, BeanLifecycle lifecycle);

    void setParent(Container container);

    BeanFactory getBeanFactory();

}
