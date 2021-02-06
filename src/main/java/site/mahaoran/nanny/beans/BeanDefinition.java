package site.mahaoran.nanny.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BeanDefinition {

    private Class<?> beanClass;

    private String beanName;

    private BeanLifecycle lifecycle;

    private final Map<String, Object> constructorArguments;

    private final Map<String, Object> injectProperties;

    private Supplier<?> instanceSupplier;

    private Object instance;

    public BeanDefinition(Class<?> beanClass) {
        this(beanClass, beanClass.getName());
    }

    public BeanDefinition(Class<?> beanClass, String beanName) {
        this(beanClass, beanName, BeanLifecycle.SINGLETON);
    }

    public BeanDefinition(Class<?> beanClass, BeanLifecycle lifecycle) {
        this(beanClass, beanClass.getName(), lifecycle);
    }

    public BeanDefinition(Object instance) {
        this(instance.getClass(), instance.getClass().getName(), BeanLifecycle.SINGLETON);
        this.instance = instance;
    }

    public BeanDefinition(String beanName, Object instance) {
        this(instance.getClass(), beanName, BeanLifecycle.SINGLETON);
        this.instance = instance;
    }

    public BeanDefinition(Class<?> beanClass, String beanName, BeanLifecycle lifecycle) {
        this.beanClass = beanClass;
        this.beanName = beanName;
        this.lifecycle = lifecycle;
        this.constructorArguments = new HashMap<>();
        this.injectProperties = new HashMap<>();
    }

    public BeanDefinition(Class<?> beanClass, String beanName, Supplier<?> instanceSupplier) {
        this.beanClass = beanClass;
        this.beanName = beanName;
        this.lifecycle = BeanLifecycle.SINGLETON;
        this.constructorArguments = new HashMap<>();
        this.injectProperties = new HashMap<>();
        this.instanceSupplier = instanceSupplier;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public BeanLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(BeanLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Map<String, Object> getConstructorArguments() {
        return constructorArguments;
    }

    public Map<String, Object> getInjectProperties() {
        return injectProperties;
    }

    public Supplier<?> getInstanceSupplier() {
        return instanceSupplier;
    }

    public void setInstanceSupplier(Supplier<?> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClass=" + beanClass +
                ", beanName='" + beanName + '\'' +
                ", lifecycle=" + lifecycle +
                '}';
    }
}
