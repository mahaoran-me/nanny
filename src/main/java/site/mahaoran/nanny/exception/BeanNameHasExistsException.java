package site.mahaoran.nanny.exception;

public class BeanNameHasExistsException extends BeanException {
    public BeanNameHasExistsException(String beanName) {
        super("注册表中已经存在名为 '" + beanName + "' 的bean定义");
    }
}
