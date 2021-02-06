package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.beans.BeanDefinition;

import java.util.*;

public class BeanRegistry {

    private final List<BeanDefinition> allBeanDefinitions = new LinkedList<>();

    private final Map<String, BeanDefinition> indexByBeanName = new HashMap<>();

    private final Map<Class<?>, List<BeanDefinition>> indexByBeanClass = new HashMap<>();

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        var beanName = beanDefinition.getBeanName();
        var definition = indexByBeanName.get(beanName);
        if (definition != null) {
            throw new RuntimeException("beanName '" + beanName + "' 已存在");
        }
        allBeanDefinitions.add(beanDefinition);
        indexByBeanName.put(beanName, beanDefinition);
        var beanClass = beanDefinition.getBeanClass();
        var definitions = indexByBeanClass.computeIfAbsent(beanClass, key -> new LinkedList<>());
        definitions.add(beanDefinition);
    }

    public void removeBeanDefinition(String beanName) {
        var definition = indexByBeanName.get(beanName);
        if (definition == null) {
            throw new RuntimeException("beanName '" + beanName + "' 不存在");
        }
        indexByBeanClass.get(definition.getBeanClass()).remove(definition);
        indexByBeanName.remove(beanName);
        allBeanDefinitions.remove(definition);
    }

    public void removeBeanDefinitions(Class<?> beanClass) {
        var beanDefinitions = indexByBeanClass.get(beanClass);
        if (beanDefinitions == null || beanDefinitions.size() == 0) {
            throw new RuntimeException("beanClass '" + beanClass + "' 不存在");
        }
        beanDefinitions.forEach(definition -> {
            allBeanDefinitions.remove(definition);
            indexByBeanName.remove(definition.getBeanName());
        });
        indexByBeanClass.remove(beanClass);
    }

    public BeanDefinition getBeanDefinition(String beanName) {
        return indexByBeanName.get(beanName);
    }

    public BeanDefinition getBeanDefinition(Class<?> beanClass) {
        var beanDefinitions = indexByBeanClass.get(beanClass);
        if (beanDefinitions != null && beanDefinitions.size() == 1) {
            return beanDefinitions.get(0);
        }
        if (beanDefinitions != null && beanDefinitions.size() > 1) {
            throw new RuntimeException("匹配 '" + beanClass.getName() + "' 的定义有多个");
        }
        var count = 0;
        BeanDefinition beanDefinition = null;
        for (var entry : indexByBeanClass.entrySet()) {
            if (beanClass.isAssignableFrom(entry.getKey())) {
                if (entry.getValue() != null && entry.getValue().size() > 0) {
                    count += entry.getValue().size();
                    if (count > 1) break;
                    beanDefinition = entry.getValue().get(0);
                }
            }
        }
        if (count > 1) {
            throw new RuntimeException("匹配 '" + beanClass.getName() + "' 的定义有多个");
        }
        return count == 1 ? beanDefinition : null;
    }

    public BeanDefinition getBeanDefinition(Class<?> beanClass, String beanName) {
        var beanDefinition = indexByBeanName.get(beanName);
        if (beanDefinition == null) return null;
        return beanClass.isAssignableFrom(beanDefinition.getBeanClass()) ? beanDefinition : null;
    }

    public int getBeanDefinitionCount() {
        return allBeanDefinitions.size();
    }

}
