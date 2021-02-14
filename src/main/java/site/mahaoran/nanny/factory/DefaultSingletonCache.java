package site.mahaoran.nanny.factory;

import site.mahaoran.nanny.exception.CircularDependencyException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DefaultSingletonCache implements SingletonCache {

    private final Map<String, Object> wholeSingletons = new HashMap<>();
    private final Map<String, Object> earlySingletons = new HashMap<>();
    private final Map<String, Supplier<?>> objectSuppliers = new HashMap<>();
    private final Set<String> creatingSingletons = new HashSet<>();

    @Override
    public void addWholeSingleton(String name, Object object) {
        wholeSingletons.put(name, object);
        earlySingletons.remove(name);
        objectSuppliers.remove(name);
        creatingSingletons.remove(name);
    }

    @Override
    public void addEarlySingleton(String name, Object object) {
        if (wholeSingletons.containsKey(name)) return;
        earlySingletons.put(name, object);
        objectSuppliers.remove(name);
        creatingSingletons.add(name);
    }

    @Override
    public void addSingletonSupplier(String name, Supplier<?> objectSupplier) {
        if (wholeSingletons.containsKey(name)) return;
        if (earlySingletons.containsKey(name)) return;
        objectSuppliers.put(name, objectSupplier);
        creatingSingletons.add(name);
    }

    @Override
    public void removeSingleton(String name) {
        wholeSingletons.remove(name);
        earlySingletons.remove(name);
        objectSuppliers.remove(name);
        creatingSingletons.remove(name);
    }

    @Override
    public Object getSingleton(String name) {
        var object = wholeSingletons.get(name);
        if (object == null && isCreating(name)) {
            object = earlySingletons.get(name);
            if (object == null) {
                Supplier<?> supplier = objectSuppliers.get(name);
                if (supplier != null) {
                    object = supplier.get();
                    addEarlySingleton(name, object);
                }
            }
        }
        return object;
    }

    @Override
    public Object getSingleton(String name, Supplier<?> singletonSupplier) {
        var object = wholeSingletons.get(name);
        if (object == null) {
            if (isCreating(name)) {
                throw new CircularDependencyException(name);
            }
            creatingSingletons.add(name);
            object = singletonSupplier.get();
            creatingSingletons.remove(name);
        }
        return object;
    }

    @Override
    public void clearSingleton() {
        wholeSingletons.clear();
        earlySingletons.clear();
        objectSuppliers.clear();
        creatingSingletons.clear();
    }

    @Override
    public boolean isCreating(String name) {
        return creatingSingletons.contains(name);
    }
}
