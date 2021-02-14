package site.mahaoran.nanny.exception;

public class MatchedBeanNotUniqueException extends BeanException {
    public MatchedBeanNotUniqueException(Class<?> beanClass) {
        super("与类型 '" + beanClass.getName() + "' 匹配的bean定义有多个");
    }
}
