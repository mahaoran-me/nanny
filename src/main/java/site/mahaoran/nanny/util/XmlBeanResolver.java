package site.mahaoran.nanny.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.beans.BeanLifecycle;
import site.mahaoran.nanny.beans.InjectValue;
import site.mahaoran.nanny.exception.BeanResolveException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlBeanResolver {

    public static BeanDefinition[] readBeansFromXmlFile(String fileName) {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            var documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new File(fileName));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        assert document != null;
        var beanList = document.getElementsByTagName("bean");
        var beanDefinitions = new BeanDefinition[beanList.getLength()];
        for (var i = 0; i < beanDefinitions.length; i++) {
            beanDefinitions[i] = resolverBean(beanList.item(i));
        }
        return beanDefinitions;
    }

    private static BeanDefinition resolverBean(Node beanNode) {
        var attributes = beanNode.getAttributes();
        String beanName = null;
        String beanClassName = null;
        String lifecycle = "singleton";
        for (var i = 0; i < attributes.getLength(); i++) {
            var item = attributes.item(i);
            if ("name".equals(item.getNodeName())) {
                beanName = item.getNodeValue();
            } else if ("class".equals(item.getNodeName())) {
                beanClassName = item.getNodeValue();
            } else if ("lifecycle".equals(item.getNodeName())) {
                lifecycle = item.getNodeValue();
            }
        }
        if (beanName == null || "".equals(beanName)) {
            throw new BeanResolveException("<bean/>中name属性不能为空");
        }
        if (beanClassName == null || "".equals(beanClassName)) {
            throw new BeanResolveException("<bean/>中class属性不能为空");
        }
        BeanLifecycle beanLifecycle;
        if ("singleton".equals(lifecycle)) {
            beanLifecycle = BeanLifecycle.SINGLETON;
        } else if ("prototype".equals(lifecycle)) {
            beanLifecycle = BeanLifecycle.PROTOTYPE;
        } else {
            throw new BeanResolveException("<bean/>中lifecycle属性只能为'singleton'或'prototype'");
        }
        Class<?> beanClass = null;
        try {
            beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        var beanDefinition = new BeanDefinition(beanClass, beanName, beanLifecycle);
        var childNodes = beanNode.getChildNodes();
        for (var i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == Node.TEXT_NODE) continue;

            var item = childNodes.item(i);
            var injectValue = new InjectValue();

            var attrs = item.getAttributes();
            var nameNode = attrs.getNamedItem("name");
            if (nameNode == null || "".equals(nameNode.getNodeValue())) {
                throw new BeanResolveException("<argument/>中name属性不能为空");
            }
            var valueNode = attrs.getNamedItem("value");
            if (valueNode != null) {
                var typeNode = attrs.getNamedItem("type");
                if (typeNode == null) {
                    injectValue = new InjectValue(valueOfType(valueNode.getNodeValue(), "string"));
                } else {
                    injectValue = new InjectValue(valueOfType(valueNode.getNodeValue(), typeNode.getNodeValue()));
                }
            }
            var reference = attrs.getNamedItem("reference");
            if (reference != null) {
                injectValue = new InjectValue(reference.getNodeValue());
            }

            if ("argument".equals(item.getNodeName())) {
                beanDefinition.getConstructorArguments().put(nameNode.getNodeValue(), injectValue);
            }
            if ("property".equals(item.getNodeName())) {
                beanDefinition.getInjectProperties().put(nameNode.getNodeValue(), injectValue);
            }
        }
        return beanDefinition;
    }

    private static Object valueOfType(String value, String type) {
        switch (type) {
            case "byte": return Byte.parseByte(value);
            case "short": return Short.parseShort(value);
            case "int": return Integer.parseInt(value);
            case "long": return Long.parseLong(value);
            case "float": return Float.parseFloat(value);
            case "double": return Double.parseDouble(value);
            case "char": return value.charAt(0);
            case "bool": return Boolean.parseBoolean(value);
            case "string": return value;
            default: throw new BeanResolveException("type: '" + type + "' 不合法");
        }
    }
}
