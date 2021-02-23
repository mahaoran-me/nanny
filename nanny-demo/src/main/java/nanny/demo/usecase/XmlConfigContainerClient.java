package nanny.demo.usecase;

import nanny.demo.model.*;
import site.mahaoran.nanny.container.Container;
import site.mahaoran.nanny.container.XmlConfigContainer;

public class XmlConfigContainerClient {
    public static void main(String[] args) {
        // 通过xml配置文件初始化容器
        Container container = new XmlConfigContainer("beans.xml");

        // 测试是否成功构造依赖关系

        // 先分别从容器中取出各个实例
        var repository = container.getBean(Repository.class, "repository");
        var service = container.getBean(Service.class, "service");
        var controller = container.getBean(Controller.class, "controller");

        // 然后验证依赖是否注入正确
        assert service.getRepository() == repository;
        assert controller.getService() == service;

        // 具有循环依赖关系的实例也能成功获取
        var student = container.getBean(Student.class, "student");
        var teacher = container.getBean(Teacher.class, "teacher");
        assert student.getTeacher() == teacher;
        assert teacher.getStudent() == student;


    }
}
