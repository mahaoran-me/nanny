package site.mahaoran.nanny.factory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SingletonCache {

    private final Map<String, Object> singletonObjects = new HashMap<>();

    private final Map<String, Object> earlySingletonObjects = new HashMap<>();

    private final Map<String, Supplier<?>> singletonSuppliers = new HashMap<>();

    private final Set<String> creatingSingletonObjects = new LinkedHashSet<>();

    private final Set<String> createdSingletonObjects = new LinkedHashSet<>();


    public void addSingletonObject(String beanName, Object singletonObject) {
        this.singletonObjects.put(beanName, singletonObject);
        this.earlySingletonObjects.remove(beanName);
        this.singletonSuppliers.remove(beanName);
        this.createdSingletonObjects.add(beanName);
    }

    public void addEarlySingletonObject(String beanName, Object earlySingletonObject) {
        if (singletonObjects.containsKey(beanName)) return;
        this.earlySingletonObjects.put(beanName, earlySingletonObject);
        this.singletonSuppliers.remove(beanName);
        this.createdSingletonObjects.add(beanName);
    }

    public void addSingletonSupplier(String beanName, Supplier<?> singletonSupplier) {
        if (singletonObjects.containsKey(beanName)) return;
        if (earlySingletonObjects.containsKey(beanName)) return;
        this.singletonSuppliers.put(beanName, singletonSupplier);
        this.createdSingletonObjects.add(beanName);
    }

    public void removeSingleton(String beanName) {
        this.singletonObjects.remove(beanName);
        this.earlySingletonObjects.remove(beanName);
        this.singletonSuppliers.remove(beanName);
        this.createdSingletonObjects.remove(beanName);
    }

    public Object getSingleton(String beanName) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCreating(beanName)) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
                Supplier<?> singletonSupplier = this.singletonSuppliers.get(beanName);
                if (singletonSupplier != null) {
                    singletonObject = singletonSupplier.get();
                    addEarlySingletonObject(beanName, singletonObject);
                }
            }
        }
        return singletonObject;
    }

    public Object getSingleton(String beanName, Supplier<?> singletonSupplier) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            if (isSingletonCreating(beanName)) {
                throw new RuntimeException("实例已经在创建了，请不要重复创建。");
            }
            this.creatingSingletonObjects.add(beanName);
            try {
                singletonObject = singletonSupplier.get();
            } catch (Exception e) {
                throw new RuntimeException("创建实例失败");
            }
            this.creatingSingletonObjects.remove(beanName);
            if (singletonObject != null) {
                addSingletonObject(beanName, singletonObject);
            }
        }
        return singletonObject;
    }

    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    public String[] getSingletonNames() {
        return this.createdSingletonObjects.toArray(new String[0]);
    }

    public int getSingletonCount() {
        return createdSingletonObjects.size();
    }

    public void clearSingletonCache() {
        this.singletonObjects.clear();
        this.earlySingletonObjects.clear();
        this.singletonSuppliers.clear();
        this.creatingSingletonObjects.clear();
        this.createdSingletonObjects.clear();
    }

    private boolean isSingletonCreating(String beanName) {
        return creatingSingletonObjects.contains(beanName);
    }
}
