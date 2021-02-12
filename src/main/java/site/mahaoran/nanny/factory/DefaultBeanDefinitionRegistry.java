package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.beans.BeanDefinition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final List<BeanDefinition> beanDefinitions = new LinkedList<>();
    private final Map<String, BeanDefinition> nameIndex = new HashMap<>();
    private final Map<Class<?>, List<BeanDefinition>> typeIndex = new HashMap<>();

    @Override
    public void addBeanDefinition(BeanDefinition beanDefinition) {
        var beanName = beanDefinition.getBeanName();
        if (containBeanDefinition(beanName)) {
            throw new RuntimeException(beanName + "已存在");
        }
        beanDefinitions.add(beanDefinition);
        nameIndex.put(beanName, beanDefinition);
        var definitions = typeIndex.computeIfAbsent(beanDefinition.getBeanClass(), key -> new LinkedList<>());
        definitions.add(beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        if (containBeanDefinition(beanName)) {
            var beanDefinition = nameIndex.get(beanName);
            beanDefinitions.remove(beanDefinition);
            nameIndex.remove(beanName);
            typeIndex.get(beanDefinition.getBeanClass()).remove(beanDefinition);
        }
    }

    @Override
    public void removeBeanDefinitions(Class<?> beanClass) {
        var definitions = typeIndex.get(beanClass);
        if (definitions == null || definitions.size() == 0) {
            return;
        }
        definitions.forEach(definition -> {
            beanDefinitions.remove(definition);
            nameIndex.remove(definition.getBeanName());
        });
        typeIndex.remove(beanClass);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return nameIndex.get(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> beanClass) {
        var definitions = typeIndex.get(beanClass);
        if (definitions != null && definitions.size() == 1) {
            return definitions.get(0);
        }
        if (definitions != null && definitions.size() > 1) {
            throw new RuntimeException(beanClass.getName() + "有多个匹配的bean定义");
        }
        var count = 0;
        BeanDefinition definition = null;
        for (var entry : typeIndex.entrySet()) {
            if (beanClass.isAssignableFrom(entry.getKey())) {
                if (entry.getValue() != null && entry.getValue().size() > 0) {
                    count += entry.getValue().size();
                    if (count > 1) break;
                    definition = entry.getValue().get(0);
                }
            }
        }
        if (count > 1) {
            throw new RuntimeException("匹配 '" + beanClass.getName() + "' 的定义有多个");
        }
        return count == 1 ? definition : null;
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> beanClass, String beanName) {
        var definition = nameIndex.get(beanName);
        if (definition == null) return null;
        return beanClass.isAssignableFrom(definition.getBeanClass()) ? definition : null;
    }

    @Override
    public boolean containBeanDefinition(String beanName) {
        return nameIndex.containsKey(beanName);
    }
}
