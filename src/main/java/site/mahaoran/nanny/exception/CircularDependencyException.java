package site.mahaoran.nanny.exception;

public class CircularDependencyException extends BeanException{
    public CircularDependencyException(String beanName) {
        super("创建bean '" + beanName + "' 时遇到循环依赖");
    }
}
