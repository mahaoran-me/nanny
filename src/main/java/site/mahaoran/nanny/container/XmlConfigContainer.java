package site.mahaoran.nanny.container;

import site.mahaoran.nanny.util.XmlBeanResolver;

public class XmlConfigContainer extends GenericContainer{

    public XmlConfigContainer(String fileName) {
        registerBeanFromXml(fileName);
    }

    private void registerBeanFromXml(String fileName) {
        var resource = XmlConfigContainer.class.getClassLoader().getResource(fileName);
        assert resource != null;
        var beanDefinitions = XmlBeanResolver.readBeansFromXmlFile(resource.getFile());
        for (var definition : beanDefinitions) {
            registerBean(definition);
        }
    }
}
