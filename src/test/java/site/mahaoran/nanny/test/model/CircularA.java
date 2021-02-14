package site.mahaoran.nanny.test.model;

public class CircularA {

    private CircularB b;

    public CircularA(CircularB b) {
        this.b = b;
    }
}
