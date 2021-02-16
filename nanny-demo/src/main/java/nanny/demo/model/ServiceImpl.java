package nanny.demo.model;

public class ServiceImpl implements Service {

    private Repository repository;

    public ServiceImpl(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
