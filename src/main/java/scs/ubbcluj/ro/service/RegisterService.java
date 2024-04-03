package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.UserDBLoginRepository;

public class RegisterService {
    private final UserDBLoginRepository repository;

    public RegisterService(UserDBLoginRepository  repository) {
        this.repository = repository;
    }

    public void register(String username, String password, String name) {
        User user = new User(name);
        repository.save(user, username, password);
    }

    public User getOne(String username, String password)
    {
        return repository.findOne(username, password).orElse(null);
    }
}
