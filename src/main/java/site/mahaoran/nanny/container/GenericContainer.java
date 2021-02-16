package site.mahaoran.nanny.container;

import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.beans.BeanLifecycle;
import site.mahaoran.nanny.factory.BeanFactory;
import site.mahaoran.nanny.factory.DefaultBeanFactory;

import java.util.function.Supplier;

public class GenericContainer implements Container{

    private final BeanFactory beanFactory = new DefaultBeanFactory();
    private Container parent;

    @Override
    public void registerBean(Class<?> beanClass) {
        beanFactory.registerBean(new BeanDefinition(beanClass));
    }

    @Override
    public void registerBean(Class<?> beanClass, String beanName) {
        beanFactory.registerBean(new BeanDefinition(beanClass, beanName));
    }

    @Override
    public void registerBean(Class<?> beanClass, String beanName, BeanLifecycle lifecycle) {
        beanFactory.registerBean(new BeanDefinition(beanClass, beanName, lifecycle));
    }

    @Override
    public void registerBean(Object object) {
        beanFactory.registerBean(new BeanDefinition(object));
    }

    @Override
    public void registerBean(String beanName, Object object) {
        beanFactory.registerBean(new BeanDefinition(beanName, object));
    }

    @Override
    public void registerBean(Class<?> beanClass, String beanName, Supplier<?> supplier) {
        beanFactory.registerBean(new BeanDefinition(beanClass, beanName, supplier));
    }

    @Override
    public void registerBean(Class<?> beanClass, String beanName, Supplier<?> supplier, BeanLifecycle lifecycle) {
        beanFactory.registerBean(new BeanDefinition(beanClass, beanName, supplier, lifecycle));
    }

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanFactory.registerBean(beanDefinition);
    }

    @Override
    public void removeBean(String beanName) {
        beanFactory.removeBean(beanName);
    }

    @Override
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String beanName) {
        return beanFactory.getBean(beanClass, beanName);
    }

    @Override
    public boolean containBean(String beanName) {
        return beanFactory.containBean(beanName);
    }

    @Override
    public void clear() {
        beanFactory.clear();
    }

    @Override
    public void setParent(BeanFactory parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Container getParent() {
        return this.parent;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
        this.beanFactory.setParent(parent.getBeanFactory());
    }

    @Override
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
