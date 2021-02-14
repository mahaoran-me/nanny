package site.mahaoran.nanny.exception;

public class NoPublicConstructorException extends BeanException{
    public NoPublicConstructorException(Class<?> clazz) {
        super("'" + clazz.getName() + "' 没有可用的构造函数");
    }
}
