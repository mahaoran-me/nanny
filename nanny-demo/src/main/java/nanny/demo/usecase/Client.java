package nanny.demo.usecase;

import nanny.demo.model.Controller;
import site.mahaoran.nanny.container.Container;
import site.mahaoran.nanny.container.XmlConfigContainer;

public class Client {
    public static void main(String[] args) {
        Container container = new XmlConfigContainer("beans.xml");
        var controller = container.getBean(Controller.class, "controller");
    }
}
