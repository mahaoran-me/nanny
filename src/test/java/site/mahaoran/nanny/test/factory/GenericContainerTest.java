package site.mahaoran.nanny.test.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.mahaoran.nanny.beans.BeanLifecycle;
import site.mahaoran.nanny.container.Container;
import site.mahaoran.nanny.container.GenericContainer;
import site.mahaoran.nanny.exception.MatchedBeanNotUniqueException;
import site.mahaoran.nanny.test.model.BMW;
import site.mahaoran.nanny.test.model.Benz;
import site.mahaoran.nanny.test.model.Person;

import static org.junit.jupiter.api.Assertions.*;

public class GenericContainerTest {

    private static Container container;

    @BeforeEach
    void init() {
        container = new GenericContainer();
    }

    @Test
    void testRegisterBean() {
        container.registerBean(new BMW());
        container.registerBean(Benz.class);
        var benz = container.getBean(Benz.class);
        assert benz.getClass().equals(Benz.class);
        container.registerBean(Benz.class, "benz");
        assertThrows(
                MatchedBeanNotUniqueException.class,
                () -> container.getBean(Benz.class)
        );
        container.removeBean("site.mahaoran.nanny.test.model.Benz");
        container.removeBean("benz");

        container.registerBean(Benz.class, "benz", BeanLifecycle.PROTOTYPE);
        var benz1 = container.getBean(Benz.class);
        var benz2 = container.getBean(Benz.class);
        assertNotEquals(benz1, benz2);
        container.removeBean("benz");

        container.registerBean("mahaoran");
        container.registerBean(22);
        container.registerBean(Person.class, "person");
        var person = container.getBean(Person.class, "person");
        assertEquals(person.getCar().getClass(), BMW.class);

        container.removeBean("person");
        container.registerBean(Person.class, "person", () -> new Person("xxxxxx", 22, new Benz()));
        person = container.getBean(Person.class, "person");
        assertEquals(person.getCar().getClass(), Benz.class);
    }

    @Test
    void testSetParent() {
        var parent = new GenericContainer();
        parent.registerBean("benz", new Benz());
        parent.registerBean("mahaoran");
        parent.registerBean(22);
        container.setParent(parent);
        container.registerBean(Person.class, "person");
        var person = container.getBean(Person.class, "person");
        assertEquals("mahaoran", person.getName());
        assertEquals(22, person.getAge());
        assertEquals(Benz.class, person.getCar().getClass());
    }
}
