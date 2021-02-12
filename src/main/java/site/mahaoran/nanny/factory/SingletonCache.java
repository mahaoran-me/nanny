package site.mahaoran.nanny.factory;

import java.util.function.Supplier;

/**
 * 单例缓存容器，用来存储工厂中创建过的单例对象。
 *
 * @author mahaoran
 * @since 2021-01-19
 */
public interface SingletonCache {

    /**
     * 向一级缓存中添加一个实例对象，该实例对象是一个完整的可用的对象。
     * @param name bean名称
     * @param object bean实例
     */
    void addWholeSingleton(String name, Object object);

    /**
     * 向二级缓存中添加一个实例对象，该实例对象是一个不完整的对象，只有实例化没有初始化。
     * @param name bean名称
     * @param object bean实例
     */
    void addEarlySingleton(String name, Object object);

    /**
     * 向三级缓存中添加一个supplier，该supplier表示提供经过特殊处理的新的实例对象，
     * 该对象只完成了实例化，没有初始化。
     * @param name bean名称
     * @param objectSupplier bean提供程序
     */
    void addSingletonSupplier(String name, Supplier<?> objectSupplier);

    /**
     * 从所有缓存中移除指定的实例对象。
     * @param name bean名称
     */
    void removeSingleton(String name);

    /**
     * 从缓存中获取指定的对象
     * @param name bean名称
     * @return bean实例
     */
    Object getSingleton(String name);

    /**
     * 从缓存中获取指定的对象，若对象不存在，使用提供程序生成实例然后返回。
     * @param name bean名称
     * @param singletonSupplier 实例提供程序
     * @return bean实例
     */
    Object getSingleton(String name, Supplier<?> singletonSupplier);

    /**
     * 清理缓存
     */
    void clearSingleton();

    /**
     * 判断指定实例是否正在创建过程中
     * @param name bean名称
     * @return 是否正在创建
     */
    boolean isCreating(String name);
}
