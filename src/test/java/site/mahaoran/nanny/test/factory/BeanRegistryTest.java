package site.mahaoran.nanny.test.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.exception.BeanNameHasExistsException;
import site.mahaoran.nanny.exception.MatchedBeanNotUniqueException;
import site.mahaoran.nanny.factory.BeanRegistry;
import site.mahaoran.nanny.factory.DefaultBeanRegistry;
import site.mahaoran.nanny.test.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class BeanRegistryTest {

    BeanRegistry beanRegistry;

    @BeforeEach
    @Test
    void testAddBeanDefinition() {
        beanRegistry = new DefaultBeanRegistry();
        beanRegistry.addBeanDefinition(new BeanDefinition(Benz.class));
        beanRegistry.addBeanDefinition(new BeanDefinition(Benz.class, "benz"));
        assertThrows(
                BeanNameHasExistsException.class,
                () -> beanRegistry.addBeanDefinition(new BeanDefinition(Benz.class, "benz"))
        );
        beanRegistry.addBeanDefinition(new BeanDefinition(BMW.class));
        beanRegistry.addBeanDefinition(new BeanDefinition(BMW.class, "bmw"));
        assertTrue(beanRegistry.containBeanDefinition("benz"));
    }

    @Test
    void testGetBeanDefinitionCount() {
        assertEquals(4, beanRegistry.getBeanDefinitionCount());
        beanRegistry.removeBeanDefinitions(Benz.class);
        assertEquals(2, beanRegistry.getBeanDefinitionCount());
        beanRegistry.removeBeanDefinitions(BMW.class);
        assertEquals(0, beanRegistry.getBeanDefinitionCount());
    }

    @Test
    void testRemoveBeanDefinition() {
        beanRegistry.removeBeanDefinition("benz");
        assertFalse(beanRegistry.containBeanDefinition("benz"));
        beanRegistry.removeBeanDefinition("xxx");
        assertFalse(beanRegistry.containBeanDefinition("xxx"));
    }

    @Test
    void testRemoveBeanDefinitions() {
        beanRegistry.removeBeanDefinitions(BMW.class);
        assertFalse(beanRegistry.containBeanDefinition("bmw"));
        beanRegistry.removeBeanDefinitions(Person.class);
        assertFalse(beanRegistry.containBeanDefinition("person"));
    }

    @Test
    void testGetBeanDefinition() {
        assertEquals(Benz.class, beanRegistry.getBeanDefinition("benz").getBeanClass());
        assertThrows(
                MatchedBeanNotUniqueException.class,
                () -> beanRegistry.getBeanDefinition(Benz.class)
        );
        beanRegistry.removeBeanDefinition("site.mahaoran.nanny.test.model.Benz");
        beanRegistry.removeBeanDefinitions(BMW.class);
        assertEquals(Benz.class, beanRegistry.getBeanDefinition(Benz.class).getBeanClass());
        assertEquals(Benz.class, beanRegistry.getBeanDefinition(Car.class).getBeanClass());
        assertEquals(Benz.class, beanRegistry.getBeanDefinition(Car.class, "benz").getBeanClass());
    }
}
