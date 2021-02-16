package site.mahaoran.nanny.test.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.beans.BeanLifecycle;
import site.mahaoran.nanny.beans.InjectValue;
import site.mahaoran.nanny.exception.*;
import site.mahaoran.nanny.factory.BeanFactory;
import site.mahaoran.nanny.factory.DefaultBeanFactory;
import site.mahaoran.nanny.test.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class BeanFactoryTest {

    private static BeanFactory beanFactory;

    @BeforeAll
    static void init() {
        BeanFactory parentBeanFactory = new DefaultBeanFactory();
        parentBeanFactory.registerBean(new BeanDefinition(Benz.class, "BenzInParent"));
        beanFactory = new DefaultBeanFactory();
        beanFactory.setParent(parentBeanFactory);

    }

    @BeforeEach
    void testRegisterBean() {
        beanFactory.registerBean(new BeanDefinition(Benz.class, "benz"));
        var personDefinition = new BeanDefinition(Person.class, "person");
        personDefinition.getConstructorArguments().put("name", new InjectValue((Object)"mahaoran"));
        personDefinition.getConstructorArguments().put("age", new InjectValue(22));
        beanFactory.registerBean(personDefinition);
    }

    @AfterEach
    void testClear() {
        beanFactory.clear();
    }

    @Test
    void testContainBean() {
        assertTrue(beanFactory.containBean("benz"));
    }

    @Test
    void testGetBean() {
        var xxx = beanFactory.getBean("xxx");
        assertNull(xxx);
        var zzz = beanFactory.getBean(String.class);
        assertNull(zzz);

        var benzByName = beanFactory.getBean("benz");
        var benzByClass = beanFactory.getBean(Benz.class);
        var benzByBoth = beanFactory.getBean(Benz.class, "benz");
        var car = beanFactory.getBean(Car.class);
        assertEquals(benzByName, benzByClass);
        assertEquals(benzByClass, benzByBoth);
        assertEquals(benzByBoth, car);

        var benzOfParent = beanFactory.getBean("BenzInParent");
        assertNotEquals(benzByBoth, benzOfParent);

        var person = beanFactory.getBean(Person.class, "person");
        assertEquals("mahaoran", person.getName());
        assertEquals(benzByBoth, person.getCar());
    }

    @Test
    void testGetBean2() {
        beanFactory.clear();
        beanFactory.registerBean(new BeanDefinition(Benz.class, "benz"));
        var personDefinition = new BeanDefinition(Person.class, "person", BeanLifecycle.PROTOTYPE);
        personDefinition.getConstructorArguments().put("name", new InjectValue((Object)"mahaoran"));
        personDefinition.getConstructorArguments().put("age", new InjectValue(22));
        beanFactory.registerBean(personDefinition);

        var person1 = beanFactory.getBean(Person.class, "person");
        var person2 = beanFactory.getBean(Person.class, "person");
        assertNotEquals(person1, person2);
        assertEquals(person1.getCar(), person2.getCar());
    }

    @Test
    void testRemoveBean() {
        beanFactory.removeBean("benz");
        assertFalse(beanFactory.containBean("benz"));
    }

    @Test
    void testBeanNameHasExistsException() {
        assertThrows(
                BeanNameHasExistsException.class,
                () -> beanFactory.registerBean(new BeanDefinition(Benz.class, "benz"))
        );
    }

    @Test
    void testMatchedBeanNotUniqueException() {
        beanFactory.registerBean(new BeanDefinition(BMW.class, "bmw"));
        assertThrows(
                MatchedBeanNotUniqueException.class,
                () -> beanFactory.getBean(Car.class)
        );
    }

    @Test
    void testCircularDependencyException() {
        beanFactory.registerBean(new BeanDefinition(CircularA.class, "ca"));
        beanFactory.registerBean(new BeanDefinition(CircularB.class, "cb"));
        assertThrows(
                CircularDependencyException.class,
                () -> beanFactory.getBean("ca")
        );

        BeanDefinition ad = new BeanDefinition(A.class, "a");
        ad.getInjectProperties().put("b", new InjectValue("b"));
        BeanDefinition bd = new BeanDefinition(B.class, "b");
        bd.getInjectProperties().put("a", new InjectValue("a"));
        beanFactory.registerBean(ad);
        beanFactory.registerBean(bd);
        var a = beanFactory.getBean(A.class, "a");
        var b = beanFactory.getBean(B.class, "b");
        assertEquals(a, b.getA());
        assertEquals(b, a.getB());
    }

    @Test
    void testNoPublicConstructorException() {
        beanFactory.registerBean(new BeanDefinition(Singleton.class, "singleton"));
        assertThrows(
                NoPublicConstructorException.class,
                () -> beanFactory.getBean("singleton")
        );
    }

    @Test
    void testBeanIsAbstractException() {
        beanFactory.registerBean(new BeanDefinition(AbstractClass.class, "abstract"));
        assertThrows(
                BeanIsAbstractException.class,
                () -> beanFactory.getBean("abstract")
        );
        beanFactory.registerBean(new BeanDefinition(Car.class, "car"));
        assertThrows(
                BeanIsAbstractException.class,
                () -> beanFactory.getBean("car")
        );
    }

    @Test
    void testParameterMissingException() {
        beanFactory.removeBean("benz");
        beanFactory.getParent().removeBean("BenzInParent");
        assertThrows(
                ParameterMissingException.class,
                () -> beanFactory.getBean("person")
        );
    }
}
