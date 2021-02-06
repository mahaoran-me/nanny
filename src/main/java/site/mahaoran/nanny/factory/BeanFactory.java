package site.mahaoran.nanny.factory;

public interface BeanFactory {

    <T> T getBean(Class<T> beanClass);

    <T> T getBean(Class<T> beanClass, String beanName);

    Object getBean(String beanName);

}
