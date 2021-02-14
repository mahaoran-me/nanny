package site.mahaoran.nanny.exception;

public class BeanIsAbstractException extends BeanException {
    public BeanIsAbstractException(Class<?> clazz) {
        super("'" + clazz.getName() + "' 是抽象类或接口，不能实例化");
    }
}
