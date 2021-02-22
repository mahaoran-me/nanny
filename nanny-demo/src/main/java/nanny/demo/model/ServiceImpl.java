package nanny.demo.model;

public class ServiceImpl implements Service {

    private Repository repository;

    public ServiceImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
