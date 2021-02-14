package site.mahaoran.nanny.exception;

import java.lang.reflect.Parameter;

public class ParameterMissingException extends BeanException{
    public ParameterMissingException(Class<?> clazz, Parameter parameter) {
        super("创建bean '" + clazz.getName() + "' 时缺少合适的参数 '" + parameter.getName() + "'");
    }
}
