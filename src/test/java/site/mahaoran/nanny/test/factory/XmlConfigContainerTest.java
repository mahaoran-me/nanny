package site.mahaoran.nanny.test.factory;

import org.junit.jupiter.api.Test;
import site.mahaoran.nanny.container.Container;
import site.mahaoran.nanny.container.XmlConfigContainer;
import site.mahaoran.nanny.test.model.Benz;
import site.mahaoran.nanny.test.model.Person;

import static org.junit.jupiter.api.Assertions.*;

public class XmlConfigContainerTest {

    @Test
    void testRegisterBeanFromXml() {
        Container container = new XmlConfigContainer("beans.xml");
        var person = container.getBean(Person.class, "person");
        assertEquals("mahaoran", person.getName());
        assertEquals(22, person.getAge());
        assertEquals(Benz.class, person.getCar().getClass());
    }
}
