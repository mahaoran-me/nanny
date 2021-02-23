package nanny.demo.usecase;

import nanny.demo.model.*;
import site.mahaoran.nanny.beans.BeanDefinition;
import site.mahaoran.nanny.beans.BeanLifecycle;
import site.mahaoran.nanny.beans.InjectValue;
import site.mahaoran.nanny.container.Container;
import site.mahaoran.nanny.container.GenericContainer;

public class GenericContainerClient {
    public static void main(String[] args) {
        // 创建一个通用IOC容器
        Container container = new GenericContainer();
        // 向容器中注册一个Repository的实现类，命名为"repository"。
        // 本次注册方式只需提供类型和名称就行，生命周期默认singleton。
        container.registerBean(RepositoryImpl.class, "repositoryBean");

        // 手动构造一个bean定义，指定类型和名称以及构造器参数。
        var serviceDefinition = new BeanDefinition(ServiceImpl.class, "serviceBean", BeanLifecycle.SINGLETON);
        // 指定构造器参数“repository”将被注入名为“repositoryBean”的bean实例。
        serviceDefinition.getConstructorArguments().put("repository", new InjectValue("repositoryBean"));
        // 将该bean定义注册到容器
        container.registerBean(serviceDefinition);

        // 手动构造一个Controller类型的bean定义，指定名称为“controllerBean”，默认生命周期为singleton。
        var controllerDefinition = new BeanDefinition(Controller.class, "controllerBean");
        // 指定属性“service”将被注入名为“serviceBean”的bean实例。
        controllerDefinition.getInjectProperties().put("service", new InjectValue("serviceBean"));
        // 将该bean定义注册到容器
        container.registerBean(controllerDefinition);

        // 测试是否成功构造依赖关系

        // 先分别从容器中取出各个实例
        Repository repository = container.getBean(Repository.class, "repositoryBean");
        Service service = container.getBean(Service.class, "serviceBean");
        Controller controller = container.getBean(Controller.class, "controllerBean");

        // 然后验证依赖是否注入正确
        assert service.getRepository() == repository;
        assert controller.getService() == service;
    }
}
