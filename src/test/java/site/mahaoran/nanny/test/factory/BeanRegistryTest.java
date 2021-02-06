package site.mahaoran.nanny.test.factory;

import site.mahaoran.nanny.test.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.factory.BeanRegistry;

import static org.junit.jupiter.api.Assertions.*;

public class BeanRegistryTest {

    BeanRegistry beanRegistry;

    @BeforeEach
    @Test
    void testRegisterBeanDefinition() {
        beanRegistry = new BeanRegistry();
        beanRegistry.registerBeanDefinition(new BeanDefinition(Benz.class));
        beanRegistry.registerBeanDefinition(new BeanDefinition(Benz.class, "benz"));
        Exception exception = assertThrows(RuntimeException.class, () -> beanRegistry.registerBeanDefinition(new BeanDefinition(Benz.class, "benz")));
        assertEquals("beanName 'benz' 已存在", exception.getMessage());
        beanRegistry.registerBeanDefinition(new BeanDefinition(BMW.class));
        beanRegistry.registerBeanDefinition(new BeanDefinition(BMW.class, "bmw"));
    }

    @Test
    void testRemoveBeanDefinition() {
        beanRegistry.removeBeanDefinition("benz");
        assertEquals(3, beanRegistry.getBeanDefinitionCount());
        Exception exception = assertThrows(RuntimeException.class, () -> beanRegistry.removeBeanDefinition("unknow"));
        assertEquals("beanName 'unknow' 不存在", exception.getMessage());
    }

    @Test
    void testRemoveBeanDefinitions() {
        beanRegistry.removeBeanDefinitions(BMW.class);
        assertEquals(2, beanRegistry.getBeanDefinitionCount());
        Exception exception = assertThrows(RuntimeException.class, () -> beanRegistry.removeBeanDefinitions(String.class));
        assertEquals("beanClass 'class java.lang.String' 不存在", exception.getMessage());
    }

    @Test
    void testGetBeanDefinition() {
        var benz = beanRegistry.getBeanDefinition("benz");
        assertEquals("benz", benz.getBeanName());
        assertNull(beanRegistry.getBeanDefinition("unknow"));

        Exception exception = assertThrows(RuntimeException.class, () -> beanRegistry.getBeanDefinition(Benz.class));
        assertEquals("匹配 'site.mahaoran.nanny.test.model.Benz' 的定义有多个", exception.getMessage());

        Exception exception2 = assertThrows(RuntimeException.class, () -> beanRegistry.getBeanDefinition(Car.class));
        assertEquals("匹配 'site.mahaoran.nanny.test.model.Car' 的定义有多个", exception2.getMessage());

        beanRegistry.removeBeanDefinition("benz");
        var benz2 = beanRegistry.getBeanDefinition(Benz.class);
        assertEquals(Benz.class, benz2.getBeanClass());

        var bmw = beanRegistry.getBeanDefinition(Car.class, "bmw");
        assertEquals("bmw", bmw.getBeanName());

        var bmw2 = beanRegistry.getBeanDefinition(Benz.class, "bmw");
        assertNull(bmw2);
    }
}
