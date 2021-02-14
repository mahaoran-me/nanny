package site.mahaoran.nanny.test.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import site.mahaoran.nanny.factory.DefaultSingletonCache;
import site.mahaoran.nanny.factory.SingletonCache;
import site.mahaoran.nanny.test.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class SingletonCacheTest {

    private SingletonCache singletonCache;

    @BeforeEach
    void init() {
        singletonCache = new DefaultSingletonCache();
    }

    @Test
    void testAddWholeSingleton() {
        singletonCache.addWholeSingleton("benz", new Benz());
        assertEquals(Benz.class, singletonCache.getSingleton("benz").getClass());
    }

    @Test
    void testAddEarlySingleton() {
        singletonCache.addEarlySingleton("bmw", new BMW());
        assertEquals(BMW.class, singletonCache.getSingleton("bmw").getClass());
    }

    @Test
    void testAddSingletonSupplier() {
        singletonCache.addSingletonSupplier("bmw", BMW::new);
        assertEquals(BMW.class, singletonCache.getSingleton("bmw").getClass());
    }

    @Test
    void testRemoveSingleton() {
        singletonCache.addSingletonSupplier("benz", Benz::new);
        singletonCache.removeSingleton("benz");
        assertNull(singletonCache.getSingleton("benz"));

        singletonCache.addEarlySingleton("benz", new Benz());
        singletonCache.removeSingleton("benz");
        assertNull(singletonCache.getSingleton("benz"));

        singletonCache.addWholeSingleton("benz", new Benz());
        singletonCache.removeSingleton("benz");
        assertNull(singletonCache.getSingleton("benz"));

        singletonCache.addSingletonSupplier("benz", Benz::new);
        singletonCache.addEarlySingleton("benz", new Benz());
        singletonCache.addWholeSingleton("benz", new Benz());
        singletonCache.removeSingleton("benz");
        assertNull(singletonCache.getSingleton("benz"));
    }

    @Test
    void testClearSingleton() {
        singletonCache.addSingletonSupplier("benz", Benz::new);
        singletonCache.addEarlySingleton("benz", new Benz());
        singletonCache.addWholeSingleton("benz", new Benz());

        singletonCache.addSingletonSupplier("bmw", BMW::new);
        singletonCache.addEarlySingleton("bmw", new BMW());
        singletonCache.addWholeSingleton("bmw", new BMW());

        singletonCache.clearSingleton();

        assertNull(singletonCache.getSingleton("benz"));
        assertNull(singletonCache.getSingleton("bmw"));
    }
}
