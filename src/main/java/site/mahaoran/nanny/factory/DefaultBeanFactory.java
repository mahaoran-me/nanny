package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.exception.BeanIsAbstractException;
import site.mahaoran.nanny.exception.NoPublicConstructorException;
import site.mahaoran.nanny.exception.ParameterMissingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public class DefaultBeanFactory implements BeanFactory{

    private final BeanRegistry beanRegistry = new DefaultBeanRegistry();
    private final SingletonCache singletonCache = new DefaultSingletonCache();
    private BeanFactory parent;

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanRegistry.addBeanDefinition(beanDefinition);
    }

    @Override
    public void removeBean(String beanName) {
        beanRegistry.removeBeanDefinition(beanName);
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
        return doGetBean(Object.class, beanRegistry.getBeanDefinition(beanName));
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        var beanDefinition = beanRegistry.getBeanDefinition(beanClass);
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
        var beanDefinition = beanRegistry.getBeanDefinition(beanClass, beanName);
        if (beanDefinition == null) {
            if (parent != null) {
                return parent.getBean(beanClass, beanName);
            } else {
                return null;
            }
        }
        return doGetBean(beanClass, beanDefinition);
    }

    @SuppressWarnings("unchecked")
    private <T> T doGetBean(Class<T> requireType, BeanDefinition beanDefinition) {
        var beanName = beanDefinition.getBeanName();
        Object bean;
        if (beanDefinition.isSingleton()) {
            bean = singletonCache.getSingleton(beanName);
            if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
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
            if (instanceSupplier != null) {
                bean = instanceSupplier.get();
                if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
                    return (T) bean;
                }
            }
            bean = singletonCache.getSingleton(beanName, () -> createBean(beanDefinition));
            if (bean != null && requireType.isAssignableFrom(bean.getClass())) {
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
            throw new BeanIsAbstractException(beanDefinition.getBeanClass());
        }
        Class<?> beanClass = beanDefinition.getBeanClass();
        var beanName = beanDefinition.getBeanName();
        Object bean = null;
        // 获取全部构造函数
        var constructors = beanClass.getConstructors();
        // 找出参数最多的构造函数，之后通过该函数进行实例化。
        var constructor = Arrays.stream(constructors)
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new NoPublicConstructorException(beanClass));
        // 获取构造函数的所需的参数
        var parameters = constructor.getParameters();
        // 解析构造函数参数，并进行合适的依赖注入
        Object[] arguments = new Object[parameters.length];
        for (var i = 0; i < parameters.length; i++) {
            var name = parameters[i].getName();
            var injectValue = beanDefinition.getConstructorArguments().get(name);
            if (injectValue == null) {
                // 没有配置注入属性则按照参数类型从BeanFactory中寻找合适的bean进行注入
                arguments[i] = getBean(parameters[i].getType());
            } else if (injectValue.getValue() != null) {
                // 如果有配置的对象则直接将该对象注入
                arguments[i] = injectValue.getValue();
            } else if (injectValue.getName() != null) {
                // 如果有配置的bean名称则按照参数类型和bean名称从BeanFactory中寻找合适的bean注入
                arguments[i] = getBean(parameters[i].getType(), injectValue.getName());
            } else {
                // 如果没有指定注入对象或注入bean名称，则根据参数类型从BeanFactory中寻找合适的bean进行注入
                arguments[i] = getBean(parameters[i].getType());
            }
            // 如果最终还是没有找到合适的bean，则抛出异常。
            if (arguments[i] == null) {
                throw new ParameterMissingException(beanClass, parameters[i]);
            }
        }
        try {
            // 通过构造函数进行实例化
            bean = constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        // 将实例化的半成品对象放入三级缓存
        Object finalBean = bean;
        singletonCache.addSingletonSupplier(beanName, () -> aopProcess(finalBean));
        // 属性注入
        var fields = beanClass.getDeclaredFields();
        for (var field : fields) {
            var name = field.getName();
            var injectValue = beanDefinition.getInjectProperties().get(name);
            try {
                field.setAccessible(true);
                if (injectValue == null) continue;
                if (injectValue.getValue() != null) {
                    field.set(bean, injectValue.getValue());
                } else if (injectValue.getName() != null) {
                    field.set(bean, getBean(field.getType(), injectValue.getName()));
                } else {
                    field.set(bean, getBean(field.getType()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    private Object aopProcess(Object object) {
        return object;
    }

    @Override
    public boolean containBean(String beanName) {
        return beanRegistry.containBeanDefinition(beanName);
    }

    @Override
    public void clear() {
        singletonCache.clearSingleton();
        beanRegistry.clearBeanDefinitions();
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
