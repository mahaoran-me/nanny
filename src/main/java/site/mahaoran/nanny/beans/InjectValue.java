package site.mahaoran.nanny.beans;

public class InjectValue {

    private String name;

    private Object value;

    public InjectValue() {}

    public InjectValue(String name) {
        this.name = name;
    }

    public InjectValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }



}
