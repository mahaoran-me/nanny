package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.beans.InjectValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Supplier;

public class DefaultBeanFactory implements BeanFactory{

    private final BeanDefinitionRegistry beanDefinitionRegistry = new DefaultBeanDefinitionRegistry();
    private final SingletonCache singletonCache = new DefaultSingletonCache();
    private BeanFactory parent;

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanDefinitionRegistry.addBeanDefinition(beanDefinition);
    }

    @Override
    public void removeBean(String beanName) {
        beanDefinitionRegistry.removeBeanDefinition(beanName);
        singletonCache.removeSingleton(beanName);
    }

    @Override
    public Object getBean(String beanName) {
        if (!containBean(beanName)) {
            if (parent != null) {
                return parent.getBean(beanName);
            } else {
                return null;
            }
        }
        return doGetBean(Object.class, beanDefinitionRegistry.getBeanDefinition(beanName));
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        var beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanClass);
        if (beanDefinition == null) {
            if (parent != null) {
                return parent.getBean(beanClass);
            } else {
                return null;
            }
        }
        return doGetBean(beanClass, beanDefinition);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String beanName) {
        var beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanClass, beanName);
        if (beanDefinition == null) {
            if (parent != null) {
                return parent.getBean(beanClass, beanName);
            } else {
                return null;
            }
        }
        return doGetBean(beanClass, beanDefinition);
    }

    private <T> T doGetBean(Class<T> requireType, BeanDefinition beanDefinition) {
        var beanName = beanDefinition.getBeanName();
        Object bean = null;
        if (beanDefinition.isSingleton()) {
            bean = singletonCache.getSingleton(beanName);
            if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
                singletonCache.addWholeSingleton(beanName, bean);
                return (T) bean;
            }
            bean = beanDefinition.getInstance();
            if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
                singletonCache.addWholeSingleton(beanName, bean);
                return (T) bean;
            }
            Supplier<?> instanceSupplier = beanDefinition.getInstanceSupplier();
            if (instanceSupplier != null) {
                bean = instanceSupplier.get();
                if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
                    singletonCache.addWholeSingleton(beanName, bean);
                    return (T) bean;
                }
            }
            bean = singletonCache.getSingleton(beanName, () -> createBean(beanDefinition));
            if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
                singletonCache.addWholeSingleton(beanName, bean);
                return (T) bean;
            }
        } else {
            Supplier<?> instanceSupplier = beanDefinition.getInstanceSupplier();
            bean = instanceSupplier.get();
            if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
                singletonCache.addWholeSingleton(beanName, bean);
                return (T) bean;
            }
        }
        return bean == null ? null : (T) bean;
    }

    /**
     * 通过反射创建一个实例对象
     * @param beanDefinition bean定义
     * @return 实例对象
     */
    private Object createBean(BeanDefinition beanDefinition) {
        if (beanDefinition.isAbstract()) {
            throw new RuntimeException("抽象类或接口不能创建实例");
        }
        Class<?> beanClass = beanDefinition.getBeanClass();
        var beanName = beanDefinition.getBeanName();
        Object bean = null;
        // 获取全部构造函数
        var constructors = beanClass.getConstructors();
        // 找出参数最多的构造函数，之后通过该函数进行实例化。
        Constructor<?> constructor = null;
        int max = 0;
        for (var c : constructors) {
            if (c.getParameterCount() >= max) {
                max = c.getParameterCount();
                constructor = c;
            }
        }
        assert constructor != null;
        // 找出构造函数的所需的参数
        var parameters = constructor.getParameters();
        // 如果需要的参数为0，直接实例化
        if (parameters.length == 0) {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        // 解析构造函数参数，并进行合适的依赖注入
        Object[] arguments = new Object[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            var name = parameters[i].getName();
            var injectValue = beanDefinition.getConstructorArguments().get(name);
            if (injectValue == null) {
                arguments[i] = getBean(parameters[i].getClass());
            } else if (injectValue.getValue() != null) {
                arguments[i] = injectValue.getValue();
            } else if (injectValue.getName() != null) {
                arguments[i] = getBean(parameters[i].getClass(), injectValue.getName());
            } else {
                arguments[i] = getBean(parameters[i].getClass());
            }
            if (parameters[i] == null) {
                throw new RuntimeException("类型为 '" + beanClass.getName() + "' 的bean构造时无法匹配合适的参数：" + parameters[i].getName());
            }
        }
        try {
            // 实例化
            bean = constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (bean == null) {
            throw new RuntimeException("实例化失败");
        }
        Object finalBean = bean;
        singletonCache.addSingletonSupplier(beanName, () -> aopCheck(finalBean));
        // 属性注入
        var fields = beanClass.getDeclaredFields();
        for (var field : fields) {
            var name = field.getName();
            var injectValue = beanDefinition.getInjectProperties().get(name);
            if (injectValue == null) continue;
            if (injectValue.getValue() != null) {
                field.setAccessible(true);
                try {
                    field.set(bean, injectValue.getValue());
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (injectValue.getName() != null) {
                field.setAccessible(true);
                try {
                    field.set(bean, getBean(field.getClass(), injectValue.getName()));
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                field.set(bean, getBean(field.getClass()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    Object aopCheck(Object object) {
        return object;
    }

    @Override
    public boolean containBean(String beanName) {
        return beanDefinitionRegistry.containBeanDefinition(beanName);
    }

    @Override
    public void setParent(BeanFactory parent) {
        this.parent = parent;
    }

    @Override
    public BeanFactory getParent() {
        return parent;
    }
}
