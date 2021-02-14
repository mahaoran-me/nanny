package site.mahaoran.nanny.test.model;

public class Singleton {

    private static volatile Singleton singleton;

    private Singleton() {}

    public Singleton getSingleton() {
        if (singleton == null) {
            synchronized (this) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
